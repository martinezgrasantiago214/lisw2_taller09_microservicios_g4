package co.edu.unicauca.lifecycle.model;

import java.util.Map;
import java.util.Set;

/**
 * Máquina de estados de la pregunta. Las transiciones permitidas están
 * centralizadas aquí: ningún otro servicio modifica el estado por su cuenta.
 */
public enum QuestionStatus {
    DRAFT,
    PENDING_REVIEW,
    IN_REVIEW,
    APPROVED,
    REJECTED,
    PUBLISHED;

    private static final Map<QuestionStatus, Set<QuestionStatus>> ALLOWED = Map.of(
            DRAFT, Set.of(PENDING_REVIEW),
            PENDING_REVIEW, Set.of(IN_REVIEW, DRAFT),
            IN_REVIEW, Set.of(APPROVED, REJECTED),
            REJECTED, Set.of(DRAFT),
            APPROVED, Set.of(PUBLISHED),
            PUBLISHED, Set.of());

    public boolean canTransitionTo(QuestionStatus target) {
        return ALLOWED.get(this).contains(target);
    }

    public Set<QuestionStatus> allowedTransitions() {
        return ALLOWED.get(this);
    }
}
