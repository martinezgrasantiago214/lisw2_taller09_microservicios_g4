package co.edu.unicauca.notification.messaging.messages;

import java.time.LocalDateTime;

public record QuestionSubmittedForReview(String operationId, Long questionId, String newState,
                                         LocalDateTime occurredAt) {
}
