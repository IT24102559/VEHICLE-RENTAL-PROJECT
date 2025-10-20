package org.example.rentalsytsem.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.rentalsytsem.entity.feedback;
import org.example.rentalsytsem.repository.feedbackrepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class feedbackservice {

	private final feedbackrepository repo;

	@Autowired
	public feedbackservice(feedbackrepository repo) { this.repo = repo; }

	public feedback createFeedback(feedback f) { 
		f.setStatus("APPROVED"); // Auto-approve all feedbacks
		return repo.save(f); 
	}
	
	public List<feedback> getAll() { return repo.findAll(); }
	public List<feedback> getApprovedFeedbacks() { return repo.findByStatusOrderByCreatedAtDesc("APPROVED"); }
	public List<feedback> getUserFeedbacks(String userName) { return repo.findByUserNameOrderByCreatedAtDesc(userName); }
	public Optional<feedback> getById(Long id) { return repo.findById(id); }
	
	public feedback updateFeedback(Long id, feedback updatedFeedback) {
		return repo.findById(id).map(existingFeedback -> {
			existingFeedback.setSubject(updatedFeedback.getSubject());
			existingFeedback.setMessage(updatedFeedback.getMessage());
			return repo.save(existingFeedback);
		}).orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
	}
	
	public feedback replyToFeedback(Long id, String reply, String repliedBy) {
		return repo.findById(id).map(existingFeedback -> {
			existingFeedback.setAdminReply(reply);
			existingFeedback.setRepliedBy(repliedBy);
			existingFeedback.setRepliedAt(LocalDateTime.now());
			return repo.save(existingFeedback);
		}).orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
	}
	
	public void delete(Long id) { repo.deleteById(id); }
	
	/**
	 * Check if a user can modify (edit/delete) a feedback
	 * Users can modify their own feedback, and admins can modify any feedback
	 */
	public boolean canUserModifyFeedback(Long feedbackId, String username, String userRole) {
		if (username == null || username.trim().isEmpty()) {
			return false;
		}
		
		// Admins can modify any feedback
		if ("Admin".equals(userRole) || "super-admin".equals(userRole) || "vehicle-admin".equals(userRole)) {
			return true;
		}
		
		// Regular users can only modify their own feedback
		Optional<feedback> feedbackOpt = repo.findById(feedbackId);
		if (feedbackOpt.isPresent()) {
			feedback fb = feedbackOpt.get();
			return username.equals(fb.getUserName());
		}
		
		return false;
	}
}

