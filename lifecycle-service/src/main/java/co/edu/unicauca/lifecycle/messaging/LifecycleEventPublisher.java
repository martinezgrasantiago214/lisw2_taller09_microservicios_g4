package co.edu.unicauca.lifecycle.messaging;

import co.edu.unicauca.lifecycle.config.RabbitMQConfig;
import co.edu.unicauca.lifecycle.messaging.messages.OperationResultMessage;
import co.edu.unicauca.lifecycle.messaging.messages.QuestionStateChanged;
import co.edu.unicauca.lifecycle.messaging.messages.QuestionSubmittedForReview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Envía a RabbitMQ los eventos del dominio una vez confirmada la transacción:
 * nunca se anuncia un cambio de estado que luego se revierte.
 */
@Component
public class LifecycleEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LifecycleEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public LifecycleEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(OperationResultMessage message) {
        send(RabbitMQConfig.RK_OPERATION_RESULT, message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(QuestionStateChanged message) {
        send(RabbitMQConfig.RK_STATE_CHANGED, message);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void on(QuestionSubmittedForReview message) {
        send(RabbitMQConfig.RK_QUESTION_SUBMITTED, message);
    }

    private void send(String routingKey, Object message) {
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, routingKey, message);
            log.info("[LifecycleService] Publicado {} ({})", message.getClass().getSimpleName(), routingKey);
        } catch (AmqpException ex) {
            // El estado ya quedó confirmado; el resultado sigue consultable por REST.
            log.error("[LifecycleService] No se pudo publicar {}: {}", message.getClass().getSimpleName(), ex.getMessage());
        }
    }
}
