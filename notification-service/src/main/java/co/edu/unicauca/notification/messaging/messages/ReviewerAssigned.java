package co.edu.unicauca.notification.messaging.messages;

import java.time.LocalDateTime;

public record ReviewerAssigned(Long assignmentId, Long questionId, Long reviewerId, Long assignedBy,
                               LocalDateTime occurredAt) {
}
