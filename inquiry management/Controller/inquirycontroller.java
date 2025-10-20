package org.example.rentalsytsem.controller;

import org.example.rentalsytsem.entity.inquiry;
import org.example.rentalsytsem.service.inquiryservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/api/inquiries")
@CrossOrigin(origins = "*")
public class inquirycontroller {

    @Autowired
    private inquiryservice inquiryService;

    // User endpoints for inquiry management

    @PostMapping("/create")
    public ResponseEntity<?> createInquiry(@RequestBody Map<String, String> requestData, HttpSession session) {
        try {
            // Get user ID from session
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Please log in to submit an inquiry"));
            }

            String title = requestData.get("title");
            String description = requestData.get("description");

            if (title == null || title.trim().isEmpty() || description == null || description.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Title and description are required"));
            }

            Map<String, Object> result = inquiryService.createInquiry(title.trim(), description.trim(), userId);
            
            if ("success".equals(result.get("status"))) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "Failed to create inquiry: " + e.getMessage()));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserInquiries(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Please log in to view your inquiries"));
            }

            Map<String, Object> result = inquiryService.getUserInquiries(userId);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "Failed to fetch inquiries: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{inquiryId}")
    public ResponseEntity<?> getInquiryById(@PathVariable Long inquiryId, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Please log in to view inquiry details"));
            }

            Map<String, Object> result = inquiryService.getInquiryById(inquiryId, userId);
            
            if ("success".equals(result.get("status"))) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "Failed to fetch inquiry: " + e.getMessage()));
        }
    }

    @PutMapping("/user/{inquiryId}")
    public ResponseEntity<?> updateInquiry(@PathVariable Long inquiryId, 
                                         @RequestBody Map<String, String> requestData, 
                                         HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Please log in to update inquiry"));
            }

            String title = requestData.get("title");
            String description = requestData.get("description");

            if (title == null || title.trim().isEmpty() || description == null || description.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Title and description are required"));
            }

            Map<String, Object> result = inquiryService.updateInquiry(inquiryId, title.trim(), description.trim(), userId);
            
            if ("success".equals(result.get("status"))) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "Failed to update inquiry: " + e.getMessage()));
        }
    }

    @DeleteMapping("/user/{inquiryId}")
    public ResponseEntity<?> deleteInquiry(@PathVariable Long inquiryId, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Please log in to delete inquiry"));
            }

            Map<String, Object> result = inquiryService.deleteInquiry(inquiryId, userId);
            
            if ("success".equals(result.get("status"))) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "Failed to delete inquiry: " + e.getMessage()));
        }
    }

    // Admin endpoints for inquiry management

    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllInquiries(HttpSession session) {
        try {
            String userRole = (String) session.getAttribute("role");
            if (!"Admin".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Admin access required"));
            }

            Map<String, Object> result = inquiryService.getAllInquiries();
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "Failed to fetch inquiries: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/status/{status}")
    public ResponseEntity<?> getInquiriesByStatus(@PathVariable String status, HttpSession session) {
        try {
            String userRole = (String) session.getAttribute("role");
            if (!"Admin".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Admin access required"));
            }

            inquiry.InquiryStatus inquiryStatus;
            try {
                inquiryStatus = inquiry.InquiryStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Invalid status: " + status));
            }

            Map<String, Object> result = inquiryService.getInquiriesByStatus(inquiryStatus);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "Failed to fetch inquiries: " + e.getMessage()));
        }
    }

    @PutMapping("/admin/{inquiryId}/status")
    public ResponseEntity<?> updateInquiryStatus(@PathVariable Long inquiryId,
                                               @RequestBody Map<String, String> requestData,
                                               HttpSession session) {
        try {
            String userRole = (String) session.getAttribute("role");
            String adminUsername = (String) session.getAttribute("username");
            
            if (!"Admin".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Admin access required"));
            }

            String status = requestData.get("status");
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Status is required"));
            }

            inquiry.InquiryStatus inquiryStatus;
            try {
                inquiryStatus = inquiry.InquiryStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Invalid status: " + status));
            }

            Map<String, Object> result = inquiryService.updateInquiryStatus(inquiryId, inquiryStatus, adminUsername);
            
            if ("success".equals(result.get("status"))) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "Failed to update inquiry status: " + e.getMessage()));
        }
    }

    @PostMapping("/admin/{inquiryId}/respond")
    public ResponseEntity<?> respondToInquiry(@PathVariable Long inquiryId,
                                            @RequestBody Map<String, String> requestData,
                                            HttpSession session) {
        try {
            String userRole = (String) session.getAttribute("role");
            String adminUsername = (String) session.getAttribute("username");
            
            if (!"Admin".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Admin access required"));
            }

            String response = requestData.get("response");
            if (response == null || response.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Response is required"));
            }

            Map<String, Object> result = inquiryService.respondToInquiry(inquiryId, response.trim(), adminUsername);
            
            if ("success".equals(result.get("status"))) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "Failed to respond to inquiry: " + e.getMessage()));
        }
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<?> getDashboardStats(HttpSession session) {
        try {
            String userRole = (String) session.getAttribute("role");
            if (!"Admin".equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Admin access required"));
            }

            Map<String, Object> result = inquiryService.getDashboardStats();
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "Failed to fetch dashboard stats: " + e.getMessage()));
        }
    }
}
