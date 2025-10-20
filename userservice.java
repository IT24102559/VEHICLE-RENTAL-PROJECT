
package org.example.rentalsytsem.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.example.rentalsytsem.entity.user;
import org.example.rentalsytsem.repository.userrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.Data;

@Service
@Data
public class userservice {
    @Autowired
    private final userrepository userrepository;
    @Autowired
    private final passwordEncoding passwordEncoding;

    @Autowired
    public userservice(userrepository userrepository, passwordEncoding passwordEncoding) {
        this.userrepository = userrepository;
        this.passwordEncoding = passwordEncoding;
    }

    public user registerUser(user rUser) {
        System.out.println("DEBUG: Attempting to register user: " + rUser.getUsername());
        
        Optional<user> existingUser = userrepository.findByUsername(rUser.getUsername());
        if (existingUser.isPresent()) {
            System.out.println("DEBUG: User already exists: " + rUser.getUsername());
            throw new RuntimeException("user already exists");
        }
        
        // Set default role if not provided
        if (rUser.getRole() == null || rUser.getRole().trim().isEmpty()) {
            rUser.setRole("USER");
        }
        
        // Encode the password before saving
        String originalPassword = rUser.getPassword();
        String encodedPassword = passwordEncoding.encode(originalPassword);
        rUser.setPassword(encodedPassword);
        
        System.out.println("DEBUG: Saving user to database...");
        user savedUser = userrepository.save(rUser);
        System.out.println("DEBUG: User saved successfully with ID: " + savedUser.getId());
        
        return savedUser;
    }

    public List<user> getAllUsers() {
        return userrepository.findAll();
    }

    public Optional<user> getUserByID(Long id) {
        return userrepository.findById(id);
    }

    public user saveUser(user U) {
        return userrepository.save(U);
    }

    public void deleteUser(Long id) {
        userrepository.deleteById(id);
    }
    public user updateUser(user U) {
        Optional<user> existingUser = userrepository.findById(U.getId());
        if(existingUser.isPresent()){
            user updatedUser = existingUser.get();
            // Only update fields that are provided (non-null / non-empty)
            if (U.getFirstName() != null && !U.getFirstName().isBlank()) {
                updatedUser.setFirstName(U.getFirstName());
            }
            if (U.getLastName() != null && !U.getLastName().isBlank()) {
                updatedUser.setLastName(U.getLastName());
            }
            if (U.getUsername() != null && !U.getUsername().isBlank()) {
                updatedUser.setUsername(U.getUsername());
            }
            if (U.getEmail() != null && !U.getEmail().isBlank()) {
                updatedUser.setEmail(U.getEmail());
            }
            if (U.getPassword() != null && !U.getPassword().isBlank()) {
                updatedUser.setPassword(passwordEncoding.encode(U.getPassword()));
            }
            if (U.getRole() != null && !U.getRole().isBlank()) {
                updatedUser.setRole(U.getRole());
            }
            return userrepository.save(updatedUser);
        } else {
            throw new RuntimeException("user not found");
        }
    }
    public void deleteUserById(Long id){
        if(userrepository.existsById(id)){
            userrepository.deleteById(id);
        }else{
            throw new RuntimeException("user not found");
        }
    }
    public boolean validateUserCredentials(String username, String password) {
        Optional<user> userOpt = userrepository.findByUsername(username);
        return userOpt.map(user -> passwordEncoding.matches(password, user.getPassword())).orElse(false);
    }
    public Map<String,Object> authenticateUser(String username, String password) {
        Map<String,Object> userResult = new HashMap<>();
        
        // Check for hardcoded admin credentials first
        if ("vehm".equals(username) && "vehm123".equals(password)) {
            userResult.put("status", "success");
            userResult.put("role", "vehicle-admin");
            userResult.put("message", "Login successful");
            userResult.put("adminType", "vehicle");
            return userResult;
        }
        
        if ("super".equals(username) && "super123".equals(password)) {
            userResult.put("status", "success");
            userResult.put("role", "super-admin");
            userResult.put("message", "Login successful");
            userResult.put("adminType", "super");
            return userResult;
        }
        
        // Check regular users from database
        System.out.println("DEBUG: Checking database for user: " + username);
        Optional<user> userOpt = userrepository.findByUsername(username);
        
        if(userOpt.isPresent()){
            user U = userOpt.get();
            System.out.println("DEBUG: User found in database: " + U.getUsername() + ", Role: " + U.getRole());
            System.out.println("DEBUG: Stored password: " + U.getPassword());
            System.out.println("DEBUG: Input password: " + password);
            
            boolean passwordMatch = passwordEncoding.matches(password, U.getPassword());
            System.out.println("DEBUG: Password match result: " + passwordMatch);
            
            if(passwordMatch){
                userResult.put("status","success");
                userResult.put("role",U.getRole());
                userResult.put("userId", U.getId()); // Add userId to the response
                userResult.put("message","Login successful");
                System.out.println("DEBUG: Authentication successful for user: " + username);
                return userResult;
            } else {
                System.out.println("DEBUG: Password mismatch for user: " + username);
            }
        } else {
            System.out.println("DEBUG: User not found in database: " + username);
        }

        userResult.put("status","fail");
        userResult.put("message","Invalid username or password");
        System.out.println("DEBUG: Authentication failed for user: " + username);
        return userResult;
    }

}