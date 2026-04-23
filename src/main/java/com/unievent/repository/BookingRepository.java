package com.unievent.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.unievent.entity.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByVenueId(Long venueId);

    List<Booking> findByVenueIdAndStartTimeBetween(Long venueId, java.time.LocalDateTime start, java.time.LocalDateTime end);

    @Modifying
    @Transactional
    @Query("DELETE FROM Booking b WHERE b.eventId = ?1")
    void deleteByEventId(Long eventId);
}
