package co.edu.unicauca.lifecycle.repository;

import co.edu.unicauca.lifecycle.model.StateHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StateHistoryRepository extends JpaRepository<StateHistory, Long> {
    List<StateHistory> findByQuestionIdOrderByIdAsc(Long questionId);
}
