package org.example.rentalsytsem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "feedback")
public class feedback {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String subject;

	@Column(length = 2000)
	private String message;

	private LocalDateTime createdAt;

	@Column(name = "status", length = 20)
	private String status = "APPROVED"; // APPROVED (default - all feedbacks are auto-approved)

	@Column(name = "approved_by", length = 100)
	private String approvedBy;

	@Column(name = "approved_at")
	private LocalDateTime approvedAt;

	@Column(name = "user_name", length = 100)
	private String userName;

	@Column(name = "admin_reply", length = 2000)
	private String adminReply;

	@Column(name = "replied_by", length = 100)
	private String repliedBy;

	@Column(name = "replied_at")
	private LocalDateTime repliedAt;

	public feedback() {
		this.createdAt = LocalDateTime.now();
		this.status = "APPROVED";
	}

	public Long getId() {
		return id;
	}

//	public void setId(Long id) {
//		this.id = id;
//	}
//
//	public String getSubject() {
//		return subject;
//	}
//
//	public void setSubject(String subject) {
//		this.subject = subject;
//	}
//
//	public String getMessage() {
//		return message;
//	}
//
//	public void setMessage(String message) {
//		this.message = message;
//	}
//
//	public LocalDateTime getCreatedAt() {
//		return createdAt;
//	}
//
//	public void setCreatedAt(LocalDateTime createdAt) {
//		this.createdAt = createdAt;
//	}
//
//	public String getStatus() {
//		return status;
//	}
//
//	public void setStatus(String status) {
//		this.status = status;
//	}
//
//	public String getApprovedBy() {
//		return approvedBy;
//	}
//
//	public void setApprovedBy(String approvedBy) {
//		this.approvedBy = approvedBy;
//	}
//
//	public LocalDateTime getApprovedAt() {
//		return approvedAt;
//	}
//
//	public void setApprovedAt(LocalDateTime approvedAt) {
//		this.approvedAt = approvedAt;
//	}
//
//	public String getUserName() {
//		return userName;
//	}
//
//	public void setUserName(String userName) {
//		this.userName = userName;
//	}
}
