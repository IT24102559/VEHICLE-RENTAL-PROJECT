package org.example.rentalsytsem.repository;

import org.example.rentalsytsem.entity.inquiry;
import org.example.rentalsytsem.entity.user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface inquiryrepository extends JpaRepository<inquiry, Long> {

    List<inquiry> findByUserOrderByCreatedAtDesc(user user);

    List<inquiry> findAllByOrderByCreatedAtDesc();

    List<inquiry> findByStatusOrderByCreatedAtDesc(inquiry.InquiryStatus status);

    @Query("SELECT i FROM inquiry i WHERE i.user.id = :userId ORDER BY i.createdAt DESC")
    List<inquiry> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT COUNT(i) FROM inquiry i WHERE i.status = :status")
    long countByStatus(@Param("status") inquiry.InquiryStatus status);

    @Query("SELECT COUNT(i) FROM inquiry i WHERE i.user.id = :userId AND i.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") inquiry.InquiryStatus status);
}
