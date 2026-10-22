package co.edu.unicauca.review.config;

import co.edu.unicauca.review.messaging.messages.QuestionReviewed;
import co.edu.unicauca.review.messaging.messages.ReviewerAssigned;
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

    public static final String RK_REVIEWER_ASSIGNED = "reviewer.assigned";
    public static final String RK_QUESTION_REVIEWED = "question.reviewed";
    public static final String NOTIFICATION_REVIEWER_ASSIGNED_QUEUE = "notification.reviewer-assigned.queue";
    public static final String NOTIFICATION_QUESTION_REVIEWED_QUEUE = "notification.question-reviewed.queue";

    @Bean
    public Declarables topology() {
        DirectExchange exchange = new DirectExchange(EXCHANGE, true, false);
        DirectExchange dlx = new DirectExchange(DLX, true, false);
        Queue dlq = QueueBuilder.durable(DLQ).build();
        Queue assigned = queue(NOTIFICATION_REVIEWER_ASSIGNED_QUEUE);
        Queue reviewed = queue(NOTIFICATION_QUESTION_REVIEWED_QUEUE);
        return new Declarables(exchange, dlx, dlq, assigned, reviewed,
                BindingBuilder.bind(dlq).to(dlx).with(DLQ_ROUTING_KEY),
                BindingBuilder.bind(assigned).to(exchange).with(RK_REVIEWER_ASSIGNED),
                BindingBuilder.bind(reviewed).to(exchange).with(RK_QUESTION_REVIEWED));
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
                "QuestionReviewed", QuestionReviewed.class));
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
