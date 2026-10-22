package co.edu.unicauca.question.service;

import co.edu.unicauca.question.client.*;
import co.edu.unicauca.question.dto.*;
import co.edu.unicauca.question.exception.*;
import co.edu.unicauca.question.messaging.QuestionEventPublisher;
import co.edu.unicauca.question.messaging.messages.SubmitQuestionForReview;
import co.edu.unicauca.question.model.Operation;
import co.edu.unicauca.question.model.OperationStatus;
import co.edu.unicauca.question.model.Option;
import co.edu.unicauca.question.model.Question;
import co.edu.unicauca.question.repository.OperationRepository;
import co.edu.unicauca.question.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);
    private static final Set<String> EDITABLE_STATES = Set.of("DRAFT", "REJECTED");
    public static final String SUBMIT_OPERATION = "SUBMIT_FOR_REVIEW";

    private final QuestionRepository questionRepository;
    private final OperationRepository operationRepository;
    private final UserClient userClient;
    private final ValidationClient validationClient;
    private final LifecycleClient lifecycleClient;
    private final QuestionEventPublisher publisher;

    public QuestionService(QuestionRepository questionRepository, OperationRepository operationRepository,
                           UserClient userClient, ValidationClient validationClient,
                           LifecycleClient lifecycleClient, QuestionEventPublisher publisher) {
        this.questionRepository = questionRepository;
        this.operationRepository = operationRepository;
        this.userClient = userClient;
        this.validationClient = validationClient;
        this.lifecycleClient = lifecycleClient;
        this.publisher = publisher;
    }

    /**
     * Pasos 2 y 3 del escenario: autenticación y validación síncronas. Si el
     * Validation Service rechaza la pregunta se responde 400 y no se persiste.
     * La transacción incluye la publicación de QuestionCreated: si RabbitMQ no
     * está disponible se hace rollback y se responde 503 (no queda una pregunta
     * que el Lifecycle Service desconozca).
     */
    @Transactional
    public CreateQuestionResponse create(String authorization, CreateQuestionRequest request) {
        AuthenticatedUser author = requireAuthor(authorization);
        validateOrFail(request, null, author.id());

        Question question = questionRepository.save(
                Question.create(request.text().trim(), toOptions(request), request.correctOptionId().trim(), author.id()));
        publisher.publishQuestionCreated(question.getId(), author.id());
        log.info("[QuestionService] Pregunta {} registrada en DRAFT por {}", question.getId(), author.username());
        return new CreateQuestionResponse(question.getId(), question.getStatus(), "Pregunta registrada en estado DRAFT");
    }

    /**
     * Pasos 4 y 5: publica el comando y responde de inmediato 202 Accepted con el
     * operationId. No espera a que el Lifecycle Service procese la transición.
     */
    public SubmitAcceptedResponse submitForReview(String authorization, Long questionId) {
        AuthenticatedUser user = userClient.authenticate(authorization);
        Question question = findQuestion(questionId);
        requireOwnerOrAdmin(user, question);

        String operationId = UUID.randomUUID().toString();
        Operation operation = operationRepository.save(new Operation(operationId, questionId, SUBMIT_OPERATION));
        try {
            publisher.publishSubmitForReview(
                    new SubmitQuestionForReview(operationId, questionId, user.id(), LocalDateTime.now()));
        } catch (AmqpException ex) {
            operation.complete(OperationStatus.FAILED, "No fue posible encolar la solicitud: broker no disponible", null);
            operationRepository.save(operation);
            throw new ServiceUnavailableException(
                    "RabbitMQ no está disponible; la solicitud no fue aceptada (operationId " + operationId + ")");
        }
        return new SubmitAcceptedResponse("Solicitud de envío a revisión aceptada para procesamiento.",
                operationId, OperationStatus.PROCESSING.name(), "/operations/" + operationId);
    }

    /**
     * Corrección del autor. Solo se permite en DRAFT o REJECTED (lo decide el
     * Lifecycle Service). Si la pregunta estaba REJECTED se solicita la
     * transición REJECTED -> DRAFT para que pueda volver al proceso de revisión.
     */
    @Transactional
    public QuestionDTO update(String authorization, Long questionId, CreateQuestionRequest request) {
        AuthenticatedUser user = userClient.authenticate(authorization);
        Question question = findQuestion(questionId);
        requireOwnerOrAdmin(user, question);

        LifecycleState current = lifecycleClient.getState(questionId);
        if (!EDITABLE_STATES.contains(current.state())) {
            throw new ConflictException("Solo se puede corregir una pregunta en DRAFT o REJECTED (estado actual: "
                    + current.state() + ")");
        }
        validateOrFail(request, questionId, user.id());

        if ("REJECTED".equals(current.state())) {
            lifecycleClient.requestTransition(questionId, "DRAFT", user.username(),
                    "Corrección del autor tras el rechazo");
        }
        question.updateContent(request.text().trim(), toOptions(request), request.correctOptionId().trim());
        question.updateStatus("DRAFT");
        return QuestionDTO.from(question);
    }

    @Transactional(readOnly = true)
    public QuestionDTO findById(Long id) {
        return QuestionDTO.from(findQuestion(id));
    }

    @Transactional(readOnly = true)
    public List<QuestionDTO> findAll(Long authorId) {
        List<Question> questions = authorId == null ? questionRepository.findAll()
                : questionRepository.findByAuthorId(authorId);
        return questions.stream().map(QuestionDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public OperationDTO findOperation(String operationId) {
        return operationRepository.findById(operationId).map(OperationDTO::from)
                .orElseThrow(() -> new NotFoundException("No existe la operación " + operationId));
    }

    // ---------------------------------------------------------------- helpers

    private void validateOrFail(CreateQuestionRequest request, Long questionId, Long authorId) {
        ValidationResult result = validationClient.validate(request);
        if (!result.valid()) {
            publisher.publishValidationFailedSafely(questionId, authorId, result.errors());
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "La pregunta no superó la validación y no se registró como válida", result.errors());
        }
    }

    private AuthenticatedUser requireAuthor(String authorization) {
        AuthenticatedUser user = userClient.authenticate(authorization);
        if (!user.hasAnyRole("AUTHOR", "TEACHER")) {
            throw new ForbiddenException("Solo un docente o autor puede crear preguntas");
        }
        return user;
    }

    private void requireOwnerOrAdmin(AuthenticatedUser user, Question question) {
        if (!question.getAuthorId().equals(user.id()) && !user.hasRole("ADMIN")) {
            throw new ForbiddenException("Solo el autor de la pregunta puede realizar esta acción");
        }
    }

    private Question findQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe la pregunta con id " + id));
    }

    private static List<Option> toOptions(CreateQuestionRequest request) {
        return request.options().stream().map(o -> new Option(o.id().trim(), o.text().trim())).toList();
    }
}
