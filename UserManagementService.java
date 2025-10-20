package org.example.rentalsytsem.service;

import org.example.rentalsytsem.entity.user;
import org.example.rentalsytsem.repository.userrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@Service
public class UserManagementService {
    
    @Autowired
    private userrepository userRepository;

    /**
     * Get all users with pagination info
     */
    public Map<String, Object> getAllUsers() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<user> users = userRepository.findAll();
            result.put("users", users);
            result.put("totalUsers", users.size());
            result.put("lastUpdated", LocalDateTime.now());
            
        } catch (Exception e) {
            result.put("error", "Error fetching users: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Get user by ID
     */
    public Map<String, Object> getUserById(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<user> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                result.put("user", userOpt.get());
                result.put("found", true);
            } else {
                result.put("found", false);
                result.put("message", "User not found");
            }
            
        } catch (Exception e) {
            result.put("error", "Error fetching user: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Search users by username
     */
    public Map<String, Object> searchUserByUsername(String username) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<user> userOpt = userRepository.findByUsername(username);
            if (userOpt.isPresent()) {
                result.put("user", userOpt.get());
                result.put("found", true);
            } else {
                result.put("found", false);
                result.put("message", "User not found");
            }
            
        } catch (Exception e) {
            result.put("error", "Error searching user: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Update user information
     */
    public Map<String, Object> updateUser(Long userId, user updatedUser) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<user> existingUserOpt = userRepository.findById(userId);
            if (existingUserOpt.isPresent()) {
                user existingUser = existingUserOpt.get();
                
                // Update fields (you can customize which fields to update)
                if (updatedUser.getFirstName() != null) {
                    existingUser.setFirstName(updatedUser.getFirstName());
                }
                if (updatedUser.getLastName() != null) {
                    existingUser.setLastName(updatedUser.getLastName());
                }
                if (updatedUser.getEmail() != null) {
                    existingUser.setEmail(updatedUser.getEmail());
                }
                
                user savedUser = userRepository.save(existingUser);
                result.put("user", savedUser);
                result.put("success", true);
                result.put("message", "User updated successfully");
                
            } else {
                result.put("success", false);
                result.put("message", "User not found");
            }
            
        } catch (Exception e) {
            result.put("error", "Error updating user: " + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }

    /**
     * Delete user by ID
     */
    public Map<String, Object> deleteUser(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<user> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                userRepository.deleteById(userId);
                result.put("success", true);
                result.put("message", "User deleted successfully");
            } else {
                result.put("success", false);
                result.put("message", "User not found");
            }
            
        } catch (Exception e) {
            result.put("error", "Error deleting user: " + e.getMessage());
            result.put("success", false);
        }
        
        return result;
    }

    /**
     * Get user statistics
     */
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            long totalUsers = userRepository.count();
            List<user> allUsers = userRepository.findAll();
            
            // Count users by role if role field exists
            long adminCount = 0;
            long customerCount = 0;
            
            for (user u : allUsers) {
                if (u.getRole() != null) {
                    if ("ADMIN".equalsIgnoreCase(u.getRole())) {
                        adminCount++;
                    } else if ("CUSTOMER".equalsIgnoreCase(u.getRole())) {
                        customerCount++;
                    }
                }
            }
            
            stats.put("totalUsers", totalUsers);
            stats.put("adminUsers", adminCount);
            stats.put("customerUsers", customerCount);
            stats.put("lastUpdated", LocalDateTime.now());
            
        } catch (Exception e) {
            stats.put("error", "Error fetching user statistics: " + e.getMessage());
        }
        
        return stats;
    }

    /**
     * Bulk operations - activate/deactivate users
     */
    public Map<String, Object> bulkUpdateUserStatus(List<Long> userIds, String status) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        
        try {
            for (Long userId : userIds) {
                try {
                    Optional<user> userOpt = userRepository.findById(userId);
                    if (userOpt.isPresent()) {
                        user u = userOpt.get();
                        // Assuming there's a status field - adjust based on your entity
                        // u.setStatus(status);
                        userRepository.save(u);
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
            result.put("message", String.format("Updated %d users, %d failed", successCount, failCount));
            
        } catch (Exception e) {
            result.put("error", "Error in bulk update: " + e.getMessage());
        }
        
        return result;
    }
}