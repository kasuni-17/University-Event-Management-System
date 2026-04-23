package com.unievent.controller;

import com.unievent.entity.Event;
import com.unievent.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("")
    public ResponseEntity<?> createEvent(@RequestBody Event event) {
        try {
            return ResponseEntity.ok(eventService.createEvent(event));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating event: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents(@RequestParam(required = false) Event.EventStatus status) {
        if (status != null) {
            return ResponseEntity.ok(eventService.getEventsByStatus(status));
        }
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event eventDetails) {
        try {
            return ResponseEntity.ok(eventService.updateEvent(id, eventDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok().build();
    }

    // ============ ADMIN ENDPOINTS ============

    /**
     * Get all pending events (newly published, awaiting approval)
     */
    @GetMapping("/admin/pending")
    public ResponseEntity<List<Event>> getPendingEvents() {
        return ResponseEntity.ok(eventService.getPendingEvents());
    }

    /**
     * Get all approved events (upcoming)
     */
    @GetMapping("/admin/approved")
    public ResponseEntity<List<Event>> getApprovedEvents() {
        return ResponseEntity.ok(eventService.getApprovedEvents());
    }

    /**
     * Get completed/past events
     */
    @GetMapping("/admin/completed")
    public ResponseEntity<List<Event>> getCompletedEvents() {
        return ResponseEntity.ok(eventService.getCompletedEvents());
    }

    /**
     * Get upcoming events (events with start time in the future)
     */
    @GetMapping("/admin/upcoming")
    public ResponseEntity<List<Event>> getUpcomingEvents() {
        return ResponseEntity.ok(eventService.getUpcomingEvents());
    }

    /**
     * Get past events (events with start time in the past)
     */
    @GetMapping("/admin/past")
    public ResponseEntity<List<Event>> getPastEvents() {
        return ResponseEntity.ok(eventService.getPastEvents());
    }

    /**
     * Get eligible events for feedback for a specific student
     */
    @GetMapping("/student/{studentId}/eligible-feedback")
    public ResponseEntity<List<Event>> getEligibleFeedbackEvents(@PathVariable Long studentId) {
        return ResponseEntity.ok(eventService.getEligibleFeedbackEvents(studentId));
    }

    /**
     * Approve an event
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<Event> approveEvent(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(eventService.approveEvent(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Reject an event
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<Event> rejectEvent(@PathVariable Long id, @RequestParam(required = false) String reason) {
        try {
            return ResponseEntity.ok(eventService.rejectEvent(id, reason));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Mark event as completed
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<Event> completeEvent(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(eventService.completeEvent(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get pending events for a specific club (for club admin to see their own pending submissions)
     */
    @GetMapping("/club/{clubId}/pending")
    public ResponseEntity<List<Event>> getPendingEventsByClub(@PathVariable Long clubId) {
        return ResponseEntity.ok(eventService.getPendingEventsByClub(clubId));
    }
}
