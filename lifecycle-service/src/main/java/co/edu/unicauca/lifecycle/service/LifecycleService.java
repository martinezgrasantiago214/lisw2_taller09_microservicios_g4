package co.edu.unicauca.lifecycle.service;

import co.edu.unicauca.lifecycle.dto.OperationResultDTO;
import co.edu.unicauca.lifecycle.dto.QuestionStateDTO;
import co.edu.unicauca.lifecycle.dto.StateHistoryDTO;
import co.edu.unicauca.lifecycle.dto.TransitionRequest;
import co.edu.unicauca.lifecycle.exception.ApiException;
import co.edu.unicauca.lifecycle.exception.ConflictException;
import co.edu.unicauca.lifecycle.exception.NotFoundException;
import co.edu.unicauca.lifecycle.messaging.messages.*;
import co.edu.unicauca.lifecycle.model.*;
import co.edu.unicauca.lifecycle.repository.OperationResultRepository;
import co.edu.unicauca.lifecycle.repository.QuestionStateRepository;
import co.edu.unicauca.lifecycle.repository.StateHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * Único componente del sistema que modifica el estado de una pregunta. Los
 * eventos se publican con ApplicationEventPublisher y salen a RabbitMQ solo
 * después del commit (ver LifecycleEventPublisher).
 */
@Service
public class LifecycleService {

    private static final Logger log = LoggerFactory.getLogger(LifecycleService.class);
    public static final String SUBMIT_OPERATION = "SUBMIT_FOR_REVIEW";

    private final QuestionStateRepository stateRepository;
    private final StateHistoryRepository historyRepository;
    private final OperationResultRepository operationRepository;
    private final ApplicationEventPublisher events;

    public LifecycleService(QuestionStateRepository stateRepository, StateHistoryRepository historyRepository,
                            OperationResultRepository operationRepository, ApplicationEventPublisher events) {
        this.stateRepository = stateRepository;
        this.historyRepository = historyRepository;
        this.operationRepository = operationRepository;
        this.events = events;
    }

    /** Evento QuestionCreated: registra el estado inicial DRAFT (idempotente). */
    @Transactional
    public void register(QuestionCreated event) {
        if (stateRepository.existsByQuestionId(event.questionId())) {
            log.info("[LifecycleService] Pregunta {} ya registrada; evento duplicado ignorado", event.questionId());
            return;
        }
        String author = "user:" + event.authorId();
        stateRepository.save(new QuestionState(event.questionId(), author));
        historyRepository.save(new StateHistory(event.questionId(), null, QuestionStatus.DRAFT, author,
                "Pregunta registrada"));
        log.info("[LifecycleService] Pregunta {} registrada en DRAFT", event.questionId());
    }

    /**
     * Punto 6 del escenario: procesa el comando SubmitQuestionForReview.
     * Los fallos de negocio no se reintentan: se registran como FAILED para que
     * el cliente los consulte; nunca se informa falsamente que la transición terminó.
     */
    @Transactional
    public OperationResult processSubmit(SubmitQuestionForReview command) {
        if (operationRepository.existsById(command.operationId())) {
            log.info("[LifecycleService] Comando {} ya procesado; se ignora (idempotencia)", command.operationId());
            return operationRepository.findById(command.operationId()).orElseThrow();
        }
        String actor = "user:" + command.requestedBy();
        QuestionState state = stateRepository.findByQuestionId(command.questionId()).orElse(null);

        OperationResult result;
        if (state == null) {
            result = failed(command, "La pregunta " + command.questionId() + " no está registrada en el ciclo de vida");
        } else if (!state.canTransitionTo(QuestionStatus.PENDING_REVIEW)) {
            result = failed(command, "Transición no permitida: " + state.getCurrentState() + " -> PENDING_REVIEW");
        } else {
            QuestionStatus from = state.getCurrentState();
            applyAndRecord(state, QuestionStatus.PENDING_REVIEW, actor, "Enviada a revisión");
            result = new OperationResult(command.operationId(), command.questionId(), SUBMIT_OPERATION,
                    OperationStatus.COMPLETED, "Transición " + from + " -> PENDING_REVIEW completada",
                    command.requestedAt());
            events.publishEvent(new QuestionSubmittedForReview(command.operationId(), command.questionId(),
                    QuestionStatus.PENDING_REVIEW.name(), LocalDateTime.now()));
        }
        operationRepository.save(result);
        events.publishEvent(new OperationResultMessage(result.getId(), result.getQuestionId(), result.getType(),
                result.getStatus().name(), result.getMessage(),
                state == null ? null : state.getCurrentState().name(), LocalDateTime.now()));
        log.info("[LifecycleService] Operación {} -> {}: {}", result.getId(), result.getStatus(), result.getMessage());
        return result;
    }

