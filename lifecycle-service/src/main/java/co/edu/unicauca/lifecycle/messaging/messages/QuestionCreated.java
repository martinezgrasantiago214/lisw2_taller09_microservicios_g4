package co.edu.unicauca.lifecycle.messaging.messages;

import java.time.LocalDateTime;

public record QuestionCreated(Long questionId, Long authorId, LocalDateTime occurredAt) {
}
