package co.edu.unicauca.question.messaging;

import co.edu.unicauca.question.config.RabbitMQConfig;
import co.edu.unicauca.question.messaging.messages.QuestionCreated;
import co.edu.unicauca.question.messaging.messages.SubmitQuestionForReview;
import co.edu.unicauca.question.messaging.messages.ValidationFailed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/** Publicación de comandos y eventos con RabbitTemplate. */
@Component
public class QuestionEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(QuestionEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public QuestionEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /** Lanza AmqpException si el broker no está disponible (el llamador decide cómo compensar). */
    public void publishQuestionCreated(Long questionId, Long authorId) {
        QuestionCreated event = new QuestionCreated(questionId, authorId, LocalDateTime.now());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.RK_QUESTION_CREATED, event);
        log.info("[QuestionService] Publicado QuestionCreated questionId={}", questionId);
    }

    public void publishSubmitForReview(SubmitQuestionForReview command) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.RK_SUBMIT, command);
        log.info("[QuestionService] Publicado comando SubmitQuestionForReview questionId={} operationId={}",
                command.questionId(), command.operationId());
    }

    /** Evento informativo: si falla la publicación solo se registra, no afecta la respuesta 400. */
    public void publishValidationFailedSafely(Long questionId, Long authorId, List<String> errors) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.RK_VALIDATION_FAILED,
                    new ValidationFailed(null, questionId, authorId, errors, LocalDateTime.now()));
        } catch (AmqpException ex) {
            log.warn("[QuestionService] No se pudo publicar ValidationFailed: {}", ex.getMessage());
        }
    }
}
