package co.edu.unicauca.question.dto;

import co.edu.unicauca.question.model.Question;

import java.time.LocalDateTime;
import java.util.List;

public record QuestionDTO(Long id, String text, List<OptionDTO> options, String correctOptionId, Long authorId,
                          String status, LocalDateTime createdAt, LocalDateTime updatedAt) {

    public static QuestionDTO from(Question q) {
        return new QuestionDTO(q.getId(), q.getText(),
                q.getOptions().stream().map(o -> new OptionDTO(o.getId(), o.getText())).toList(),
                q.getCorrectOptionId(), q.getAuthorId(), q.getStatus(), q.getCreatedAt(), q.getUpdatedAt());
    }
}
