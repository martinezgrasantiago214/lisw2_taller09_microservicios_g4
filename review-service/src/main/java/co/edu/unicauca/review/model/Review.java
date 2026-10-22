package co.edu.unicauca.review.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long assignmentId;
    private Long questionId;
    private Long reviewerId;

    @Enumerated(EnumType.STRING)
    private ReviewResult result;

    @Column(length = 2000)
    private String comments;

    private LocalDateTime createdAt;

    protected Review() {
    }

    public Review(Long assignmentId, Long questionId, Long reviewerId, ReviewResult result, String comments) {
        this.assignmentId = assignmentId;
        this.questionId = questionId;
        this.reviewerId = reviewerId;
        this.result = result;
        this.comments = comments;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getAssignmentId() { return assignmentId; }
    public Long getQuestionId() { return questionId; }
    public Long getReviewerId() { return reviewerId; }
    public ReviewResult getResult() { return result; }
    public String getComments() { return comments; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
