package co.edu.unicauca.lifecycle.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Estado actual de una pregunta. */
@Entity
@Table(name = "question_states")
public class QuestionState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long questionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus state;

    private LocalDateTime changedAt;
    private String changedBy;

    @Column(length = 1000)
    private String comment;

    @Version
    private Long version;

    protected QuestionState() {
    }

    public QuestionState(Long questionId, String changedBy) {
        this.questionId = questionId;
        this.state = QuestionStatus.DRAFT;
        this.changedAt = LocalDateTime.now();
        this.changedBy = changedBy;
        this.comment = "Pregunta registrada";
    }

    public boolean canTransitionTo(QuestionStatus target) {
        return state.canTransitionTo(target);
    }

    public void applyTransition(QuestionStatus target, String changedBy, String comment) {
        if (!canTransitionTo(target)) {
            throw new IllegalStateException("Transición no permitida: " + state + " -> " + target);
        }
        this.state = target;
        this.changedAt = LocalDateTime.now();
        this.changedBy = changedBy;
        this.comment = comment;
    }

    public QuestionStatus getCurrentState() { return state; }
    public Long getId() { return id; }
    public Long getQuestionId() { return questionId; }
    public LocalDateTime getChangedAt() { return changedAt; }
    public String getChangedBy() { return changedBy; }
    public String getComment() { return comment; }
}
