package com.unievent.service;

import com.unievent.entity.Event;
import com.unievent.entity.Club;
import com.unievent.repository.EventRepository;
import com.unievent.repository.FeedbackRepository;
import com.unievent.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private com.unievent.repository.BookingRepository bookingRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VenueService venueService;

    public Event createEvent(Event event) {
        if (event.getStartTime() == null || event.getEndTime() == null) {
            throw new RuntimeException("Start time and End time are required");
        }

        // Check venue availability
        if (event.getVenue() != null && event.getVenue().getId() != null) {
            boolean available = venueService.checkVenueAvailability(
                event.getVenue().getId(), 
                event.getStartTime().toString(), 
                event.getEndTime().toString()
            );
            if (!available) {
                throw new RuntimeException("The selected venue is already booked for this time period.");
            }
        }

        event.setStatus(Event.EventStatus.PENDING);
        return eventRepository.save(event);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getEventsByStatus(Event.EventStatus status) {
        return eventRepository.findByStatus(status);
    }

    public List<Event> getEventsByClub(Long clubId) {
        return eventRepository.findByOrganizerId(clubId).stream()
                .filter(e -> e.getStatus() == Event.EventStatus.APPROVED)
                .collect(Collectors.toList());
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Event updateEvent(Long id, Event eventDetails) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));

        if (eventDetails.getStartTime() == null || eventDetails.getEndTime() == null) {
            throw new RuntimeException("Start time and End time are required");
        }

        event.setTitle(eventDetails.getTitle());
        event.setDescription(eventDetails.getDescription());
        event.setStartTime(eventDetails.getStartTime());
        event.setEndTime(eventDetails.getEndTime());
        event.setVenue(eventDetails.getVenue());
        event.setLocation(eventDetails.getLocation());
        event.setEventType(eventDetails.getEventType());
        event.setMaxParticipants(eventDetails.getMaxParticipants());
        event.setRegistrationDeadline(eventDetails.getRegistrationDeadline());
        if (eventDetails.getOrganizer() != null) {
            event.setOrganizer(eventDetails.getOrganizer());
        }
        event.setPosterUrl(eventDetails.getPosterUrl());

        return eventRepository.save(event);
    }

    @org.springframework.transaction.annotation.Transactional
    public void deleteEvent(Long id) {
        // First check if exists
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found with id: " + id);
        }
        
        // Delete all associated entities
        feedbackRepository.deleteByEventId(id);
        registrationRepository.deleteByEventId(id);
        bookingRepository.deleteByEventId(id);
        
        // Finally delete the event
        eventRepository.deleteById(id);
    }

    // ============ ADMIN APPROVAL METHODS ============

    /**
     * Get all pending events (newly published, awaiting approval)
     */
    public List<Event> getPendingEvents() {
        return eventRepository.findByStatus(Event.EventStatus.PENDING);
    }

    /**
     * Get all approved events (visible to students)
     */
    public List<Event> getApprovedEvents() {
        return eventRepository.findByStatus(Event.EventStatus.APPROVED);
    }

    /**
     * Get completed/past events
     */
    public List<Event> getCompletedEvents() {
        return eventRepository.findByStatus(Event.EventStatus.COMPLETED);
    }

    /**
     * Approve an event with email notification
     */
    public Event approveEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        event.setStatus(Event.EventStatus.APPROVED);
        event.setApprovalDate(LocalDateTime.now());
        Event savedEvent = eventRepository.save(event);

        // Send email notification to event creator (club admin)
        if (event.getOrganizer() != null && event.getOrganizer().getCreatedByUser() != null) {
            emailService.sendEventApprovalEmail(event.getOrganizer().getCreatedByUser(), event, event.getOrganizer(), true, null);
        }

        return savedEvent;
    }

    /**
     * Reject an event with email notification
     */
    public Event rejectEvent(Long eventId, String rejectionReason) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        event.setStatus(Event.EventStatus.CANCELLED);
        event.setRejectionReason(rejectionReason);
        event.setApprovalDate(LocalDateTime.now());
        Event savedEvent = eventRepository.save(event);

        // Send email notification to event creator (club admin)
        if (event.getOrganizer() != null && event.getOrganizer().getCreatedByUser() != null) {
            emailService.sendEventApprovalEmail(event.getOrganizer().getCreatedByUser(), event, event.getOrganizer(), false, rejectionReason);
        }

        return savedEvent;
    }

    /**
     * Mark event as completed (past event)
     */
    public Event completeEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));
        event.setStatus(Event.EventStatus.COMPLETED);
        return eventRepository.save(event);
    }

    // ============ STUDENT/CLUB ADMIN EVENT FILTERING ============

    /**
     * Get upcoming events (only APPROVED - visible to students and club admins)
     */
    public List<Event> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findAll().stream()
                .filter(event -> event.getStartTime().isAfter(now) 
                        && event.getStatus() == Event.EventStatus.APPROVED)
                .sorted((e1, e2) -> e1.getStartTime().compareTo(e2.getStartTime()))
                .collect(Collectors.toList());
    }

    /**
     * Get past events (only APPROVED or COMPLETED - visible to students)
     */
    public List<Event> getPastEvents() {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findAll().stream()
                .filter(event -> event.getStartTime().isBefore(now) 
                        && (event.getStatus() == Event.EventStatus.APPROVED || event.getStatus() == Event.EventStatus.COMPLETED))
                .sorted((e1, e2) -> e2.getStartTime().compareTo(e1.getStartTime()))
                .collect(Collectors.toList());
    }

    /**
     * Get eligible events for feedback for a specific student
     * Filter: Past events user registered for OR past public events
     */
    public List<Event> getEligibleFeedbackEvents(Long studentId) {
        LocalDateTime now = LocalDateTime.now();
        List<Event> allPast = eventRepository.findAll().stream()
                .filter(event -> event.getStartTime().isBefore(now))
                .collect(Collectors.toList());

        return allPast.stream().filter(event -> {
            if (!event.isRegistrationRequired()) {
                return true; // Public events are always eligible if past
            }
            // If registration required, check if user registered
            return registrationRepository.findByStudentIdAndEventId(studentId, event.getId()).isPresent();
        }).collect(Collectors.toList());
    }

    /**
     * Get pending events for a specific club (for club admin to see their own pending events)
     */
    public List<Event> getPendingEventsByClub(Long clubId) {
        LocalDateTime now = LocalDateTime.now();
        return eventRepository.findAll().stream()
                .filter(event -> event.getStatus() == Event.EventStatus.PENDING 
                        && event.getOrganizer().getId().equals(clubId)
                        && event.getStartTime().isAfter(now))
                .collect(Collectors.toList());
    }

    /**
     * Get club events by type (upcoming, pending, past)
     * Used by club admin in My Clubs page
     */
    public List<Event> getClubEventsByFilter(Long clubId, String filterType) {
        LocalDateTime now = LocalDateTime.now();
        List<Event> allClubEvents = eventRepository.findByOrganizerId(clubId);

        return allClubEvents.stream()
                .filter(event -> {
                    if ("upcoming".equalsIgnoreCase(filterType)) {
                        return event.getStartTime().isAfter(now) && event.getStatus() == Event.EventStatus.APPROVED;
                    } else if ("pending".equalsIgnoreCase(filterType)) {
                        return event.getStatus() == Event.EventStatus.PENDING;
                    } else if ("past".equalsIgnoreCase(filterType)) {
                        return event.getStartTime().isBefore(now) && (event.getStatus() == Event.EventStatus.APPROVED || event.getStatus() == Event.EventStatus.COMPLETED);
                    }
                    return true;
                })
                .sorted((e1, e2) -> {
                    if ("past".equalsIgnoreCase(filterType)) {
                        return e2.getStartTime().compareTo(e1.getStartTime());
                    }
                    return e1.getStartTime().compareTo(e2.getStartTime());
                })
                .collect(Collectors.toList());
    }

    /**
     * Check if event is approved
     */
    public boolean isEventApproved(Long eventId) {
        return eventRepository.findById(eventId)
                .map(e -> e.getStatus() == Event.EventStatus.APPROVED)
                .orElse(false);
    }
}
