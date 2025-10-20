package org.example.rentalsytsem.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.rentalsytsem.entity.payment;
import org.example.rentalsytsem.repository.paymentrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class paymentservice {
    @Autowired
    private paymentrepository paymentrepository;

    public payment processPayment(payment payment) {
        System.out.println("DEBUG: Processing payment for user: " + payment.getUserEmail());
        
        // Handle email field mapping
        if (payment.getUserEmail() == null && payment.getEmail() != null) {
            payment.setUserEmail(payment.getEmail());
        }
        
        // Generate transaction ID
        if (payment.getTransactionId() == null) {
            payment.setTransactionId("TXN" + System.currentTimeMillis());
        }
        
        // Set payment date and created at timestamp
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }
        if (payment.getCreatedAt() == null) {
            payment.setCreatedAt(LocalDateTime.now());
        }
        payment.setUpdatedAt(LocalDateTime.now());
        
        // Set default status if not provided
        if (payment.getPaymentStatus() == null || payment.getPaymentStatus().trim().isEmpty()) {
            payment.setPaymentStatus("PENDING");
        }
        
        System.out.println("DEBUG: Saving payment to database...");
        payment savedPayment = paymentrepository.save(payment);
        System.out.println("DEBUG: Payment saved successfully with ID: " + savedPayment.getId());
        
        return savedPayment;
    }

    public List<payment> getAllPayments() {
        return paymentrepository.findAll();
    }

    public Optional<payment> getPaymentById(Long id) {
        return paymentrepository.findById(id);
    }

    public List<payment> getPaymentsByEmail(String email) {
        return paymentrepository.findByUserEmail(email);
    }

    public List<payment> getPaymentsByBookingId(Long bookingId) {
        return paymentrepository.findByBookingId(bookingId);
    }

    public List<payment> getPaymentsByStatus(String status) {
        return paymentrepository.findByPaymentStatus(status);
    }

    public payment savePayment(payment payment) {
        payment.setUpdatedAt(LocalDateTime.now());
        return paymentrepository.save(payment);
    }

    public void deletePayment(Long id) {
        paymentrepository.deleteById(id);
    }

    public payment updatePayment(payment payment) {
        Optional<payment> existingPayment = paymentrepository.findById(payment.getId());
        if (existingPayment.isPresent()) {
            payment updatedPayment = existingPayment.get();
            
            // Update fields that are provided (non-null)
            if (payment.getAmount() != null) {
                updatedPayment.setAmount(payment.getAmount());
            }
            if (payment.getPaymentMethod() != null && !payment.getPaymentMethod().isBlank()) {
                updatedPayment.setPaymentMethod(payment.getPaymentMethod());
            }
            if (payment.getPaymentStatus() != null && !payment.getPaymentStatus().isBlank()) {
                updatedPayment.setPaymentStatus(payment.getPaymentStatus());
            }
            if (payment.getTransactionId() != null && !payment.getTransactionId().isBlank()) {
                updatedPayment.setTransactionId(payment.getTransactionId());
            }
            if (payment.getNotes() != null) {
                updatedPayment.setNotes(payment.getNotes());
            }
            
            updatedPayment.setUpdatedAt(LocalDateTime.now());
            return paymentrepository.save(updatedPayment);
        } else {
            throw new RuntimeException("Payment not found with ID: " + payment.getId());
        }
    }
}
