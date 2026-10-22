package co.edu.unicauca.question.messaging.messages;

import java.time.LocalDateTime;

/** Resultado del procesamiento de un comando asíncrono (COMPLETED / FAILED). */
public record OperationResultMessage(String operationId, Long questionId, String type, String status,
                                     String message, String newState, LocalDateTime occurredAt) {
}
