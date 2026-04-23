package com.unievent.controller;

import com.unievent.dto.EventFilterDTO;
import com.unievent.entity.Event;
import com.unievent.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Event Filter API Controller
 * Handles event filtering for different views:
 * - Upcoming events
 * - Past events
 * - Pending events (for club admins)
 * - Club-specific event filtering
 */
@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventFilterController {

    @Autowired
    private EventService eventService;

    /**
     * GET /api/events/upcoming
     * Get all upcoming events (visible to students and club admins)
     */
    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingEvents() {
        try {
            List<Event> upcomingEvents = eventService.getUpcomingEvents();
            List<EventFilterDTO> response = convertToEventFilterDTOs(upcomingEvents);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/events/past
     * Get all past events (completed events visible to students)
     */
    @GetMapping("/past")
    public ResponseEntity<?> getPastEvents() {
        try {
            List<Event> pastEvents = eventService.getPastEvents();
            List<EventFilterDTO> response = convertToEventFilterDTOs(pastEvents);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/events/pending/{clubId}
     * Get pending events for a specific club (for club admin view)
     */
    @GetMapping("/pending/{clubId}")
    public ResponseEntity<?> getPendingEventsByClub(@PathVariable Long clubId) {
        try {
            List<Event> pendingEvents = eventService.getPendingEventsByClub(clubId);
            List<EventFilterDTO> response = convertToEventFilterDTOs(pendingEvents);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/events/club/{clubId}?filter=upcoming|pending|past
     * Get club events filtered by type
     * Used by club admin in My Clubs page
     */
    @GetMapping("/club/{clubId}")
    public ResponseEntity<?> getClubEventsByFilter(
            @PathVariable Long clubId,
            @RequestParam(value = "filter", defaultValue = "upcoming") String filterType) {
        try {
            List<Event> filteredEvents = eventService.getClubEventsByFilter(clubId, filterType);
            List<EventFilterDTO> response = convertToEventFilterDTOs(filteredEvents);
            return ResponseEntity.ok(Map.of(
                    "filter", filterType,
                    "count", filteredEvents.size(),
                    "events", response
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/events/approved
     * Get all approved events (visible to all)
     */
    @GetMapping("/approved")
    public ResponseEntity<?> getApprovedEvents() {
        try {
            List<Event> approvedEvents = eventService.getApprovedEvents();
            List<EventFilterDTO> response = convertToEventFilterDTOs(approvedEvents);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/events/{eventId}/check-approval
     * Check if an event is approved
     */
    @GetMapping("/{eventId}/check-approval")
    public ResponseEntity<?> checkEventApproval(@PathVariable Long eventId) {
        try {
            boolean isApproved = eventService.isEventApproved(eventId);
            return ResponseEntity.ok(Map.of(
                    "eventId", eventId,
                    "approved", isApproved
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============ HELPER METHODS ============

    private List<EventFilterDTO> convertToEventFilterDTOs(List<Event> events) {
        List<EventFilterDTO> dtos = new ArrayList<>();
        for (Event event : events) {
            EventFilterDTO dto = new EventFilterDTO(
                    event.getId(),
                    event.getTitle(),
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getLocation(),
                    event.getStatus().toString(),
                    event.getOrganizer() != null ? event.getOrganizer().getName() : "Unknown",
                    event.getEventType(),
                    event.getPosterUrl()
            );
            dtos.add(dto);
        }
        return dtos;
    }
}
