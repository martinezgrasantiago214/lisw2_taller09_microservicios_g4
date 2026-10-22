package co.edu.unicauca.review.dto;

import co.edu.unicauca.review.model.ReviewAssignment;

import java.time.LocalDateTime;

public record AssignmentDTO(Long id, Long questionId, Long reviewerId, Long assignedBy, LocalDateTime assignedAt,
                            String status, String statusDetail, String questionState, Boolean notificationQueued) {

    public static AssignmentDTO from(ReviewAssignment a) {
        return from(a, null, null);
    }

    public static AssignmentDTO from(ReviewAssignment a, String questionState, Boolean notificationQueued) {
        return new AssignmentDTO(a.getId(), a.getQuestionId(), a.getReviewerId(), a.getAssignedBy(),
                a.getAssignedAt(), a.getStatus().name(), a.getStatusDetail(), questionState, notificationQueued);
    }
}
