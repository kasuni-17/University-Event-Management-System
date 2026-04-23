package com.unievent.controller;

import com.unievent.dto.ApprovalRequestDTO;
import com.unievent.entity.User;
import com.unievent.entity.Club;
import com.unievent.entity.Event;
import com.unievent.entity.Role;
import com.unievent.service.UserService;
import com.unievent.service.ClubService;
import com.unievent.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin API Controller
 * Handles all admin approval workflows for:
 * - Club Admin Account Approvals
 * - Club Registration Approvals
 * - Event Approvals
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ClubService clubService;

    @Autowired
    private EventService eventService;

    // ============ CLUB ADMIN APPROVAL ENDPOINTS ============

    /**
     * GET /api/admin/pending-club-admins
     * Get all pending club admin accounts awaiting approval
     */
    @GetMapping("/pending-club-admins")
    public ResponseEntity<?> getPendingClubAdmins() {
        try {
            List<User> pendingAdmins = userService.getPendingClubAdmins();
            return ResponseEntity.ok(pendingAdmins);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/admin/approve-club-admin/{userId}
     * Approve a club admin account
     */
    @PostMapping("/approve-club-admin/{userId}")
    public ResponseEntity<?> approveClubAdmin(@PathVariable Long userId) {
        try {
            User approvedUser = userService.approveClubAdmin(userId);
            return ResponseEntity.ok(Map.of(
                    "message", "Club admin account approved successfully",
                    "user", approvedUser
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/admin/reject-club-admin/{userId}
     * Reject a club admin account
     * Body: { "rejectionReason": "reason text" }
     */
    @PostMapping("/reject-club-admin/{userId}")
    public ResponseEntity<?> rejectClubAdmin(@PathVariable Long userId, @RequestBody Map<String, String> payload) {
        try {
            String rejectionReason = payload.getOrDefault("rejectionReason", "Your application did not meet our requirements");
            User rejectedUser = userService.rejectClubAdmin(userId, rejectionReason);
            return ResponseEntity.ok(Map.of(
                    "message", "Club admin account rejected successfully",
                    "user", rejectedUser
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============ CLUB REGISTRATION APPROVAL ENDPOINTS ============

    /**
     * GET /api/admin/pending-clubs
     * Get all pending club registrations awaiting approval
     */
    @GetMapping("/pending-clubs")
    public ResponseEntity<?> getPendingClubs() {
        try {
            List<Club> pendingClubs = clubService.getPendingClubs();
            return ResponseEntity.ok(pendingClubs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/admin/approve-club/{clubId}
     * Approve a club registration
     */
    @PostMapping("/approve-club/{clubId}")
    public ResponseEntity<?> approveClub(@PathVariable Long clubId) {
        try {
            Club approvedClub = clubService.approveClub(clubId);
            return ResponseEntity.ok(Map.of(
                    "message", "Club approved successfully",
                    "club", approvedClub
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/admin/reject-club/{clubId}
     * Reject a club registration
     * Body: { "rejectionReason": "reason text" }
     */
    @PostMapping("/reject-club/{clubId}")
    public ResponseEntity<?> rejectClub(@PathVariable Long clubId, @RequestBody Map<String, String> payload) {
        try {
            String rejectionReason = payload.getOrDefault("rejectionReason", "Club registration did not meet our standards");
            Club rejectedClub = clubService.rejectClub(clubId, rejectionReason);
            return ResponseEntity.ok(Map.of(
                    "message", "Club rejected successfully",
                    "club", rejectedClub
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============ EVENT APPROVAL ENDPOINTS ============

    /**
     * GET /api/admin/pending-events
     * Get all pending event approvals
     */
    @GetMapping("/pending-events")
    public ResponseEntity<?> getPendingEvents() {
        try {
            List<Event> pendingEvents = eventService.getPendingEvents();
            return ResponseEntity.ok(pendingEvents);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/admin/approve-event/{eventId}
     * Approve an event
     */
    @PostMapping("/approve-event/{eventId}")
    public ResponseEntity<?> approveEvent(@PathVariable Long eventId) {
        try {
            Event approvedEvent = eventService.approveEvent(eventId);
            return ResponseEntity.ok(Map.of(
                    "message", "Event approved successfully",
                    "event", approvedEvent
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/admin/reject-event/{eventId}
     * Reject an event
     * Body: { "rejectionReason": "reason text" }
     */
    @PostMapping("/reject-event/{eventId}")
    public ResponseEntity<?> rejectEvent(@PathVariable Long eventId, @RequestBody Map<String, String> payload) {
        try {
            String rejectionReason = payload.getOrDefault("rejectionReason", "Event does not meet our requirements");
            Event rejectedEvent = eventService.rejectEvent(eventId, rejectionReason);
            return ResponseEntity.ok(Map.of(
                    "message", "Event rejected successfully",
                    "event", rejectedEvent
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ============ DASHBOARD STATISTICS ENDPOINTS ============

    /**
     * GET /api/admin/approval-stats
     * Get approval workflow statistics
     */
    @GetMapping("/approval-stats")
    public ResponseEntity<?> getApprovalStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("pending_club_admins", userService.getPendingClubAdmins().size());
            stats.put("pending_clubs", clubService.getPendingClubs().size());
            stats.put("pending_events", eventService.getPendingEvents().size());
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
