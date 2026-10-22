package co.edu.unicauca.review.messaging;

import co.edu.unicauca.review.config.RabbitMQConfig;
import co.edu.unicauca.review.messaging.messages.QuestionReviewed;
import co.edu.unicauca.review.messaging.messages.ReviewerAssigned;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publicación de eventos de revisión. Es "best effort": si RabbitMQ falla la
 * operación principal ya quedó confirmada y no se revierte (la notificación no
 * debe bloquear la asignación). Devuelve si el evento quedó encolado.
 */
@Component
public class ReviewEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ReviewEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public ReviewEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public boolean publishReviewerAssigned(ReviewerAssigned event) {
        return send(RabbitMQConfig.RK_REVIEWER_ASSIGNED, event);
    }

    public boolean publishQuestionReviewed(QuestionReviewed event) {
        return send(RabbitMQConfig.RK_QUESTION_REVIEWED, event);
    }

    private boolean send(String routingKey, Object event) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, routingKey, event);
            log.info("[ReviewService] Publicado {} ({})", event.getClass().getSimpleName(), routingKey);
            return true;
        } catch (AmqpException ex) {
            log.error("[ReviewService] No se pudo publicar {}: {}", event.getClass().getSimpleName(), ex.getMessage());
            return false;
        }
    }
}
