package co.edu.unicauca.question.repository;

import co.edu.unicauca.question.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByAuthorId(Long authorId);
}
