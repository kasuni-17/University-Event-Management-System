package com.unievent.dto;

import java.time.LocalDateTime;

/**
 * DTO for user registration status
 */
public class UserRegistrationStatusDTO {
    private Long eventId;
    private String eventTitle;
    private boolean registered;
    private LocalDateTime registrationDate;
    private String status;

    public UserRegistrationStatusDTO() {
    }

    public UserRegistrationStatusDTO(Long eventId, String eventTitle, boolean registered, LocalDateTime registrationDate, String status) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.registered = registered;
        this.registrationDate = registrationDate;
        this.status = status;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public boolean isRegistered() {
        return registered;
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
