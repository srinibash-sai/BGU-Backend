package com.anup.bgu.feedback.repo;

import com.anup.bgu.feedback.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Long>{
}
