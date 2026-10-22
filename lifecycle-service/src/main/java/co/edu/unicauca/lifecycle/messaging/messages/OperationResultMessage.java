package co.edu.unicauca.lifecycle.messaging.messages;

import java.time.LocalDateTime;

public record OperationResultMessage(String operationId, Long questionId, String type, String status,
                                     String message, String newState, LocalDateTime occurredAt) {
}
