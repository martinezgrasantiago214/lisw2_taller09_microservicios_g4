package co.edu.unicauca.question.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Seguimiento de una operación asíncrona (202 Accepted + operationId). */
@Entity
@Table(name = "operations")
public class Operation {

    @Id
    private String operationId;

    @Column(nullable = false)
    private Long questionId;

    @Column(nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperationStatus status;

    @Column(length = 500)
    private String message;

    private String resultingState;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    protected Operation() {
    }

    public Operation(String operationId, Long questionId, String type) {
        this.operationId = operationId;
        this.questionId = questionId;
        this.type = type;
        this.status = OperationStatus.PROCESSING;
        this.message = "Solicitud aceptada; pendiente de procesamiento por el Lifecycle Service.";
        this.createdAt = LocalDateTime.now();
    }

    public void complete(OperationStatus finalStatus, String message, String resultingState) {
        this.status = finalStatus;
        this.message = message;
        this.resultingState = resultingState;
        this.completedAt = LocalDateTime.now();
    }

    public String getOperationId() { return operationId; }
    public Long getQuestionId() { return questionId; }
    public String getType() { return type; }
    public OperationStatus getStatus() { return status; }
    public String getMessage() { return message; }
    public String getResultingState() { return resultingState; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}
