package co.edu.unicauca.review.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_assignments")
public class ReviewAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long questionId;

    @Column(nullable = false)
    private Long reviewerId;

    private Long assignedBy;
    private LocalDateTime assignedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    @Column(length = 500)
    private String statusDetail;

    protected ReviewAssignment() {
    }

    public ReviewAssignment(Long questionId, Long reviewerId, Long assignedBy) {
        this.questionId = questionId;
        this.reviewerId = reviewerId;
        this.assignedBy = assignedBy;
        this.assignedAt = LocalDateTime.now();
        this.status = AssignmentStatus.PENDING;
        this.statusDetail = "Esperando confirmación del Lifecycle Service";
    }

    public void updateStatus(AssignmentStatus status, String detail) {
        this.status = status;
        this.statusDetail = detail;
    }

    public Long getId() { return id; }
    public Long getQuestionId() { return questionId; }
    public Long getReviewerId() { return reviewerId; }
    public Long getAssignedBy() { return assignedBy; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public AssignmentStatus getStatus() { return status; }
    public String getStatusDetail() { return statusDetail; }
}
