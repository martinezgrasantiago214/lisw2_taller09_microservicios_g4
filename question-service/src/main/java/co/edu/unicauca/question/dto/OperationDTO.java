package co.edu.unicauca.question.dto;

import co.edu.unicauca.question.model.Operation;

import java.time.LocalDateTime;

public record OperationDTO(String operationId, Long questionId, String type, String status, String message,
                           String resultingState, LocalDateTime createdAt, LocalDateTime completedAt) {

    public static OperationDTO from(Operation op) {
        return new OperationDTO(op.getOperationId(), op.getQuestionId(), op.getType(), op.getStatus().name(),
                op.getMessage(), op.getResultingState(), op.getCreatedAt(), op.getCompletedAt());
    }
}
