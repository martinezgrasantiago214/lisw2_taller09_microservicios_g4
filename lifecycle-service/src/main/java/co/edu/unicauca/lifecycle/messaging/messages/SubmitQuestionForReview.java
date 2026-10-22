package co.edu.unicauca.lifecycle.messaging.messages;

import java.time.LocalDateTime;

public record SubmitQuestionForReview(String operationId, Long questionId, Long requestedBy,
                                      LocalDateTime requestedAt) {
}
