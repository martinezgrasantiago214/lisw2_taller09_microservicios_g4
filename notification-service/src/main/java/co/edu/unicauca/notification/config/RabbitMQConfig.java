package co.edu.unicauca.notification.config;

import co.edu.unicauca.notification.messaging.messages.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "saberpro.direct.exchange";
    public static final String DLX = "saberpro.dlx";
    public static final String DLQ = "saberpro.dead-letter.queue";
    public static final String DLQ_ROUTING_KEY = "dead-letter";

    public static final String REVIEWER_ASSIGNED_QUEUE = "notification.reviewer-assigned.queue";
    public static final String QUESTION_REVIEWED_QUEUE = "notification.question-reviewed.queue";
    public static final String QUESTION_SUBMITTED_QUEUE = "notification.question-submitted.queue";
    public static final String VALIDATION_FAILED_QUEUE = "notification.validation-failed.queue";

    @Bean
    public Declarables topology() {
        DirectExchange exchange = new DirectExchange(EXCHANGE, true, false);
        DirectExchange dlx = new DirectExchange(DLX, true, false);
        Queue dlq = QueueBuilder.durable(DLQ).build();
        Queue assigned = queue(REVIEWER_ASSIGNED_QUEUE);
        Queue reviewed = queue(QUESTION_REVIEWED_QUEUE);
        Queue submitted = queue(QUESTION_SUBMITTED_QUEUE);
        Queue validationFailed = queue(VALIDATION_FAILED_QUEUE);
        return new Declarables(exchange, dlx, dlq, assigned, reviewed, submitted, validationFailed,
                BindingBuilder.bind(dlq).to(dlx).with(DLQ_ROUTING_KEY),
                BindingBuilder.bind(assigned).to(exchange).with("reviewer.assigned"),
                BindingBuilder.bind(reviewed).to(exchange).with("question.reviewed"),
                BindingBuilder.bind(submitted).to(exchange).with("question.submitted"),
                BindingBuilder.bind(validationFailed).to(exchange).with("validation.failed"));
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
                "ReviewerAssigned", ReviewerAssigned.class,
                "QuestionReviewed", QuestionReviewed.class,
                "QuestionSubmittedForReview", QuestionSubmittedForReview.class,
                "ValidationFailed", ValidationFailed.class));
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
