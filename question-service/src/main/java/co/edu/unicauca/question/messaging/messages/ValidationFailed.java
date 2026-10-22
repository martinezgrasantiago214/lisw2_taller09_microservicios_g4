package co.edu.unicauca.question.messaging.messages;

import java.time.LocalDateTime;
import java.util.List;

/** Evento informativo: un intento de crear/corregir una pregunta no superó la validación. */
public record ValidationFailed(String operationId, Long questionId, Long authorId, List<String> errors,
                               LocalDateTime occurredAt) {
}
