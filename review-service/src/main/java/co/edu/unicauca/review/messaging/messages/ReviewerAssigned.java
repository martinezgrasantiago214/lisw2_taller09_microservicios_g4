package co.edu.unicauca.review.messaging.messages;

import java.time.LocalDateTime;

public record ReviewerAssigned(Long assignmentId, Long questionId, Long reviewerId, Long assignedBy,
                               LocalDateTime occurredAt) {
}
