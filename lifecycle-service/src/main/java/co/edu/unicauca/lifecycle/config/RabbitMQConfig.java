package co.edu.unicauca.lifecycle.config;

import co.edu.unicauca.lifecycle.messaging.messages.*;
import co.edu.unicauca.lifecycle.service.LifecycleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.Map;

@Configuration
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    public static final String EXCHANGE = "saberpro.direct.exchange";
    public static final String DLX = "saberpro.dlx";
    public static final String DLQ = "saberpro.dead-letter.queue";
    public static final String DLQ_ROUTING_KEY = "dead-letter";

    // Consume (una sola cola => los mensajes de una misma pregunta se procesan en orden)
    public static final String LIFECYCLE_COMMANDS_QUEUE = "lifecycle.commands.queue";
    public static final String RK_QUESTION_CREATED = "question.created";
    public static final String RK_SUBMIT = "question.submit";

    // Publica
    public static final String RK_OPERATION_RESULT = "question.operation-result";
    public static final String RK_STATE_CHANGED = "question.state-changed";
    public static final String RK_QUESTION_SUBMITTED = "question.submitted";
    public static final String OPERATION_RESULT_QUEUE = "question.operation-result.queue";
    public static final String STATE_CHANGED_QUEUE = "question.state-changed.queue";
    public static final String NOTIFICATION_QUESTION_SUBMITTED_QUEUE = "notification.question-submitted.queue";

    @Bean
    public Declarables topology() {
        DirectExchange exchange = new DirectExchange(EXCHANGE, true, false);
        DirectExchange dlx = new DirectExchange(DLX, true, false);
        Queue dlq = QueueBuilder.durable(DLQ).build();
        Queue commands = queue(LIFECYCLE_COMMANDS_QUEUE);
        Queue operationResult = queue(OPERATION_RESULT_QUEUE);
        Queue stateChanged = queue(STATE_CHANGED_QUEUE);
        Queue submitted = queue(NOTIFICATION_QUESTION_SUBMITTED_QUEUE);
        return new Declarables(exchange, dlx, dlq, commands, operationResult, stateChanged, submitted,
                BindingBuilder.bind(dlq).to(dlx).with(DLQ_ROUTING_KEY),
                BindingBuilder.bind(commands).to(exchange).with(RK_QUESTION_CREATED),
                BindingBuilder.bind(commands).to(exchange).with(RK_SUBMIT),
                BindingBuilder.bind(operationResult).to(exchange).with(RK_OPERATION_RESULT),
                BindingBuilder.bind(stateChanged).to(exchange).with(RK_STATE_CHANGED),
                BindingBuilder.bind(submitted).to(exchange).with(RK_QUESTION_SUBMITTED));
    }

    static Queue queue(String name) {
        return QueueBuilder.durable(name).deadLetterExchange(DLX).deadLetterRoutingKey(DLQ_ROUTING_KEY).build();
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        typeMapper.setIdClassMapping(Map.<String, Class<?>>of(
                "QuestionCreated", QuestionCreated.class,
                "SubmitQuestionForReview", SubmitQuestionForReview.class,
                "OperationResult", OperationResultMessage.class,
                "QuestionStateChanged", QuestionStateChanged.class,
                "QuestionSubmittedForReview", QuestionSubmittedForReview.class));
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    /**
     * Se ejecuta cuando un mensaje agotó los reintentos por un error técnico (no de
     * negocio). Si era un SubmitQuestionForReview, antes de enviarlo a la DLQ se registra
     * la operación como FAILED y se publica el resultado: así el cliente que consulta
     * GET /operations/{id} recibe una respuesta veraz y no queda en PROCESSING para siempre.
     * Spring Boot usa automáticamente este bean en los listeners con reintentos.
     */
    @Bean
    public MessageRecoverer messageRecoverer(MessageConverter jsonMessageConverter,
                                             @Lazy LifecycleService lifecycleService) {
        RejectAndDontRequeueRecoverer toDeadLetterQueue = new RejectAndDontRequeueRecoverer();
        return (message, cause) -> {
            try {
                Object payload = jsonMessageConverter.fromMessage(message);
                if (payload instanceof SubmitQuestionForReview command) {
                    lifecycleService.recordTechnicalFailure(command, rootMessage(cause));
                }
            } catch (Exception ex) {
                log.error("[LifecycleService] No se pudo registrar el fallo técnico del mensaje: {}", ex.getMessage());
            }
            toDeadLetterQueue.recover(message, cause); // el mensaje original termina en la DLQ
        };
    }

    private static String rootMessage(Throwable cause) {
        Throwable root = cause;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        return root.getMessage() == null ? root.getClass().getSimpleName() : root.getMessage();
    }
}
