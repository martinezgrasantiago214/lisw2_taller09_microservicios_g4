package co.edu.unicauca.review.model;

public enum AssignmentStatus {
    /** Registrada localmente, esperando la confirmación del Lifecycle Service. */
    PENDING,
    /** Confirmada: la pregunta pasó a IN_REVIEW. */
    ASSIGNED,
    /** Compensada: el Lifecycle Service rechazó o no respondió la transición. */
    CANCELLED,
    /** El revisor ya emitió su dictamen. */
    COMPLETED
}
