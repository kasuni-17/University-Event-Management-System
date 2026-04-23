package com.unievent.controller;

import com.unievent.entity.Club;
import com.unievent.service.ClubService;
import com.unievent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
@CrossOrigin(origins = "*")
public class ClubController {

    @Autowired
    private ClubService clubService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Club> createClub(@RequestBody Club club) {
        return ResponseEntity.ok(clubService.createClub(club));
    }

    @GetMapping
    public ResponseEntity<List<Club>> getAllClubs(@RequestParam(required = false) String category) {
        if (category != null) {
            return ResponseEntity.ok(clubService.getClubsByCategory(category));
        }
        return ResponseEntity.ok(clubService.getAllClubs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Club> getClubById(@PathVariable Long id) {
        return clubService.getClubById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/member-count")
    public ResponseEntity<Long> getClubMemberCount(@PathVariable Long id) {
        return ResponseEntity.ok(clubService.getClubMemberCount(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Club> updateClub(@PathVariable Long id, @RequestBody Club clubDetails) {
        try {
            return ResponseEntity.ok(clubService.updateClub(id, clubDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClub(@PathVariable Long id) {
        clubService.deleteClub(id);
        return ResponseEntity.ok().build();
    }

    // ============ ADMIN ENDPOINTS ============

    /**
     * Get all pending clubs (newly registered, awaiting approval)
     */
    @GetMapping("/admin/pending")
    public ResponseEntity<List<Club>> getPendingClubs() {
        return ResponseEntity.ok(clubService.getPendingClubs());
    }

    /**
     * Get all approved clubs
     */
    @GetMapping("/admin/approved")
    public ResponseEntity<List<Club>> getApprovedClubs() {
        return ResponseEntity.ok(clubService.getApprovedClubs());
    }

    /**
     * Get clubs by approval status
     */
    @GetMapping("/admin/status/{status}")
    public ResponseEntity<List<Club>> getClubsByApprovalStatus(@PathVariable Club.ApprovalStatus status) {
        return ResponseEntity.ok(clubService.getClubsByApprovalStatus(status));
    }

    /**
     * Approve a club registration
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<Club> approveClub(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(clubService.approveClub(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Reject a club registration
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<Club> rejectClub(@PathVariable Long id, @RequestParam(required = false) String reason) {
        try {
            return ResponseEntity.ok(clubService.rejectClub(id, reason));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // ============ ORGANIZER ENDPOINTS ============

    @GetMapping("/organizer/{userId}")
    public ResponseEntity<List<Club>> getClubsByOrganizer(@PathVariable Long userId) {
        System.out.println("DEBUG: Fetching clubs for organizer userId=" + userId);
        return userService.getUserById(userId)
                .map(user -> {
                    System.out.println("DEBUG: Found user by ID, email=" + user.getEmail());
                    return ResponseEntity.ok(clubService.getClubsByPresidentEmail(user.getEmail()));
                })
                .orElseGet(() -> {
                    System.out.println("DEBUG: User not found in DB with ID=" + userId);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/organizer-email/{email}")
    public ResponseEntity<List<Club>> getClubsByOrganizerEmail(@PathVariable String email) {
        System.out.println("DEBUG: Fetching clubs for organizer email=" + email);
        return ResponseEntity.ok(clubService.getClubsByPresidentEmail(email));
    }
}
