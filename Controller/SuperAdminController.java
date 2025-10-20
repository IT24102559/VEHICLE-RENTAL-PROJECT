package org.example.rentalsytsem.controller;

import org.example.rentalsytsem.service.AdminService;
import org.example.rentalsytsem.service.UserManagementService;
import org.example.rentalsytsem.service.InquiryManagementService;
import org.example.rentalsytsem.service.ReportingService;
import org.example.rentalsytsem.entity.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/super-admin")
@CrossOrigin(origins = "*") // Allow frontend calls from any domain
public class SuperAdminController {

    @Autowired
    private AdminService adminService;
    
    @Autowired
    private UserManagementService userManagementService;
    
    @Autowired
    private InquiryManagementService inquiryManagementService;
    
    @Autowired
    private ReportingService reportingService;

    // ========== Dashboard Endpoints ==========
    
    @GetMapping("/dashboard/summary")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        try {
            Map<String, Object> summary = adminService.getDashboardSummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch dashboard summary: " + e.getMessage()));
        }
    }

    @GetMapping("/dashboard/recent-activities")
    public ResponseEntity<Map<String, Object>> getRecentActivities() {
        try {
            Map<String, Object> activities = adminService.getRecentActivities();
            return ResponseEntity.ok(activities);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch recent activities: " + e.getMessage()));
        }
    }

    @GetMapping("/dashboard/system-health")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        try {
            Map<String, Object> health = adminService.getSystemHealth();
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch system health: " + e.getMessage()));
        }
    }

    @GetMapping("/dashboard/statistics")
    public ResponseEntity<Map<String, Object>> getMonthlyStatistics() {
        try {
            Map<String, Object> stats = adminService.getMonthlyStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch statistics: " + e.getMessage()));
        }
    }

    // ========== User Management Endpoints ==========
    
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        try {
            Map<String, Object> users = userManagementService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch users: " + e.getMessage()));
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        try {
            Map<String, Object> user = userManagementService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch user: " + e.getMessage()));
        }
    }

    @GetMapping("/users/search")
    public ResponseEntity<Map<String, Object>> searchUser(@RequestParam String username) {
        try {
            Map<String, Object> user = userManagementService.searchUserByUsername(username);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to search user: " + e.getMessage()));
        }
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody user updatedUser) {
        try {
            Map<String, Object> result = userManagementService.updateUser(id, updatedUser);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user: " + e.getMessage()));
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        try {
            Map<String, Object> result = userManagementService.deleteUser(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete user: " + e.getMessage()));
        }
    }

    @GetMapping("/users/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        try {
            Map<String, Object> stats = userManagementService.getUserStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch user statistics: " + e.getMessage()));
        }
    }

    @PutMapping("/users/bulk-status")
    public ResponseEntity<Map<String, Object>> bulkUpdateUserStatus(
            @RequestParam List<Long> userIds, 
            @RequestParam String status) {
        try {
            Map<String, Object> result = userManagementService.bulkUpdateUserStatus(userIds, status);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to bulk update users: " + e.getMessage()));
        }
    }

    // ========== Inquiry Management Endpoints ==========
    
    @GetMapping("/inquiries")
    public ResponseEntity<Map<String, Object>> getAllInquiries() {
        try {
            Map<String, Object> inquiries = inquiryManagementService.getAllInquiries();
            return ResponseEntity.ok(inquiries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch inquiries: " + e.getMessage()));
        }
    }

    @GetMapping("/inquiries/{id}")
    public ResponseEntity<Map<String, Object>> getInquiryById(@PathVariable Long id) {
        try {
            Map<String, Object> inquiry = inquiryManagementService.getInquiryById(id);
            return ResponseEntity.ok(inquiry);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch inquiry: " + e.getMessage()));
        }
    }

    @PutMapping("/inquiries/{id}")
    public ResponseEntity<Map<String, Object>> updateInquiry(
            @PathVariable Long id,
            @RequestParam(required = false) String response,
            @RequestParam(required = false) String status) {
        try {
            Map<String, Object> result = inquiryManagementService.updateInquiry(id, response, status);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update inquiry: " + e.getMessage()));
        }
    }

    @DeleteMapping("/inquiries/{id}")
    public ResponseEntity<Map<String, Object>> deleteInquiry(@PathVariable Long id) {
        try {
            Map<String, Object> result = inquiryManagementService.deleteInquiry(id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete inquiry: " + e.getMessage()));
        }
    }

    @GetMapping("/inquiries/statistics")
    public ResponseEntity<Map<String, Object>> getInquiryStatistics() {
        try {
            Map<String, Object> stats = inquiryManagementService.getInquiryStatistics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch inquiry statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/inquiries/recent")
    public ResponseEntity<Map<String, Object>> getRecentInquiries() {
        try {
            Map<String, Object> inquiries = inquiryManagementService.getRecentInquiries();
            return ResponseEntity.ok(inquiries);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch recent inquiries: " + e.getMessage()));
        }
    }

    @PutMapping("/inquiries/bulk-status")
    public ResponseEntity<Map<String, Object>> bulkUpdateInquiryStatus(
            @RequestParam List<Long> inquiryIds,
            @RequestParam String status) {
        try {
            Map<String, Object> result = inquiryManagementService.bulkUpdateInquiryStatus(inquiryIds, status);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to bulk update inquiries: " + e.getMessage()));
        }
    }

    // ========== Reporting Endpoints ==========
    
    @GetMapping("/reports/system")
    public ResponseEntity<Map<String, Object>> getSystemReport() {
        try {
            Map<String, Object> report = reportingService.generateSystemReport();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate system report: " + e.getMessage()));
        }
    }

    @GetMapping("/reports/user-activity")
    public ResponseEntity<Map<String, Object>> getUserActivityReport() {
        try {
            Map<String, Object> report = reportingService.generateUserActivityReport();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate user activity report: " + e.getMessage()));
        }
    }

    @GetMapping("/reports/vehicle-utilization")
    public ResponseEntity<Map<String, Object>> getVehicleUtilizationReport() {
        try {
            Map<String, Object> report = reportingService.generateVehicleUtilizationReport();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate vehicle utilization report: " + e.getMessage()));
        }
    }

    @GetMapping("/reports/financial")
    public ResponseEntity<Map<String, Object>> getFinancialReport() {
        try {
            Map<String, Object> report = reportingService.generateFinancialReport();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate financial report: " + e.getMessage()));
        }
    }

    @GetMapping("/reports/feedback")
    public ResponseEntity<Map<String, Object>> getFeedbackReport() {
        try {
            Map<String, Object> report = reportingService.generateFeedbackReport();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate feedback report: " + e.getMessage()));
        }
    }

    @GetMapping("/reports/date-range")
    public ResponseEntity<Map<String, Object>> getDateRangeReport(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            Map<String, Object> report = reportingService.generateDateRangeReport(start, end);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to generate date range report: " + e.getMessage()));
        }
    }

    @GetMapping("/reports/types")
    public ResponseEntity<Map<String, Object>> getAvailableReportTypes() {
        try {
            Map<String, Object> types = reportingService.getAvailableReportTypes();
            return ResponseEntity.ok(types);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch report types: " + e.getMessage()));
        }
    }

    @GetMapping("/reports/export/{reportType}")
    public ResponseEntity<Map<String, Object>> exportReport(@PathVariable String reportType) {
        try {
            Map<String, Object> exportData = reportingService.exportReportData(reportType);
            return ResponseEntity.ok(exportData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to export report: " + e.getMessage()));
        }
    }
  
    
    @GetMapping("/pages/dashboard")
    public String getDashboardPage() {
        return "redirect:/admin/super/super-admin-dashboard.html";
    }

    @GetMapping("/pages/users")
    public String getUsersPage() {
        return "redirect:/admin/super/super-admin-users.html";
    }

    @GetMapping("/pages/inquiries")
    public String getInquiriesPage() {
        return "redirect:/admin/super/super-admin-inquiries.html";
    }

    @GetMapping("/pages/reports")
    public String getReportsPage() {
        return "redirect:/admin/super/super-admin-reports.html";
    }
}
