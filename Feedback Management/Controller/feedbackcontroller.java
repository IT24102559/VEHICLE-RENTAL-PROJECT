package org.example.rentalsytsem.controller;

import java.util.List;
import java.util.Map;

import org.example.rentalsytsem.entity.feedback;
import org.example.rentalsytsem.service.feedbackservice;
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
@RequestMapping("/api/feedbacks")
@CrossOrigin(origins = "*")
public class feedbackcontroller {

	private final feedbackservice service;
	@Autowired
	public feedbackcontroller(feedbackservice service) { this.service = service; }

	@PostMapping
	public ResponseEntity<?> create(@RequestBody feedback f, HttpSession session) { 
		try {
			String username = (String) session.getAttribute("username");
			if (username == null || username.trim().isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("status", "error", "message", "User must be logged in"));
			}
			
			f.setUserName(username);
			feedback createdFeedback = service.createFeedback(f);
			return ResponseEntity.ok(createdFeedback);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "message", "Failed to create feedback: " + e.getMessage()));
		}
	}

	@GetMapping
	public ResponseEntity<List<feedback>> getAll() { 
		// For dashboard - only show approved feedbacks
		return ResponseEntity.ok(service.getApprovedFeedbacks()); 
	}
	
	@GetMapping("/my-feedbacks")
	public ResponseEntity<?> getMyFeedbacks(HttpSession session) { 
		try {
			String username = (String) session.getAttribute("username");
			if (username == null || username.trim().isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("status", "error", "message", "User must be logged in"));
			}
			
			List<feedback> userFeedbacks = service.getUserFeedbacks(username);
			return ResponseEntity.ok(userFeedbacks);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "message", "Failed to fetch feedbacks: " + e.getMessage()));
		}
	}
	
	@GetMapping("/all")
	public ResponseEntity<List<feedback>> getAllFeedbacks() { 
		// For super admin - show all feedbacks
		return ResponseEntity.ok(service.getAll()); 
	}

	@GetMapping("/{id}")
	public ResponseEntity<feedback> getById(@PathVariable Long id) { 
		return service.getById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); 
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestBody feedback f, HttpSession session) {
		try {
			String username = (String) session.getAttribute("username");
			String userRole = (String) session.getAttribute("role");
			
			if (username == null || username.trim().isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("status", "error", "message", "User must be logged in"));
			}
			
			// Check if user can update this feedback
			if (!service.canUserModifyFeedback(id, username, userRole)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(Map.of("status", "error", "message", "You can only edit your own feedback"));
			}
			
			feedback updated = service.updateFeedback(id, f);
			return ResponseEntity.ok(updated);
		} catch (RuntimeException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("status", "error", "message", "Feedback not found"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "message", "Failed to update feedback: " + e.getMessage()));
		}
	}
	
	@PostMapping("/{id}/reply")
	public ResponseEntity<?> replyToFeedback(@PathVariable Long id, @RequestBody Map<String, String> request, HttpSession session) {
		try {
			String userRole = (String) session.getAttribute("role");
			String adminUsername = (String) session.getAttribute("username");
			
			if (!"Admin".equals(userRole) && !"super-admin".equals(userRole) && !"vehicle-admin".equals(userRole)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(Map.of("status", "error", "message", "Admin access required"));
			}
			
			String reply = request.get("reply");
			if (reply == null || reply.trim().isEmpty()) {
				return ResponseEntity.badRequest()
						.body(Map.of("status", "error", "message", "Reply is required"));
			}
			
			feedback replied = service.replyToFeedback(id, reply.trim(), adminUsername);
			return ResponseEntity.ok(replied);
		} catch (RuntimeException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("status", "error", "message", "Feedback not found"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "message", "Failed to reply to feedback: " + e.getMessage()));
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) { 
		try {
			String username = (String) session.getAttribute("username");
			String userRole = (String) session.getAttribute("role");
			
			if (username == null || username.trim().isEmpty()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("status", "error", "message", "User must be logged in"));
			}
			
			// Check if user can delete this feedback
			if (!service.canUserModifyFeedback(id, username, userRole)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(Map.of("status", "error", "message", "You can only delete your own feedback"));
			}
			
			service.delete(id); 
			return ResponseEntity.ok(Map.of("status", "success", "message", "Feedback deleted successfully"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("status", "error", "message", "Failed to delete feedback: " + e.getMessage()));
		}
	}
}

