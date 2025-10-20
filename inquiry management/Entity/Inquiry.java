package org.example.rentalsytsem.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@Entity
@Table(name = "inquiries")
public class inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InquiryStatus status = InquiryStatus.PENDING;

    // @Enumerated(EnumType.STRING)
    // private InquiryPriority priority = InquiryPriority.MEDIUM;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private user user;

    @Column(columnDefinition = "TEXT")
    private String response;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "responded_by")
    private String respondedBy; 
    // Admin username who responded to inquiries

    public enum InquiryStatus {
        PENDING,
        IN_PROGRESS,
        RESOLVED,
        CLOSED, RESPONDED
    }

    public enum InquiryPriority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InquiryStatus getStatus() {
        return status;
    }

    public void setStatus(InquiryStatus status) {
        this.status = status;
    }

    public user getUser() {
        return user;
    }

    public void setUser(user user) {
        this.user = user;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

    public String getRespondedBy() {
        return respondedBy;
    }

    public void setRespondedBy(String respondedBy) {
        this.respondedBy = respondedBy;
    }

    
    public InquiryPriority getPriority() {
        return InquiryPriority.MEDIUM; 
    }


    public void setPriority(InquiryPriority priority) {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        if (response != null && status == InquiryStatus.RESOLVED && respondedAt == null) {
            respondedAt = LocalDateTime.now();
        }
    }
}
