package co.edu.unicauca.lifecycle.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Resultado registrado del procesamiento de un comando asíncrono (permite idempotencia y consulta). */
@Entity
@Table(name = "operation_results")
public class OperationResult {

    @Id
    private String id;

    private Long questionId;
    private String type;

    @Enumerated(EnumType.STRING)
    private OperationStatus status;

    @Column(length = 1000)
    private String message;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    protected OperationResult() {
    }

    public OperationResult(String id, Long questionId, String type, OperationStatus status, String message,
                           LocalDateTime createdAt) {
        this.id = id;
        this.questionId = questionId;
        this.type = type;
        this.status = status;
        this.message = message;
        this.createdAt = createdAt;
        this.completedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public Long getQuestionId() { return questionId; }
    public String getType() { return type; }
    public OperationStatus getStatus() { return status; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}
