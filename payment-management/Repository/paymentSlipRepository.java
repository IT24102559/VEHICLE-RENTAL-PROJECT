package org.example.rentalsytsem.repository;

import org.example.rentalsytsem.entity.paymentSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface paymentSlipRepository extends JpaRepository<paymentSlip, Long> {
    
    Optional<paymentSlip> findById(Long id);
    
    List<paymentSlip> findByPaymentId(Long paymentId);
    
    List<paymentSlip> findByBookingId(Long bookingId);
    
    List<paymentSlip> findByStatus(String status);
    
    List<paymentSlip> findByUploadedBy(String uploadedBy);
    
    Optional<paymentSlip> findByTransferReference(String transferReference);
    
    List<paymentSlip> findByStatusOrderByUploadedAtDesc(String status);
    
    List<paymentSlip> findAllByOrderByUploadedAtDesc();
}
