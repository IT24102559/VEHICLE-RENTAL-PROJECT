package org.example.rentalsytsem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.rentalsytsem.entity.paymentSlip;
import org.example.rentalsytsem.service.paymentSlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payment-slips")
@CrossOrigin(origins = "*")
public class paymentSlipController {
    
    @Autowired
    private paymentSlipService service;
    
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadPaymentSlip(
            @RequestParam("paymentSlip") MultipartFile file,
            @RequestParam("paymentData") String paymentDataJson) {
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> paymentData = mapper.readValue(paymentDataJson, Map.class);
            
            Long paymentId = paymentData.get("paymentId") != null ? 
                Long.valueOf(paymentData.get("paymentId").toString()) : null;
            Long bookingId = paymentData.get("bookingId") != null ? 
                Long.valueOf(paymentData.get("bookingId").toString()) : null;
            String transferReference = (String) paymentData.get("transferReference");
            String uploadedBy = (String) paymentData.get("customerName");
            
            paymentSlip slip = service.savePaymentSlip(file, paymentId, bookingId, 
                                                     transferReference, uploadedBy);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment slip uploaded successfully");
            response.put("paymentSlip", slip);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to upload payment slip: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping
    public ResponseEntity<List<paymentSlip>> getAllPaymentSlips() {
        try {
            List<paymentSlip> slips = service.getAllPaymentSlips();
            System.out.println("Retrieved " + slips.size() + " payment slips");
            return ResponseEntity.ok(slips);
        } catch (Exception e) {
            System.err.println("Error fetching payment slips: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<paymentSlip>> getPaymentSlipsByStatus(@PathVariable String status) {
        List<paymentSlip> slips = service.getPaymentSlipsByStatus(status);
        return ResponseEntity.ok(slips);
    }
    
        @GetMapping("/{id}")
    public ResponseEntity<paymentSlip> getPaymentSlip(@PathVariable Long id) {
        try {
            paymentSlip slip = service.getPaymentSlipById(id);
            return ResponseEntity.ok(slip);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<paymentSlip> getPaymentSlipByBookingId(@PathVariable Long bookingId) {
        try {
            paymentSlip slip = service.getPaymentSlipByBookingId(bookingId);
            return ResponseEntity.ok(slip);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadPaymentSlip(@PathVariable Long id) {
        try {
            paymentSlip slip = service.getPaymentSlipById(id);
            
            byte[] fileData = service.getPaymentSlipFile(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(slip.getFileType()));
            headers.setContentDispositionFormData("attachment", slip.getFileName());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileData);
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/{id}/approve")
    public ResponseEntity<paymentSlip> approvePaymentSlip(@PathVariable Long id, 
                                                         @RequestBody Map<String, String> request) {
        try {
            String reviewedBy = request.get("reviewedBy");
            String reviewNotes = request.get("reviewNotes");
            
            paymentSlip approved = service.approvePaymentSlip(id, reviewedBy, reviewNotes);
            return ResponseEntity.ok(approved);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/reject")
    public ResponseEntity<paymentSlip> rejectPaymentSlip(@PathVariable Long id, 
                                                        @RequestBody Map<String, String> request) {
        try {
            String reviewedBy = request.get("reviewedBy");
            String reviewNotes = request.get("reviewNotes");
            
            paymentSlip rejected = service.rejectPaymentSlip(id, reviewedBy, reviewNotes);
            return ResponseEntity.ok(rejected);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaymentSlip(@PathVariable Long id) {
        try {
            service.deletePaymentSlip(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
