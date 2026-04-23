package com.unievent.controller;

import com.unievent.entity.Booking;
import com.unievent.entity.Venue;
import com.unievent.service.VenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@CrossOrigin(origins = "*")
public class VenueController {

    @Autowired
    private VenueService venueService;

    // Venues
    @PostMapping
    public ResponseEntity<?> createVenue(@RequestBody Venue venue) {
        try {
            return ResponseEntity.ok(venueService.createVenue(venue));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding venue: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenues() {
        return ResponseEntity.ok(venueService.getAllVenues());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venue> getVenueById(@PathVariable Long id) {
        return venueService.getVenueById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venue> updateVenue(@PathVariable Long id, @RequestBody Venue venueDetails) {
        try {
            return ResponseEntity.ok(venueService.updateVenue(id, venueDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.ok().build();
    }

    // ============ ADMIN ENDPOINTS ============

    /**
     * Get venues by type (Auditorium, Hall, Lab, Ground, Meeting Room)
     */
    @GetMapping("/admin/type/{type}")
    public ResponseEntity<List<Venue>> getVenuesByType(@PathVariable Venue.VenueType type) {
        return ResponseEntity.ok(venueService.getVenuesByType(type));
    }

    /**
     * Search venues by name
     */
    @GetMapping("/admin/search")
    public ResponseEntity<List<Venue>> searchVenues(@RequestParam String name) {
        return ResponseEntity.ok(venueService.searchVenuesByName(name));
    }

    /**
     * Get venues with minimum capacity
     */
    @GetMapping("/admin/capacity")
    public ResponseEntity<List<Venue>> getVenuesByCapacity(@RequestParam int minCapacity) {
        return ResponseEntity.ok(venueService.getVenuesByMinCapacity(minCapacity));
    }

    // Bookings
    @PostMapping("/bookings")
    public ResponseEntity<Booking> createBooking(@RequestBody Booking booking) {
        return ResponseEntity.ok(venueService.createBooking(booking));
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings(@RequestParam(required = false) Long venueId) {
        if (venueId != null) {
            return ResponseEntity.ok(venueService.getBookingsByVenue(venueId));
        }
        return ResponseEntity.ok(venueService.getAllBookings());
    }

    @PutMapping("/bookings/{id}/status")
    public ResponseEntity<Booking> updateBookingStatus(@PathVariable Long id,
            @RequestParam Booking.BookingStatus status) {
        return ResponseEntity.ok(venueService.updateBookingStatus(id, status));
    }

    /**
     * Check venue availability for a specific date/time
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<Boolean> checkVenueAvailability(@PathVariable Long id,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            boolean available = venueService.checkVenueAvailability(id, startDate, endDate);
            return ResponseEntity.ok(available);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
