package co.edu.unicauca.question.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String text;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "question_options", joinColumns = @JoinColumn(name = "question_id"))
    @OrderColumn(name = "position")
    private List<Option> options = new ArrayList<>();

    @Column(nullable = false)
    private String correctOptionId;

    @Column(nullable = false)
    private Long authorId;

    /**
     * Proyección local del estado. La autoridad es el Lifecycle Service; este campo se
     * actualiza con el evento QuestionStateChanged (consistencia eventual).
     */
    @Column(nullable = false)
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected Question() {
    }

    public static Question create(String text, List<Option> options, String correctOptionId, Long authorId) {
        Question q = new Question();
        q.text = text;
        q.options = new ArrayList<>(options);
        q.correctOptionId = correctOptionId;
        q.authorId = authorId;
        q.status = "DRAFT";
        q.createdAt = LocalDateTime.now();
        q.updatedAt = q.createdAt;
        return q;
    }

    public void updateContent(String text, List<Option> options, String correctOptionId) {
        this.text = text;
        this.options.clear();
        this.options.addAll(options);
        this.correctOptionId = correctOptionId;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getText() { return text; }
    public List<Option> getOptions() { return options; }
    public String getCorrectOptionId() { return correctOptionId; }
    public Long getAuthorId() { return authorId; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
