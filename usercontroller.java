 package org.example.rentalsytsem.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.rentalsytsem.entity.user;
import org.example.rentalsytsem.service.userservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/users")
 @CrossOrigin (origins = "*") // Allow frontend calls from any domain
public class usercontroller {  // Removed @Data - not needed for controllers

    @Autowired
    private userservice userService;

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody user rUser) {
        try {
            System.out.println("DEBUG: Registration request received for: " + rUser.getUsername());
            user registeredUser = userService.registerUser(rUser);
            System.out.println("DEBUG: Registration successful for: " + registeredUser.getUsername());
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.out.println("DEBUG: Registration failed: " + e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.out.println("DEBUG: Unexpected error during registration: " + e.getMessage());
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Registration failed: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody Map<String, String> rUser, HttpSession session) {  // Added HttpSession parameter
        String username = rUser.get("username");
        String password = rUser.get("password");

        System.out.println("DEBUG: Login request received for username: " + username);

        Map<String, Object> result = userService.authenticateUser(username, password);
        // result contains keys: status, role, message
        if ("success".equals(result.get("status"))) {
            System.out.println("DEBUG: Login successful for: " + username);
            
            // Store user info in session
            session.setAttribute("userId", result.get("userId"));
            session.setAttribute("username", username);
            session.setAttribute("role", result.get("role"));
            
            return ResponseEntity.ok(result);
        } else {
            System.out.println("DEBUG: Login failed for: " + username + ", Reason: " + result.get("message"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            String username = (String) session.getAttribute("username");
            String role = (String) session.getAttribute("role");
            
            if (userId == null || username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Please log in to access this resource"));
            }

            // Get full user details from database
            Optional<user> userOpt = userService.getUserByID(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User not found"));
            }

            user currentUser = userOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("user", Map.of(
                "id", currentUser.getId(),
                "username", currentUser.getUsername(),
                "firstName", currentUser.getFirstName(),
                "lastName", currentUser.getLastName(),
                "email", currentUser.getEmail(),
                "role", currentUser.getRole()
            ));
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "Failed to get user info: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpSession session) {
        try {
            session.invalidate(); // Clear session
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Logged out successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "Logout failed: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<user>> getAllUsers() {
        List<user> allUsers = userService.getAllUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        Optional<user> userOpt = userService.getAllUsers().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
        return userOpt.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));  // Simplified; removed unnecessary type cast
    }

    @PutMapping("/{id}")
    public ResponseEntity<user> updateUser(@PathVariable Long id, @RequestBody user reqUser) {
        Optional<user> updateUserOpt = userService.getUserByID(id);
        if (updateUserOpt.isPresent()) {
            user updatedUser = updateUserOpt.get();
            updatedUser.setFirstName(reqUser.getFirstName());
            updatedUser.setLastName(reqUser.getLastName());
            updatedUser.setUsername(reqUser.getUsername());
            updatedUser.setEmail(reqUser.getEmail());
            updatedUser.setPassword(reqUser.getPassword());
            updatedUser.setRole(reqUser.getRole());
            userService.saveUser(updatedUser);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<user> inUser = userService.getUserByID(id);
        if (inUser.isPresent()) {
            userService.deleteUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Update current user's profile
    @PutMapping("/profile")
    public ResponseEntity<?> updateCurrentUserProfile(@RequestBody Map<String, String> updateData, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            String username = (String) session.getAttribute("username");
            
            if (userId == null || username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Please log in to access this resource"));
            }

            Optional<user> userOpt = userService.getUserByID(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User not found"));
            }

            user currentUser = userOpt.get();
            
            // Update only allowed fields
            if (updateData.containsKey("firstName")) {
                currentUser.setFirstName(updateData.get("firstName"));
            }
            if (updateData.containsKey("lastName")) {
                currentUser.setLastName(updateData.get("lastName"));
            }
            if (updateData.containsKey("email")) {
                currentUser.setEmail(updateData.get("email"));
            }

            userService.saveUser(currentUser);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Profile updated successfully");
            response.put("user", Map.of(
                "id", currentUser.getId(),
                "username", currentUser.getUsername(),
                "firstName", currentUser.getFirstName(),
                "lastName", currentUser.getLastName(),
                "email", currentUser.getEmail(),
                "role", currentUser.getRole()
            ));
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "Failed to update profile: " + e.getMessage()));
        }
    }

    // Change password
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordData, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            String username = (String) session.getAttribute("username");
            
            if (userId == null || username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Please log in to access this resource"));
            }

            Optional<user> userOpt = userService.getUserByID(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User not found"));
            }

            user currentUser = userOpt.get();
            String currentPassword = passwordData.get("currentPassword");
            String newPassword = passwordData.get("newPassword");

            // Verify current password (you may need to implement password verification in service)
            if (!currentUser.getPassword().equals(currentPassword)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Current password is incorrect"));
            }

            // Update password
            currentUser.setPassword(newPassword);
            userService.saveUser(currentUser);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Password changed successfully"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "Failed to change password: " + e.getMessage()));
        }
    }

    // Deactivate account
    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateAccount(@RequestBody Map<String, String> deactivationData, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("userId");
            String username = (String) session.getAttribute("username");
            
            if (userId == null || username == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Please log in to access this resource"));
            }

            Optional<user> userOpt = userService.getUserByID(userId);
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User not found"));
            }

            user currentUser = userOpt.get();
            String reason = deactivationData.get("reason");

            // For now, we'll just mark the account as inactive by updating role or adding a status field
            // You might want to add an 'active' field to the user entity for better implementation
            currentUser.setRole("DEACTIVATED");
            userService.saveUser(currentUser);
            
            // Invalidate session
            session.invalidate();
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Account deactivated successfully"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("status", "error", "message", "Failed to deactivate account: " + e.getMessage()));
        }
    }
}
