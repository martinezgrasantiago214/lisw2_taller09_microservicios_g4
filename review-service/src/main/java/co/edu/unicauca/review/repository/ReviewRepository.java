package co.edu.unicauca.review.repository;

import co.edu.unicauca.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByQuestionId(Long questionId);
}
