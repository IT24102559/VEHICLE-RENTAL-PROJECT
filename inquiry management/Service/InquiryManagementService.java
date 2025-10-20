package org.example.rentalsytsem.service;

import org.example.rentalsytsem.entity.inquiry;
import org.example.rentalsytsem.repository.inquiryrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@Service
public class InquiryManagementService {
    
    @Autowired
    private inquiryrepository inquiryRepository;

    /**
     * Get all inquiries
     */
    public Map<String, Object> getAllInquiries() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<inquiry> inquiries = inquiryRepository.findAll();
            
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
            
            result.put("inquiries", inquiryDataList);
            result.put("totalInquiries", inquiries.size());
            result.put("lastUpdated", LocalDateTime.now());
            
        } catch (Exception e) {
            result.put("error", "Error fetching inquiries: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Get inquiry by ID
     */
    public Map<String, Object> getInquiryById(Long inquiryId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<inquiry> inquiryOpt = inquiryRepository.findById(inquiryId);
            if (inquiryOpt.isPresent()) {
                inquiry inq = inquiryOpt.get();
                
                // Create clean inquiry data structure
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
                
                result.put("inquiry", inquiryData);
                result.put("found", true);
            } else {
                result.put("found", false);
                result.put("message", "Inquiry not found");
            }
            
        } catch (Exception e) {
            result.put("error", "Error fetching inquiry: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Update inquiry status and response
     */
    public Map<String, Object> updateInquiry(Long inquiryId, String response, String status) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<inquiry> inquiryOpt = inquiryRepository.findById(inquiryId);
            if (inquiryOpt.isPresent()) {
                inquiry existingInquiry = inquiryOpt.get();
                
                // Update fields based on your inquiry entity structure
                if (response != null && !response.trim().isEmpty()) {
                    existingInquiry.setResponse(response);
                    // Set response date and admin
                    existingInquiry.setRespondedAt(LocalDateTime.now());
                    // For now, set a generic admin. In production, get from session
                    existingInquiry.setRespondedBy("admin");
                    // AUTOMATICALLY set status to RESPONDED when admin provides a response
                    existingInquiry.setStatus(inquiry.InquiryStatus.RESPONDED);
                } else if (status != null) {
                    // Only allow manual status change if no response is being added
                    // Convert string to enum if needed
                    try {
                        inquiry.InquiryStatus inquiryStatus = inquiry.InquiryStatus.valueOf(status.toUpperCase());
                        existingInquiry.setStatus(inquiryStatus);
                    } catch (IllegalArgumentException e) {
                        result.put("error", "Invalid status: " + status);
                        return result;
                    }
                }
                
                inquiry savedInquiry = inquiryRepository.save(existingInquiry);
                
                // Create clean inquiry data structure
                Map<String, Object> inquiryData = new HashMap<>();
                inquiryData.put("id", savedInquiry.getId());
                inquiryData.put("title", savedInquiry.getTitle());
                inquiryData.put("description", savedInquiry.getDescription());
                inquiryData.put("status", savedInquiry.getStatus().toString());
                inquiryData.put("priority", savedInquiry.getPriority() != null ? savedInquiry.getPriority().toString() : "MEDIUM");
                inquiryData.put("createdAt", savedInquiry.getCreatedAt());
                inquiryData.put("response", savedInquiry.getResponse());
                inquiryData.put("respondedAt", savedInquiry.getRespondedAt());
                inquiryData.put("respondedBy", savedInquiry.getRespondedBy());
                
                // Add user information without circular reference
                if (savedInquiry.getUser() != null) {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("id", savedInquiry.getUser().getId());
                    userData.put("username", savedInquiry.getUser().getUsername());
                    userData.put("firstName", savedInquiry.getUser().getFirstName());
                    userData.put("lastName", savedInquiry.getUser().getLastName());
                    userData.put("email", savedInquiry.getUser().getEmail());
                    inquiryData.put("user", userData);
                }
                
                result.put("inquiry", inquiryData);
                result.put("success", true);
                result.put("message", "Inquiry updated successfully");
                
            } else {
                result.put("success", false);
                result.put("message", "Inquiry not found");
            }
            
        } catch (Exception e) {
            result.put("error", "Error updating inquiry: " + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }

    /**
     * Delete inquiry by ID
     */
    public Map<String, Object> deleteInquiry(Long inquiryId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<inquiry> inquiryOpt = inquiryRepository.findById(inquiryId);
            if (inquiryOpt.isPresent()) {
                inquiryRepository.deleteById(inquiryId);
                result.put("success", true);
                result.put("message", "Inquiry deleted successfully");
            } else {
                result.put("success", false);
                result.put("message", "Inquiry not found");
            }
            
        } catch (Exception e) {
            result.put("error", "Error deleting inquiry: " + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }

    /**
     * Get inquiry statistics
     */
    public Map<String, Object> getInquiryStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            long totalInquiries = inquiryRepository.count();
            List<inquiry> allInquiries = inquiryRepository.findAll();
            
            // Count inquiries by status
            long pendingCount = 0;
            long respondedCount = 0;
            long closedCount = 0;
            
            for (inquiry inq : allInquiries) {
                if (inq.getStatus() != null) {
                    switch (inq.getStatus()) {
                        case PENDING:
                            pendingCount++;
                            break;
                        case IN_PROGRESS:
                            pendingCount++; // Count as pending for simplicity
                            break;
                        case RESPONDED:
                            respondedCount++;
                            break;
                        case RESOLVED:
                            respondedCount++; // Count as responded for simplicity
                            break;
                        case CLOSED:
                            closedCount++;
                            break;
                    }
                }
            }
            
            stats.put("totalInquiries", totalInquiries);
            stats.put("pendingInquiries", pendingCount);
            stats.put("respondedInquiries", respondedCount);
            stats.put("closedInquiries", closedCount);
            stats.put("lastUpdated", LocalDateTime.now());
            
        } catch (Exception e) {
            stats.put("error", "Error fetching inquiry statistics: " + e.getMessage());
        }
        
        return stats;
    }

    /**
     * Get recent inquiries (last 10)
     */
    public Map<String, Object> getRecentInquiries() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<inquiry> allInquiries = inquiryRepository.findAll();
            
            // In a real implementation, you'd use a repository method like:
            // List<inquiry> recentInquiries = inquiryRepository.findTop10ByOrderByCreatedDateDesc();
            // For now, we'll return all inquiries
            
            result.put("inquiries", allInquiries);
            result.put("count", Math.min(10, allInquiries.size()));
            result.put("lastUpdated", LocalDateTime.now());
            
        } catch (Exception e) {
            result.put("error", "Error fetching recent inquiries: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Bulk status update for multiple inquiries
     */
    public Map<String, Object> bulkUpdateInquiryStatus(List<Long> inquiryIds, String status) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        
        try {
            inquiry.InquiryStatus inquiryStatus = inquiry.InquiryStatus.valueOf(status.toUpperCase());
            
            for (Long inquiryId : inquiryIds) {
                try {
                    Optional<inquiry> inquiryOpt = inquiryRepository.findById(inquiryId);
                    if (inquiryOpt.isPresent()) {
                        inquiry inq = inquiryOpt.get();
                        inq.setStatus(inquiryStatus);
                        inquiryRepository.save(inq);
                        successCount++;
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                }
            }
            
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("message", String.format("Updated %d inquiries, %d failed", successCount, failCount));
            
        } catch (IllegalArgumentException e) {
            result.put("error", "Invalid status: " + status);
        } catch (Exception e) {
            result.put("error", "Error in bulk update: " + e.getMessage());
        }
        
        return result;
    }
}