    /**
     * Registra como FAILED un comando que no pudo procesarse por un error técnico tras
     * agotar los reintentos (lo invoca el MessageRecoverer). La transacción del intento
     * fallido ya se revirtió, por lo que el estado de la pregunta no cambió: FAILED es veraz.
     */
    @Transactional
    public void recordTechnicalFailure(SubmitQuestionForReview command, String reason) {
        if (operationRepository.existsById(command.operationId())) {
            return; // ya tiene un resultado registrado
        }
        OperationResult result = failed(command, "Error técnico al procesar la transición: " + reason);
        operationRepository.save(result);
        String currentState = stateRepository.findByQuestionId(command.questionId())
                .map(s -> s.getCurrentState().name()).orElse(null);
        events.publishEvent(new OperationResultMessage(result.getId(), result.getQuestionId(), result.getType(),
                result.getStatus().name(), result.getMessage(), currentState, LocalDateTime.now()));
        log.warn("[LifecycleService] Operación {} registrada FAILED por error técnico: {}", result.getId(), reason);
    }

    /** Transición síncrona solicitada por REST (Review Service, Question Service o un administrador). */
    @Transactional
    public QuestionStateDTO requestTransition(Long questionId, TransitionRequest request) {
        QuestionStatus target = parse(request.targetState());
        if (target == QuestionStatus.REJECTED && (request.comment() == null || request.comment().isBlank())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Para rechazar una pregunta debe registrar el motivo (comment)");
        }
        QuestionState state = findState(questionId);
        if (!state.canTransitionTo(target)) {
            throw new ConflictException("Transición no permitida: " + state.getCurrentState() + " -> " + target
                    + ". Permitidas desde " + state.getCurrentState() + ": " + state.getCurrentState().allowedTransitions());
        }
        String actor = request.changedBy() == null || request.changedBy().isBlank() ? "system" : request.changedBy();
        applyAndRecord(state, target, actor, request.comment());
        return QuestionStateDTO.from(state);
    }

    @Transactional(readOnly = true)
    public QuestionStateDTO getState(Long questionId) {
        return QuestionStateDTO.from(findState(questionId));
    }

    @Transactional(readOnly = true)
    public List<StateHistoryDTO> history(Long questionId) {
        findState(questionId);
        return historyRepository.findByQuestionIdOrderByIdAsc(questionId).stream().map(StateHistoryDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public OperationResultDTO getOperation(String operationId) {
        return operationRepository.findById(operationId).map(OperationResultDTO::from)
                .orElseThrow(() -> new NotFoundException("No hay resultado registrado para la operación " + operationId
                        + " (puede seguir en cola)"));
    }

    // ---------------------------------------------------------------- helpers

    private void applyAndRecord(QuestionState state, QuestionStatus target, String actor, String comment) {
        QuestionStatus from = state.getCurrentState();
        state.applyTransition(target, actor, comment);
        historyRepository.save(new StateHistory(state.getQuestionId(), from, target, actor, comment));
        events.publishEvent(new QuestionStateChanged(state.getQuestionId(), from.name(), target.name(), actor,
                comment, LocalDateTime.now()));
        log.info("[LifecycleService] Pregunta {}: {} -> {} por {}", state.getQuestionId(), from, target, actor);
    }

    private OperationResult failed(SubmitQuestionForReview command, String message) {
        return new OperationResult(command.operationId(), command.questionId(), SUBMIT_OPERATION,
                OperationStatus.FAILED, message, command.requestedAt());
    }

    private QuestionState findState(Long questionId) {
        return stateRepository.findByQuestionId(questionId)
                .orElseThrow(() -> new NotFoundException("La pregunta " + questionId + " no está registrada en el ciclo de vida"));
    }

    private static QuestionStatus parse(String value) {
        try {
            return QuestionStatus.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Estado desconocido: " + value
                    + ". Valores válidos: " + List.of(QuestionStatus.values()));
        }
    }
}
