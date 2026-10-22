package co.edu.unicauca.notification.messaging.messages;

import java.time.LocalDateTime;
import java.util.List;

public record ValidationFailed(String operationId, Long questionId, Long authorId, List<String> errors,
                               LocalDateTime occurredAt) {
}
