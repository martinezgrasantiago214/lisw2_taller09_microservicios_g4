package co.edu.unicauca.review.client;

public record LifecycleState(Long questionId, String state, String changedBy, String comment) {
}
