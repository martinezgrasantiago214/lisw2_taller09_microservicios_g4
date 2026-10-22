package co.edu.unicauca.lifecycle.repository;

import co.edu.unicauca.lifecycle.model.QuestionState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionStateRepository extends JpaRepository<QuestionState, Long> {
    Optional<QuestionState> findByQuestionId(Long questionId);

    boolean existsByQuestionId(Long questionId);
}
