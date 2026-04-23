package com.unievent.controller;

import com.unievent.entity.Registration;
import com.unievent.entity.ClubMembership;
import com.unievent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User Status API Controller
 * Handles user interaction status checks:
 * - Event registration status
 * - Club membership status
 */
@RestController
@RequestMapping("/api/user-status")
@CrossOrigin(origins = "*")
public class UserStatusController {

    @Autowired
    private UserService userService;

    /**
     * GET /api/user-status/registered-events/{userId}
     * Get all events registered by a user
     */
    @GetMapping("/registered-events/{userId}")
    public ResponseEntity<?> getUserRegisteredEvents(@PathVariable Long userId) {
        try {
            List<Registration> registrations = userService.getUserRegisteredEvents(userId);
            return ResponseEntity.ok(registrations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/user-status/is-registered/{userId}/{eventId}
     * Check if user is registered for a specific event
     */
    @GetMapping("/is-registered/{userId}/{eventId}")
    public ResponseEntity<?> isUserRegistered(@PathVariable Long userId, @PathVariable Long eventId) {
        try {
            boolean isRegistered = userService.isUserRegisteredForEvent(userId, eventId);
            Map<String, Object> response = new HashMap<>();
            response.put("registered", isRegistered);
            response.put("userId", userId);
            response.put("eventId", eventId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/user-status/joined-clubs/{userId}
     * Get all clubs joined by a user
     */
    @GetMapping("/joined-clubs/{userId}")
    public ResponseEntity<?> getUserJoinedClubs(@PathVariable Long userId) {
        try {
            List<ClubMembership> memberships = userService.getUserJoinedClubs(userId);
            return ResponseEntity.ok(memberships);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/user-status/is-joined/{userId}/{clubId}
     * Check if user has joined a specific club
     */
    @GetMapping("/is-joined/{userId}/{clubId}")
    public ResponseEntity<?> isUserJoinedClub(@PathVariable Long userId, @PathVariable Long clubId) {
        try {
            boolean isJoined = userService.isUserJoinedClub(userId, clubId);
            Map<String, Object> response = new HashMap<>();
            response.put("joined", isJoined);
            response.put("userId", userId);
            response.put("clubId", clubId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
