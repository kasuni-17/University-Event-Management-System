package com.unievent.repository;

import com.unievent.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByOrganizerId(Long clubId);

    List<Event> findByStatus(Event.EventStatus status);

    List<Event> findByVenueId(Long venueId);
}
