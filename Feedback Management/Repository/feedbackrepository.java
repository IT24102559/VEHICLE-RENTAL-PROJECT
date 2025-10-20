package org.example.rentalsytsem.repository;

import org.example.rentalsytsem.entity.feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface feedbackrepository extends JpaRepository<feedback, Long> {
    List<feedback> findByStatus(String status);
    List<feedback> findByStatusOrderByCreatedAtDesc(String status);
    List<feedback> findByUserNameOrderByCreatedAtDesc(String userName);
}

