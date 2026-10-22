package co.edu.unicauca.question.config;

import co.edu.unicauca.question.messaging.messages.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Topología RabbitMQ que usa el Question Service. Se declaran tanto las colas que
 * consume como las colas destino de lo que publica, para que ningún mensaje se
 * pierda si el consumidor aún no ha arrancado (las declaraciones son idempotentes).
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "saberpro.direct.exchange";
    public static final String DLX = "saberpro.dlx";
    public static final String DLQ = "saberpro.dead-letter.queue";
    public static final String DLQ_ROUTING_KEY = "dead-letter";

    // Publica
    public static final String RK_QUESTION_CREATED = "question.created";
    public static final String RK_SUBMIT = "question.submit";
    public static final String RK_VALIDATION_FAILED = "validation.failed";
    public static final String LIFECYCLE_COMMANDS_QUEUE = "lifecycle.commands.queue";
    public static final String NOTIFICATION_VALIDATION_FAILED_QUEUE = "notification.validation-failed.queue";

    // Consume
    public static final String RK_OPERATION_RESULT = "question.operation-result";
    public static final String RK_STATE_CHANGED = "question.state-changed";
    public static final String OPERATION_RESULT_QUEUE = "question.operation-result.queue";
    public static final String STATE_CHANGED_QUEUE = "question.state-changed.queue";

    @Bean
    public Declarables topology() {
        DirectExchange exchange = new DirectExchange(EXCHANGE, true, false);
        DirectExchange dlx = new DirectExchange(DLX, true, false);
        Queue dlq = QueueBuilder.durable(DLQ).build();
        Queue lifecycleCommands = queue(LIFECYCLE_COMMANDS_QUEUE);
        Queue validationFailed = queue(NOTIFICATION_VALIDATION_FAILED_QUEUE);
        Queue operationResult = queue(OPERATION_RESULT_QUEUE);
        Queue stateChanged = queue(STATE_CHANGED_QUEUE);
        return new Declarables(exchange, dlx, dlq, lifecycleCommands, validationFailed, operationResult, stateChanged,
                BindingBuilder.bind(dlq).to(dlx).with(DLQ_ROUTING_KEY),
                BindingBuilder.bind(lifecycleCommands).to(exchange).with(RK_QUESTION_CREATED),
                BindingBuilder.bind(lifecycleCommands).to(exchange).with(RK_SUBMIT),
                BindingBuilder.bind(validationFailed).to(exchange).with(RK_VALIDATION_FAILED),
                BindingBuilder.bind(operationResult).to(exchange).with(RK_OPERATION_RESULT),
                BindingBuilder.bind(stateChanged).to(exchange).with(RK_STATE_CHANGED));
    }

    /** Cola durable cuyos mensajes rechazados van a la Dead Letter Queue. */
    static Queue queue(String name) {
        return QueueBuilder.durable(name).deadLetterExchange(DLX).deadLetterRoutingKey(DLQ_ROUTING_KEY).build();
    }

    /**
     * Serialización JSON con identificadores lógicos de tipo (cabecera __TypeId__),
     * así cada microservicio usa sus propias clases sin compartir librerías.
     */
    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        typeMapper.setIdClassMapping(Map.<String, Class<?>>of(
                "QuestionCreated", QuestionCreated.class,
                "SubmitQuestionForReview", SubmitQuestionForReview.class,
                "ValidationFailed", ValidationFailed.class,
                "OperationResult", OperationResultMessage.class,
                "QuestionStateChanged", QuestionStateChanged.class));
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
