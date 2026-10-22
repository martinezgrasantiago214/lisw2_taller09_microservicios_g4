package co.edu.unicauca.lifecycle.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Bitácora de transiciones (auditoría, incluye el motivo de rechazo). */
@Entity
@Table(name = "state_history")
public class StateHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long questionId;

    @Enumerated(EnumType.STRING)
    private QuestionStatus fromState;

    @Enumerated(EnumType.STRING)
    private QuestionStatus toState;

    private String changedBy;

    @Column(length = 1000)
    private String comment;

    private LocalDateTime changedAt;

    protected StateHistory() {
    }

    public StateHistory(Long questionId, QuestionStatus fromState, QuestionStatus toState, String changedBy,
                        String comment) {
        this.questionId = questionId;
        this.fromState = fromState;
        this.toState = toState;
        this.changedBy = changedBy;
        this.comment = comment;
        this.changedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getQuestionId() { return questionId; }
    public QuestionStatus getFromState() { return fromState; }
    public QuestionStatus getToState() { return toState; }
    public String getChangedBy() { return changedBy; }
    public String getComment() { return comment; }
    public LocalDateTime getChangedAt() { return changedAt; }
}
