package co.edu.unicauca.lifecycle.messaging.messages;

import java.time.LocalDateTime;

/** Evento: la pregunta quedó en PENDING_REVIEW (lista para asignar revisor). */
public record QuestionSubmittedForReview(String operationId, Long questionId, String newState,
                                         LocalDateTime occurredAt) {
}
