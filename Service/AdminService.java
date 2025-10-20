package org.example.rentalsytsem.service;

import org.example.rentalsytsem.entity.user;
import org.example.rentalsytsem.entity.booking;
import org.example.rentalsytsem.entity.inquiry;
import org.example.rentalsytsem.repository.userrepository;
import org.example.rentalsytsem.repository.bookingrepository;
import org.example.rentalsytsem.repository.vehiclerepository;
import org.example.rentalsytsem.repository.inquiryrepository;
import org.example.rentalsytsem.repository.feedbackrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class AdminService {
    
    @Autowired
    private userrepository userRepository;
    
    @Autowired
    private bookingrepository bookingRepository;
    
    @Autowired
    private vehiclerepository vehicleRepository;
    
    @Autowired
    private inquiryrepository inquiryRepository;
    
    @Autowired
    private feedbackrepository feedbackRepository;

    /**
     * Get dashboard summary with key metrics
     */
    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        try {
            // Basic counts - using existing repository methods
            long totalUsers = userRepository.count();
            long totalBookings = bookingRepository.count();
            long totalVehicles = vehicleRepository.count();
            long totalInquiries = inquiryRepository.count();
            long totalFeedbacks = feedbackRepository.count();
            
            summary.put("totalUsers", totalUsers);
            summary.put("totalBookings", totalBookings);
            summary.put("totalVehicles", totalVehicles);
            summary.put("totalInquiries", totalInquiries);
            summary.put("totalFeedbacks", totalFeedbacks);
            summary.put("lastUpdated", LocalDateTime.now());
            
        } catch (Exception e) {
            summary.put("error", "Error fetching dashboard data: " + e.getMessage());
        }
        
        return summary;
    }

    /**
     * Get recent activities for dashboard
     */
    public Map<String, Object> getRecentActivities() {
        Map<String, Object> activities = new HashMap<>();
        
        try {
            // Get all records - in real implementation you'd want to limit and order these
            List<booking> allBookings = bookingRepository.findAll();
            List<user> allUsers = userRepository.findAll();
            List<inquiry> allInquiries = inquiryRepository.findAll();
            
            // Take last 5 of each for dashboard
            activities.put("recentBookingsCount", Math.min(5, allBookings.size()));
            activities.put("recentUsersCount", Math.min(5, allUsers.size()));
            activities.put("recentInquiriesCount", Math.min(5, allInquiries.size()));
            
        } catch (Exception e) {
            activities.put("error", "Error fetching recent activities: " + e.getMessage());
        }
        
        return activities;
    }

    /**
     * Get system health status
     */
    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Check database connectivity
            boolean dbConnected = userRepository.count() >= 0;
            
            health.put("databaseConnected", dbConnected);
            health.put("lastHealthCheck", LocalDateTime.now());
            health.put("status", dbConnected ? "HEALTHY" : "CRITICAL");
            
        } catch (Exception e) {
            health.put("status", "CRITICAL");
            health.put("error", "Health check failed: " + e.getMessage());
        }
        
        return health;
    }

    /**
     * Get monthly statistics for charts - simplified version
     */
    public Map<String, Object> getMonthlyStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Return basic structure for now - can be enhanced later
            stats.put("totalCounts", getDashboardSummary());
            stats.put("generatedAt", LocalDateTime.now());
            
        } catch (Exception e) {
            stats.put("error", "Error fetching monthly statistics: " + e.getMessage());
        }
        
        return stats;
    }
}
