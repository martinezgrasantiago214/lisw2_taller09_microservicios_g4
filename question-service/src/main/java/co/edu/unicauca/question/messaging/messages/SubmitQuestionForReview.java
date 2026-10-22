package co.edu.unicauca.question.messaging.messages;

import java.time.LocalDateTime;

/** Comando dirigido al Lifecycle Service: solicitar la transición DRAFT -> PENDING_REVIEW. */
public record SubmitQuestionForReview(String operationId, Long questionId, Long requestedBy,
                                      LocalDateTime requestedAt) {
}
