package org.example.rentalsytsem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rentalsytsem.entity.payment;
import org.example.rentalsytsem.entity.booking;
import org.example.rentalsytsem.entity.paymentSlip;
import org.example.rentalsytsem.service.paymentservice;
import org.example.rentalsytsem.service.bookingservice;
import org.example.rentalsytsem.service.paymentSlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    @Autowired
    private paymentservice paymentService;
    
    @Autowired
    private paymentSlipService paymentSlipService;
    
    @Autowired
    private bookingservice bookingService;
    
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody payment payment) {
        try {
            // Map email to userEmail field
            if (payment.getUserEmail() == null && payment.getCustomerName() != null) {
                // You can add logic here to get email from request or use a different field
                // For now, we'll handle it in the service layer
            }
            
            // Process the payment
            payment processedPayment = paymentService.processPayment(payment);
            
            // Only update booking status to PAID for non-bank transfer payments
            // Bank transfers need admin approval first
            if (processedPayment.getBookingId() != null && 
                !"BANK_TRANSFER".equals(processedPayment.getPaymentMethod()) &&
                !"PENDING_VERIFICATION".equals(processedPayment.getPaymentStatus())) {
                try {
                    booking booking = bookingService.getBookingById(processedPayment.getBookingId())
                            .orElseThrow(() -> new RuntimeException("Booking not found"));
                    booking.setStatus("PAID");
                    bookingService.updateBooking(processedPayment.getBookingId(), booking);
                } catch (Exception e) {
                    System.err.println("Failed to update booking status: " + e.getMessage());
                    // Continue with payment response even if booking update fails
                }
            }
            
            // Create response with payment details and success flag
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("payment", processedPayment);
            response.put("message", "Payment processed successfully");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Payment processing failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/bank-transfer")
    public ResponseEntity<Map<String, Object>> processBankTransfer(
            @RequestParam("paymentSlip") MultipartFile file,
            @RequestParam("paymentData") String paymentDataJson) {
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> paymentDataMap = mapper.readValue(paymentDataJson, Map.class);
            
            // Create payment record for bank transfer
            payment bankPayment = new payment();
            bankPayment.setCustomerName((String) paymentDataMap.get("customerName"));
            bankPayment.setEmail((String) paymentDataMap.get("email"));
            bankPayment.setUserEmail((String) paymentDataMap.get("email"));
            bankPayment.setPaymentMethod("BANK_TRANSFER");
            bankPayment.setAmount(Double.valueOf(paymentDataMap.get("amount").toString()));
            bankPayment.setCurrency((String) paymentDataMap.get("currency"));
            bankPayment.setPaymentStatus("PENDING_VERIFICATION");
            
            if (paymentDataMap.get("bookingId") != null) {
                bankPayment.setBookingId(Long.valueOf(paymentDataMap.get("bookingId").toString()));
            }
            
            // Save payment
            payment savedPayment = paymentService.processPayment(bankPayment);
            
            // Save payment slip
            Long paymentId = savedPayment.getId();
            Long bookingId = savedPayment.getBookingId();
            String transferReference = (String) paymentDataMap.get("transferReference");
            String uploadedBy = (String) paymentDataMap.get("customerName");
            
            paymentSlip slip = paymentSlipService.savePaymentSlip(file, paymentId, bookingId, 
                                                               transferReference, uploadedBy);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment slip uploaded successfully! Added to review to admin.");
            response.put("payment", savedPayment);
            response.put("paymentSlip", slip);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to process bank transfer: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<payment> getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/email/{email}")
    public ResponseEntity<List<payment>> getPaymentsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(paymentService.getPaymentsByEmail(email));
    }
    
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<payment>> getPaymentsByBookingId(@PathVariable Long bookingId) {
        List<payment> payments = paymentService.getPaymentsByBookingId(bookingId);
        return ResponseEntity.ok(payments);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.ok("Payment deleted successfully");
    }
}


