package co.edu.unicauca.lifecycle.messaging.messages;

import java.time.LocalDateTime;

public record QuestionStateChanged(Long questionId, String fromState, String toState, String changedBy,
                                   String comment, LocalDateTime occurredAt) {
}
