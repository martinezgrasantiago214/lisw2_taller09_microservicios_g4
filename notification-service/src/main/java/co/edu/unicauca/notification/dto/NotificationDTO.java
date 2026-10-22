package co.edu.unicauca.notification.dto;

import co.edu.unicauca.notification.model.Notification;

import java.time.LocalDateTime;

public record NotificationDTO(Long id, Long recipientId, Long questionId, String type, String subject, String message,
                              String status, LocalDateTime createdAt, LocalDateTime sentAt) {

    public static NotificationDTO from(Notification n) {
        return new NotificationDTO(n.getId(), n.getRecipientId(), n.getQuestionId(), n.getType().name(),
                n.getSubject(), n.getMessage(), n.getStatus().name(), n.getCreatedAt(), n.getSentAt());
    }
}
