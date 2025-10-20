package org.example.rentalsytsem.service;

import org.example.rentalsytsem.entity.inquiry;
import org.example.rentalsytsem.entity.user;
import org.example.rentalsytsem.repository.inquiryrepository;
import org.example.rentalsytsem.repository.userrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class inquiryservice {

    @Autowired
    private inquiryrepository inquiryRepository;

    @Autowired
    private userrepository userRepository;

    // User Methods - CRUD operations for inquiries

    public Map<String, Object> createInquiry(String title, String description, Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<user> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                response.put("status", "error");
                response.put("message", "User not found");
                return response;
            }

            inquiry newInquiry = new inquiry();
            newInquiry.setTitle(title);
            newInquiry.setDescription(description);
            newInquiry.setUser(userOpt.get());
            newInquiry.setStatus(inquiry.InquiryStatus.PENDING);
            
            inquiry savedInquiry = inquiryRepository.save(newInquiry);
            
            // Create a clean response without circular references
            Map<String, Object> inquiryData = new HashMap<>();
            inquiryData.put("id", savedInquiry.getId());
            inquiryData.put("title", savedInquiry.getTitle());
            inquiryData.put("description", savedInquiry.getDescription());
            inquiryData.put("status", savedInquiry.getStatus().toString());
            inquiryData.put("priority", savedInquiry.getPriority() != null ? savedInquiry.getPriority().toString() : "MEDIUM");
            inquiryData.put("createdAt", savedInquiry.getCreatedAt());
            
            response.put("status", "success");
            response.put("message", "Inquiry submitted successfully");
            response.put("inquiry", inquiryData);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to create inquiry: " + e.getMessage());
        }
        
        return response;
    }

    public Map<String, Object> getUserInquiries(Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<inquiry> inquiries = inquiryRepository.findByUserIdOrderByCreatedAtDesc(userId);
            
            // Convert inquiries to clean data structure
            List<Map<String, Object>> inquiryDataList = inquiries.stream().map(inq -> {
                Map<String, Object> inquiryData = new HashMap<>();
                inquiryData.put("id", inq.getId());
                inquiryData.put("title", inq.getTitle());
                inquiryData.put("description", inq.getDescription());
                inquiryData.put("status", inq.getStatus().toString());
                inquiryData.put("priority", inq.getPriority() != null ? inq.getPriority().toString() : "MEDIUM");
                inquiryData.put("createdAt", inq.getCreatedAt());
                inquiryData.put("response", inq.getResponse());
                inquiryData.put("respondedAt", inq.getRespondedAt());
                inquiryData.put("respondedBy", inq.getRespondedBy());
                return inquiryData;
            }).toList();
            
            response.put("status", "success");
            response.put("inquiries", inquiryDataList);
            response.put("total", inquiries.size());
            
            // Count by status
            Map<String, Long> statusCounts = new HashMap<>();
            statusCounts.put("pending", inquiries.stream().filter(i -> i.getStatus() == inquiry.InquiryStatus.PENDING).count());
            statusCounts.put("in_progress", inquiries.stream().filter(i -> i.getStatus() == inquiry.InquiryStatus.IN_PROGRESS).count());
            statusCounts.put("responded", inquiries.stream().filter(i -> i.getStatus() == inquiry.InquiryStatus.RESPONDED).count());
            statusCounts.put("resolved", inquiries.stream().filter(i -> i.getStatus() == inquiry.InquiryStatus.RESOLVED).count());
            statusCounts.put("closed", inquiries.stream().filter(i -> i.getStatus() == inquiry.InquiryStatus.CLOSED).count());
            
            response.put("statusCounts", statusCounts);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to fetch inquiries: " + e.getMessage());
        }
        
        return response;
    }

    public Map<String, Object> getInquiryById(Long inquiryId, Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<inquiry> inquiryOpt = inquiryRepository.findById(inquiryId);
            
            if (!inquiryOpt.isPresent()) {
                response.put("status", "error");
                response.put("message", "Inquiry not found");
                return response;
            }
            
            inquiry inquiryObj = inquiryOpt.get();
            
            // Check if the inquiry belongs to the user
            if (!inquiryObj.getUser().getId().equals(userId)) {
                response.put("status", "error");
                response.put("message", "Access denied");
                return response;
            }
            
            // Create a clean response without circular references
            Map<String, Object> inquiryData = new HashMap<>();
            inquiryData.put("id", inquiryObj.getId());
            inquiryData.put("title", inquiryObj.getTitle());
            inquiryData.put("description", inquiryObj.getDescription());
            inquiryData.put("status", inquiryObj.getStatus().toString());
            inquiryData.put("priority", inquiryObj.getPriority() != null ? inquiryObj.getPriority().toString() : "MEDIUM");
            inquiryData.put("createdAt", inquiryObj.getCreatedAt());
            inquiryData.put("response", inquiryObj.getResponse());
            inquiryData.put("respondedAt", inquiryObj.getRespondedAt());
            inquiryData.put("respondedBy", inquiryObj.getRespondedBy());
            
            response.put("status", "success");
            response.put("inquiry", inquiryData);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to fetch inquiry: " + e.getMessage());
        }
        
        return response;
    }

    public Map<String, Object> updateInquiry(Long inquiryId, String title, String description, Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<inquiry> inquiryOpt = inquiryRepository.findById(inquiryId);
            
            if (!inquiryOpt.isPresent()) {
                response.put("status", "error");
                response.put("message", "Inquiry not found");
                return response;
            }
            
            inquiry inquiryObj = inquiryOpt.get();
            
            // Check if the inquiry belongs to the user
            if (!inquiryObj.getUser().getId().equals(userId)) {
                response.put("status", "error");
                response.put("message", "Access denied");
                return response;
            }
            
            // Only allow updates if inquiry is still pending
            if (inquiryObj.getStatus() != inquiry.InquiryStatus.PENDING) {
                response.put("status", "error");
                response.put("message", "Cannot update inquiry that is already being processed");
                return response;
            }
            
            inquiryObj.setTitle(title);
            inquiryObj.setDescription(description);
            
            inquiry updatedInquiry = inquiryRepository.save(inquiryObj);
            
            // Create a clean response without circular references
            Map<String, Object> inquiryData = new HashMap<>();
            inquiryData.put("id", updatedInquiry.getId());
            inquiryData.put("title", updatedInquiry.getTitle());
            inquiryData.put("description", updatedInquiry.getDescription());
            inquiryData.put("status", updatedInquiry.getStatus().toString());
            inquiryData.put("createdAt", updatedInquiry.getCreatedAt());
            inquiryData.put("response", updatedInquiry.getResponse());
            inquiryData.put("respondedAt", updatedInquiry.getRespondedAt());
            inquiryData.put("respondedBy", updatedInquiry.getRespondedBy());
            
            response.put("status", "success");
            response.put("message", "Inquiry updated successfully");
            response.put("inquiry", inquiryData);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to update inquiry: " + e.getMessage());
        }
        
        return response;
    }

    public Map<String, Object> deleteInquiry(Long inquiryId, Long userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<inquiry> inquiryOpt = inquiryRepository.findById(inquiryId);
            
            if (!inquiryOpt.isPresent()) {
                response.put("status", "error");
                response.put("message", "Inquiry not found");
                return response;
            }
            
            inquiry inquiryObj = inquiryOpt.get();
            
            // Check if the inquiry belongs to the user
            if (!inquiryObj.getUser().getId().equals(userId)) {
                response.put("status", "error");
                response.put("message", "Access denied");
                return response;
            }
            
            // Only allow deletion if inquiry is still pending
            if (inquiryObj.getStatus() != inquiry.InquiryStatus.PENDING) {
                response.put("status", "error");
                response.put("message", "Cannot delete inquiry that is already being processed");
                return response;
            }
            
            inquiryRepository.delete(inquiryObj);
            
            response.put("status", "success");
            response.put("message", "Inquiry deleted successfully");
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to delete inquiry: " + e.getMessage());
        }
        
        return response;
    }

    // Admin Methods - Management and response functionality

    public Map<String, Object> getAllInquiries() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<inquiry> inquiries = inquiryRepository.findAllByOrderByCreatedAtDesc();
            
            // Convert inquiries to clean data structure
            List<Map<String, Object>> inquiryDataList = inquiries.stream().map(inq -> {
                Map<String, Object> inquiryData = new HashMap<>();
                inquiryData.put("id", inq.getId());
                inquiryData.put("title", inq.getTitle());
                inquiryData.put("description", inq.getDescription());
                inquiryData.put("status", inq.getStatus().toString());
                inquiryData.put("priority", inq.getPriority() != null ? inq.getPriority().toString() : "MEDIUM");
                inquiryData.put("createdAt", inq.getCreatedAt());
                inquiryData.put("response", inq.getResponse());
                inquiryData.put("respondedAt", inq.getRespondedAt());
                inquiryData.put("respondedBy", inq.getRespondedBy());
                
                // Add user information without circular reference
                if (inq.getUser() != null) {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("id", inq.getUser().getId());
                    userData.put("username", inq.getUser().getUsername());
                    userData.put("firstName", inq.getUser().getFirstName());
                    userData.put("lastName", inq.getUser().getLastName());
                    userData.put("email", inq.getUser().getEmail());
                    inquiryData.put("user", userData);
                }
                
                return inquiryData;
            }).toList();
            
            response.put("status", "success");
            response.put("inquiries", inquiryDataList);
            response.put("total", inquiries.size());
            
            // Count by status
            Map<String, Long> statusCounts = new HashMap<>();
            statusCounts.put("pending", inquiries.stream().filter(i -> i.getStatus() == inquiry.InquiryStatus.PENDING).count());
            statusCounts.put("in_progress", inquiries.stream().filter(i -> i.getStatus() == inquiry.InquiryStatus.IN_PROGRESS).count());
            statusCounts.put("responded", inquiries.stream().filter(i -> i.getStatus() == inquiry.InquiryStatus.RESPONDED).count());
            statusCounts.put("resolved", inquiries.stream().filter(i -> i.getStatus() == inquiry.InquiryStatus.RESOLVED).count());
            statusCounts.put("closed", inquiries.stream().filter(i -> i.getStatus() == inquiry.InquiryStatus.CLOSED).count());
            
            response.put("statusCounts", statusCounts);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to fetch inquiries: " + e.getMessage());
        }
        
        return response;
    }

    public Map<String, Object> getInquiriesByStatus(inquiry.InquiryStatus status) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<inquiry> inquiries = inquiryRepository.findByStatusOrderByCreatedAtDesc(status);
            
            // Convert inquiries to clean data structure
            List<Map<String, Object>> inquiryDataList = inquiries.stream().map(inq -> {
                Map<String, Object> inquiryData = new HashMap<>();
                inquiryData.put("id", inq.getId());
                inquiryData.put("title", inq.getTitle());
                inquiryData.put("description", inq.getDescription());
                inquiryData.put("status", inq.getStatus().toString());
                inquiryData.put("createdAt", inq.getCreatedAt());
                inquiryData.put("response", inq.getResponse());
                inquiryData.put("respondedAt", inq.getRespondedAt());
                inquiryData.put("respondedBy", inq.getRespondedBy());
                
                // Add user information without circular reference
                if (inq.getUser() != null) {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("id", inq.getUser().getId());
                    userData.put("username", inq.getUser().getUsername());
                    userData.put("firstName", inq.getUser().getFirstName());
                    userData.put("lastName", inq.getUser().getLastName());
                    userData.put("email", inq.getUser().getEmail());
                    inquiryData.put("user", userData);
                }
                
                return inquiryData;
            }).toList();
            
            response.put("status", "success");
            response.put("inquiries", inquiryDataList);
            response.put("total", inquiries.size());
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to fetch inquiries: " + e.getMessage());
        }
        
        return response;
    }

    public Map<String, Object> updateInquiryStatus(Long inquiryId, inquiry.InquiryStatus status, String adminUsername) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<inquiry> inquiryOpt = inquiryRepository.findById(inquiryId);
            
            if (!inquiryOpt.isPresent()) {
                response.put("status", "error");
                response.put("message", "Inquiry not found");
                return response;
            }
            
            inquiry inquiryObj = inquiryOpt.get();
            inquiryObj.setStatus(status);
            
            if (status == inquiry.InquiryStatus.IN_PROGRESS) {
                inquiryObj.setRespondedBy(adminUsername);
            }
            
            inquiry updatedInquiry = inquiryRepository.save(inquiryObj);
            
            // Create clean inquiry data structure
            Map<String, Object> inquiryData = new HashMap<>();
            inquiryData.put("id", updatedInquiry.getId());
            inquiryData.put("title", updatedInquiry.getTitle());
            inquiryData.put("description", updatedInquiry.getDescription());
            inquiryData.put("status", updatedInquiry.getStatus().toString());
            inquiryData.put("createdAt", updatedInquiry.getCreatedAt());
            inquiryData.put("response", updatedInquiry.getResponse());
            inquiryData.put("respondedAt", updatedInquiry.getRespondedAt());
            inquiryData.put("respondedBy", updatedInquiry.getRespondedBy());
            
            // Add user information without circular reference
            if (updatedInquiry.getUser() != null) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", updatedInquiry.getUser().getId());
                userData.put("username", updatedInquiry.getUser().getUsername());
                userData.put("firstName", updatedInquiry.getUser().getFirstName());
                userData.put("lastName", updatedInquiry.getUser().getLastName());
                userData.put("email", updatedInquiry.getUser().getEmail());
                inquiryData.put("user", userData);
            }
            
            response.put("status", "success");
            response.put("message", "Inquiry status updated successfully");
            response.put("inquiry", inquiryData);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to update inquiry status: " + e.getMessage());
        }
        
        return response;
    }

    public Map<String, Object> respondToInquiry(Long inquiryId, String responseText, String adminUsername) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<inquiry> inquiryOpt = inquiryRepository.findById(inquiryId);
            
            if (!inquiryOpt.isPresent()) {
                response.put("status", "error");
                response.put("message", "Inquiry not found");
                return response;
            }
            
            inquiry inquiryObj = inquiryOpt.get();
            inquiryObj.setResponse(responseText);
            inquiryObj.setStatus(inquiry.InquiryStatus.RESPONDED);
            inquiryObj.setRespondedBy(adminUsername);
            inquiryObj.setRespondedAt(LocalDateTime.now());
            
            inquiry updatedInquiry = inquiryRepository.save(inquiryObj);
            
            // Create clean inquiry data structure
            Map<String, Object> inquiryData = new HashMap<>();
            inquiryData.put("id", updatedInquiry.getId());
            inquiryData.put("title", updatedInquiry.getTitle());
            inquiryData.put("description", updatedInquiry.getDescription());
            inquiryData.put("status", updatedInquiry.getStatus().toString());
            inquiryData.put("createdAt", updatedInquiry.getCreatedAt());
            inquiryData.put("response", updatedInquiry.getResponse());
            inquiryData.put("respondedAt", updatedInquiry.getRespondedAt());
            inquiryData.put("respondedBy", updatedInquiry.getRespondedBy());
            
            // Add user information without circular reference
            if (updatedInquiry.getUser() != null) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", updatedInquiry.getUser().getId());
                userData.put("username", updatedInquiry.getUser().getUsername());
                userData.put("firstName", updatedInquiry.getUser().getFirstName());
                userData.put("lastName", updatedInquiry.getUser().getLastName());
                userData.put("email", updatedInquiry.getUser().getEmail());
                inquiryData.put("user", userData);
            }
            
            response.put("status", "success");
            response.put("message", "Response submitted successfully");
            response.put("inquiry", inquiryData);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to respond to inquiry: " + e.getMessage());
        }
        
        return response;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> stats = new HashMap<>();
            
            stats.put("total", inquiryRepository.count());
            stats.put("pending", inquiryRepository.countByStatus(inquiry.InquiryStatus.PENDING));
            stats.put("in_progress", inquiryRepository.countByStatus(inquiry.InquiryStatus.IN_PROGRESS));
            stats.put("resolved", inquiryRepository.countByStatus(inquiry.InquiryStatus.RESOLVED));
            stats.put("closed", inquiryRepository.countByStatus(inquiry.InquiryStatus.CLOSED));
            
            response.put("status", "success");
            response.put("stats", stats);
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to fetch dashboard stats: " + e.getMessage());
        }
        
        return response;
    }
}
