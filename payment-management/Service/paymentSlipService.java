package org.example.rentalsytsem.service;

import org.example.rentalsytsem.entity.paymentSlip;
import org.example.rentalsytsem.entity.payment;
import org.example.rentalsytsem.entity.booking;
import org.example.rentalsytsem.repository.paymentSlipRepository;
import org.example.rentalsytsem.repository.paymentrepository;
import org.example.rentalsytsem.repository.bookingrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class paymentSlipService {
    
    @Autowired
    private paymentSlipRepository repository;
    
    @Autowired
    private paymentrepository paymentRepository;
    
    @Autowired
    private bookingrepository bookingRepository;
    
    private final String uploadDir = "uploads/payment-slips/";
    
    public paymentSlip savePaymentSlip(MultipartFile file, Long paymentId, Long bookingId, 
                                      String transferReference, String uploadedBy) throws IOException {
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        // Save file to disk
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Create payment slip entity
        paymentSlip slip = new paymentSlip();
        slip.setPaymentId(paymentId);
        slip.setBookingId(bookingId);
        slip.setFileName(originalFilename);
        slip.setFilePath(filePath.toString());
        slip.setFileType(file.getContentType());
        slip.setFileSize(file.getSize());
        slip.setTransferReference(transferReference);
        slip.setUploadedBy(uploadedBy);
        slip.setUploadedAt(LocalDateTime.now());
        slip.setStatus("PENDING");
        
        return repository.save(slip);
    }
    
    public List<paymentSlip> getAllPaymentSlips() {
        return repository.findAllByOrderByUploadedAtDesc();
    }
    
    public List<paymentSlip> getPaymentSlipsByStatus(String status) {
        return repository.findByStatusOrderByUploadedAtDesc(status);
    }
    
    public paymentSlip getPaymentSlipById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment slip not found with id: " + id));
    }
    
    public paymentSlip getPaymentSlipByBookingId(Long bookingId) {
        List<paymentSlip> slips = repository.findByBookingId(bookingId);
        if (slips.isEmpty()) {
            throw new RuntimeException("No payment slip found for booking: " + bookingId);
        }
        // Return the most recent one if multiple exist
        return slips.get(0);
    }
    
    public List<paymentSlip> getPaymentSlipsByBookingId(Long bookingId) {
        return repository.findByBookingId(bookingId);
    }
    
    public List<paymentSlip> getPaymentSlipsByPaymentId(Long paymentId) {
        return repository.findByPaymentId(paymentId);
    }
    
    public paymentSlip approvePaymentSlip(Long id, String reviewedBy, String reviewNotes) {
        return repository.findById(id).map(slip -> {
            slip.setStatus("APPROVED");
            slip.setReviewedBy(reviewedBy);
            slip.setReviewedAt(LocalDateTime.now());
            slip.setReviewNotes(reviewNotes);
            paymentSlip savedSlip = repository.save(slip);
            
            // Update payment status to APPROVED
            if (slip.getPaymentId() != null) {
                Optional<payment> paymentOpt = paymentRepository.findById(slip.getPaymentId());
                if (paymentOpt.isPresent()) {
                    payment payment = paymentOpt.get();
                    payment.setPaymentStatus("APPROVED");
                    paymentRepository.save(payment);
                }
            }
            
            // Update booking status to PAID when payment slip is approved
            if (slip.getBookingId() != null) {
                Optional<booking> bookingOpt = bookingRepository.findById(slip.getBookingId());
                if (bookingOpt.isPresent()) {
                    booking booking = bookingOpt.get();
                    booking.setStatus("PAID");
                    bookingRepository.save(booking);
                }
            }
            
            return savedSlip;
        }).orElseThrow(() -> new RuntimeException("Payment slip not found with id: " + id));
    }
    
    public paymentSlip rejectPaymentSlip(Long id, String reviewedBy, String reviewNotes) {
        return repository.findById(id).map(slip -> {
            slip.setStatus("REJECTED");
            slip.setReviewedBy(reviewedBy);
            slip.setReviewedAt(LocalDateTime.now());
            slip.setReviewNotes(reviewNotes);
            paymentSlip savedSlip = repository.save(slip);
            
            // Update payment status to REJECTED
            if (slip.getPaymentId() != null) {
                Optional<payment> paymentOpt = paymentRepository.findById(slip.getPaymentId());
                if (paymentOpt.isPresent()) {
                    payment payment = paymentOpt.get();
                    payment.setPaymentStatus("REJECTED");
                    paymentRepository.save(payment);
                }
            }
            
            // Update booking status to REJECTED when payment slip is rejected
            if (slip.getBookingId() != null) {
                Optional<booking> bookingOpt = bookingRepository.findById(slip.getBookingId());
                if (bookingOpt.isPresent()) {
                    booking booking = bookingOpt.get();
                    booking.setStatus("REJECTED");
                    bookingRepository.save(booking);
                }
            }
            
            return savedSlip;
        }).orElseThrow(() -> new RuntimeException("Payment slip not found with id: " + id));
    }
    
    public void deletePaymentSlip(Long id) {
        Optional<paymentSlip> slip = repository.findById(id);
        if (slip.isPresent()) {
            // Delete file from disk
            try {
                Path filePath = Paths.get(slip.get().getFilePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                // Log error but continue with database deletion
                System.err.println("Error deleting file: " + e.getMessage());
            }
            repository.deleteById(id);
        }
    }
    
    public byte[] getPaymentSlipFile(Long id) throws IOException {
        paymentSlip slip = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Payment slip not found with id: " + id));
        
        Path filePath = Paths.get(slip.getFilePath());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("File not found: " + slip.getFileName());
        }
        
        return Files.readAllBytes(filePath);
    }
}
