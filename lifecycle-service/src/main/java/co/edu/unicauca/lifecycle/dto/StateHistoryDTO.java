package co.edu.unicauca.lifecycle.dto;

import co.edu.unicauca.lifecycle.model.StateHistory;

import java.time.LocalDateTime;

public record StateHistoryDTO(String fromState, String toState, String changedBy, String comment,
                              LocalDateTime changedAt) {

    public static StateHistoryDTO from(StateHistory h) {
        return new StateHistoryDTO(h.getFromState() == null ? null : h.getFromState().name(), h.getToState().name(),
                h.getChangedBy(), h.getComment(), h.getChangedAt());
    }
}
