package co.edu.unicauca.lifecycle.dto;

import co.edu.unicauca.lifecycle.model.QuestionState;
import co.edu.unicauca.lifecycle.model.QuestionStatus;

import java.time.LocalDateTime;
import java.util.List;

public record QuestionStateDTO(Long questionId, String state, LocalDateTime changedAt, String changedBy,
                               String comment, List<String> allowedTransitions) {

    public static QuestionStateDTO from(QuestionState s) {
        return new QuestionStateDTO(s.getQuestionId(), s.getCurrentState().name(), s.getChangedAt(),
                s.getChangedBy(), s.getComment(),
                s.getCurrentState().allowedTransitions().stream().map(QuestionStatus::name).sorted().toList());
    }
}
