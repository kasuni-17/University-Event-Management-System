package com.unievent.service;

import com.unievent.entity.Booking;
import com.unievent.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    public Booking createBooking(Booking booking) {
        // Simple overlap check
        List<Booking> existing = bookingRepository.findByVenueIdAndStartTimeBetween(
                booking.getVenue().getId(), booking.getStartTime(), booking.getEndTime());
        
        if (!existing.isEmpty()) {
            throw new RuntimeException("Venue is already confirmed/pending for this time");
        }
        
        booking.setStatus(Booking.BookingStatus.PENDING);
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsForVenue(Long venueId) {
        return bookingRepository.findByVenueId(venueId);
    }

    public List<Booking> getBookingsForVenueInDateRange(Long venueId, LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByVenueIdAndStartTimeBetween(venueId, start, end);
    }
}
