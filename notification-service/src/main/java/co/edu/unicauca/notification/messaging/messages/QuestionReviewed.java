package co.edu.unicauca.notification.messaging.messages;

import java.time.LocalDateTime;

public record QuestionReviewed(Long reviewId, Long questionId, Long authorId, Long reviewerId, String result,
                               String comments, LocalDateTime occurredAt) {
}
