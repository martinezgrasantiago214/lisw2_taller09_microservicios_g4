package co.edu.unicauca.lifecycle.dto;

import co.edu.unicauca.lifecycle.model.OperationResult;

import java.time.LocalDateTime;

public record OperationResultDTO(String operationId, Long questionId, String type, String status, String message,
                                 LocalDateTime createdAt, LocalDateTime completedAt) {

    public static OperationResultDTO from(OperationResult r) {
        return new OperationResultDTO(r.getId(), r.getQuestionId(), r.getType(), r.getStatus().name(),
                r.getMessage(), r.getCreatedAt(), r.getCompletedAt());
    }
}
