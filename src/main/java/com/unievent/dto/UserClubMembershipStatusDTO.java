package com.unievent.dto;

import java.time.LocalDateTime;

/**
 * DTO for club membership status
 */
public class UserClubMembershipStatusDTO {
    private Long clubId;
    private String clubName;
    private boolean joined;
    private LocalDateTime joinDate;
    private String status;

    public UserClubMembershipStatusDTO() {
    }

    public UserClubMembershipStatusDTO(Long clubId, String clubName, boolean joined, LocalDateTime joinDate, String status) {
        this.clubId = clubId;
        this.clubName = clubName;
        this.joined = joined;
        this.joinDate = joinDate;
        this.status = status;
    }

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public LocalDateTime getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDateTime joinDate) {
        this.joinDate = joinDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
