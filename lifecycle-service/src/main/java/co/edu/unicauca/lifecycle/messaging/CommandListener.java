package co.edu.unicauca.lifecycle.messaging;

import co.edu.unicauca.lifecycle.config.RabbitMQConfig;
import co.edu.unicauca.lifecycle.messaging.messages.QuestionCreated;
import co.edu.unicauca.lifecycle.messaging.messages.SubmitQuestionForReview;
import co.edu.unicauca.lifecycle.service.LifecycleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de lifecycle.commands.queue. El tipo de mensaje (cabecera
 * __TypeId__) decide qué @RabbitHandler lo procesa.
 */
@Component
@RabbitListener(queues = RabbitMQConfig.LIFECYCLE_COMMANDS_QUEUE)
public class CommandListener {

    private static final Logger log = LoggerFactory.getLogger(CommandListener.class);

    private final LifecycleService lifecycleService;

    public CommandListener(LifecycleService lifecycleService) {
        this.lifecycleService = lifecycleService;
    }

    @RabbitHandler
    public void onQuestionCreated(QuestionCreated event) {
        log.info("[LifecycleService] Recibido QuestionCreated questionId={}", event.questionId());
        lifecycleService.register(event);
    }

    @RabbitHandler
    public void onSubmitForReview(SubmitQuestionForReview command) {
        log.info("[LifecycleService] Recibido comando SubmitQuestionForReview questionId={} operationId={}",
                command.questionId(), command.operationId());
        lifecycleService.processSubmit(command);
    }
}
