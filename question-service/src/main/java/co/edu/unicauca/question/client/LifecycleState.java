package co.edu.unicauca.question.client;

public record LifecycleState(Long questionId, String state, String changedBy, String comment) {
}
