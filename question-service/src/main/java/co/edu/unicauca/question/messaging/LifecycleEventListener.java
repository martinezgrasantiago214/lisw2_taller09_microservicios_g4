package co.edu.unicauca.question.messaging;

import co.edu.unicauca.question.config.RabbitMQConfig;
import co.edu.unicauca.question.messaging.messages.OperationResultMessage;
import co.edu.unicauca.question.messaging.messages.QuestionStateChanged;
import co.edu.unicauca.question.model.OperationStatus;
import co.edu.unicauca.question.repository.OperationRepository;
import co.edu.unicauca.question.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** Consume los resultados que publica el Lifecycle Service. */
@Component
public class LifecycleEventListener {

    private static final Logger log = LoggerFactory.getLogger(LifecycleEventListener.class);

    private final OperationRepository operationRepository;
    private final QuestionRepository questionRepository;

    public LifecycleEventListener(OperationRepository operationRepository, QuestionRepository questionRepository) {
        this.operationRepository = operationRepository;
        this.questionRepository = questionRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.OPERATION_RESULT_QUEUE)
    @Transactional
    public void onOperationResult(OperationResultMessage result) {
        log.info("[QuestionService] OperationResult recibido operationId={} status={}",
                result.operationId(), result.status());
        operationRepository.findById(result.operationId()).ifPresentOrElse(
                op -> op.complete(OperationStatus.valueOf(result.status()), result.message(), result.newState()),
                () -> log.warn("[QuestionService] Operación desconocida {}", result.operationId()));
    }

    @RabbitListener(queues = RabbitMQConfig.STATE_CHANGED_QUEUE)
    @Transactional
    public void onStateChanged(QuestionStateChanged event) {
        log.info("[QuestionService] Estado de la pregunta {}: {} -> {}",
                event.questionId(), event.fromState(), event.toState());
        questionRepository.findById(event.questionId()).ifPresent(q -> q.updateStatus(event.toState()));
    }
}
