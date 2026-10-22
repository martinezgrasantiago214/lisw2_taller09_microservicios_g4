package co.edu.unicauca.question.messaging.messages;

import java.time.LocalDateTime;

/** Evento: se registró una pregunta válida en DRAFT. El Lifecycle Service crea su estado inicial. */
public record QuestionCreated(Long questionId, Long authorId, LocalDateTime occurredAt) {
}
