package com.unievent.repository;

import com.unievent.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Long> {
    // Query methods for filtering
    List<Venue> findByVenueType(Venue.VenueType venueType);

    List<Venue> findByNameContainingIgnoreCase(String name);

    List<Venue> findByCapacityGreaterThanEqual(int capacity);
}
