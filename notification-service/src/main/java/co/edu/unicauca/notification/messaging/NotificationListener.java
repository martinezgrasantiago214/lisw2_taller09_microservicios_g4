package co.edu.unicauca.notification.messaging;

import co.edu.unicauca.notification.config.RabbitMQConfig;
import co.edu.unicauca.notification.messaging.messages.QuestionReviewed;
import co.edu.unicauca.notification.messaging.messages.QuestionSubmittedForReview;
import co.edu.unicauca.notification.messaging.messages.ReviewerAssigned;
import co.edu.unicauca.notification.messaging.messages.ValidationFailed;
import co.edu.unicauca.notification.model.NotificationType;
import co.edu.unicauca.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    private final NotificationService notificationService;

    public NotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /** Punto 8 del escenario: notificar al revisor asignado. */
    @RabbitListener(queues = RabbitMQConfig.REVIEWER_ASSIGNED_QUEUE)
    public void onReviewerAssigned(ReviewerAssigned event) {
        log.info("[NotificationService] Recibido ReviewerAssigned questionId={} reviewerId={}",
                event.questionId(), event.reviewerId());
        notificationService.notify(event.reviewerId(), event.questionId(), NotificationType.REVIEWER_ASSIGNED,
                "Nueva pregunta asignada para revisión",
                "Se le asignó la revisión de la pregunta " + event.questionId()
                        + " (asignación " + event.assignmentId() + ").");
    }

    @RabbitListener(queues = RabbitMQConfig.QUESTION_SUBMITTED_QUEUE)
    public void onQuestionSubmitted(QuestionSubmittedForReview event) {
        log.info("[NotificationService] Recibido QuestionSubmittedForReview questionId={}", event.questionId());
        notificationService.notify(null, event.questionId(), NotificationType.QUESTION_SUBMITTED,
                "Pregunta pendiente de asignación de revisor",
                "La pregunta " + event.questionId() + " está en " + event.newState() + " y requiere un revisor.");
    }

    @RabbitListener(queues = RabbitMQConfig.QUESTION_REVIEWED_QUEUE)
    public void onQuestionReviewed(QuestionReviewed event) {
        log.info("[NotificationService] Recibido QuestionReviewed questionId={} result={}",
                event.questionId(), event.result());
        boolean approved = "APPROVED".equals(event.result());
        notificationService.notify(event.authorId(), event.questionId(),
                approved ? NotificationType.QUESTION_APPROVED : NotificationType.QUESTION_REJECTED,
                approved ? "Su pregunta fue aprobada" : "Su pregunta fue rechazada",
                "Resultado de la revisión de la pregunta " + event.questionId() + ": " + event.result()
                        + (event.comments() == null ? "" : ". Observaciones: " + event.comments()));
    }

    @RabbitListener(queues = RabbitMQConfig.VALIDATION_FAILED_QUEUE)
    public void onValidationFailed(ValidationFailed event) {
        log.info("[NotificationService] Recibido ValidationFailed authorId={}", event.authorId());
        notificationService.notify(event.authorId(), event.questionId(), NotificationType.VALIDATION_FAILED,
                "La pregunta no superó la validación",
                "Errores encontrados: " + (event.errors() == null ? "-" : String.join(" | ", event.errors())));
    }
}
