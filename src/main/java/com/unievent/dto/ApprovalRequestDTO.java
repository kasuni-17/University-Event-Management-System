package com.unievent.dto;

/**
 * DTO for approval requests
 */
public class ApprovalRequestDTO {
    private Long id;
    private boolean approved;
    private String rejectionReason;

    public ApprovalRequestDTO() {
    }

    public ApprovalRequestDTO(Long id, boolean approved, String rejectionReason) {
        this.id = id;
        this.approved = approved;
        this.rejectionReason = rejectionReason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
