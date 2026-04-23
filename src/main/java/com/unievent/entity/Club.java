package com.unievent.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clubs")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String category; // e.g., Sports, Cultural, Academic

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "president_email")
    private String presidentEmail;

    private String email; // Official club email

    @Column(name = "personal_contact_gmail")
    private String personalContactGmail;

    private String logoUrl;

    private String coverUrl;

    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    private java.time.LocalDateTime approvalDate;
    private String rejectionReason;
    private java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }

    // Could add relations to Events here if needed, but Event owning the
    // relationship is easier.

    public Club() {
    }

    public Club(Long id, String name, String category, String description, String presidentEmail, String email, String logoUrl, String coverUrl, boolean active) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.presidentEmail = presidentEmail;
        this.email = email;
        this.logoUrl = logoUrl;
        this.coverUrl = coverUrl;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPresidentEmail() {
        return presidentEmail;
    }

    public void setPresidentEmail(String presidentEmail) {
        this.presidentEmail = presidentEmail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersonalContactGmail() {
        return personalContactGmail;
    }

    public void setPersonalContactGmail(String personalContactGmail) {
        this.personalContactGmail = personalContactGmail;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public java.time.LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(java.time.LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }
}
