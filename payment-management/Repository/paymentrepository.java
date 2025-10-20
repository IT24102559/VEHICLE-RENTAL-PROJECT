package org.example.rentalsytsem.repository;

import org.example.rentalsytsem.entity.payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface paymentrepository extends JpaRepository<payment, Long> {

    Optional<payment> findById(Long id);
    
    List<payment> findByUserEmail(String userEmail);
    
    List<payment> findByBookingId(Long bookingId);
    
    List<payment> findByPaymentStatus(String paymentStatus);
}
