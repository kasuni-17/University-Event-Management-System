package com.unievent.service;

import com.unievent.entity.Feedback;
import com.unievent.entity.Event;
import com.unievent.entity.User;
import com.unievent.repository.FeedbackRepository;
import com.unievent.repository.EventRepository;
import com.unievent.repository.UserRepository;
import com.unievent.dto.FeedbackRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeedbackService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    public Feedback submitFeedback(Feedback feedback) {
        feedback.setSubmittedAt(LocalDateTime.now());
        return feedbackRepository.save(feedback);
    }

    public Feedback submitFeedbackRequest(FeedbackRequest request) {
        Feedback f = new Feedback();
        f.setRating(request.getRating());
        f.setComment(request.getComment());
        f.setSubmittedAt(LocalDateTime.now());

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
        f.setEvent(event);

        if (!request.isAnonymous() && request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            f.setStudent(user);
        }

        return feedbackRepository.save(f);
    }

    public List<Feedback> getFeedbackByEvent(Long eventId) {
        return feedbackRepository.findByEventId(eventId);
    }

    public List<Feedback> getFeedbackByStudent(Long studentId) {
        return feedbackRepository.findByStudentId(studentId);
    }

    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }

    // ============ ADMIN METHODS ============

    /**
     * Get feedback count for an event
     */
    public long getFeedbackCountByEvent(Long eventId) {
        return feedbackRepository.countFeedbackByEvent(eventId);
    }

    /**
     * Get average rating for an event
     */
    public double getAverageRatingByEvent(Long eventId) {
        Double average = feedbackRepository.getAverageRatingByEvent(eventId);
        return average != null ? average : 0.0;
    }

    /**
     * Get feedback analytics for an event
     */
    public Map<String, Object> getEventAnalytics(Long eventId) {
        Map<String, Object> analytics = new HashMap<>();
        List<Feedback> feedbacks = feedbackRepository.findByEventId(eventId);

        analytics.put("totalFeedback", feedbacks.size());
        analytics.put("averageRating", getAverageRatingByEvent(eventId));

        // Calculate rating distribution
        Map<Integer, Integer> ratingDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            ratingDistribution.put(i, 0);
        }

        for (Feedback feedback : feedbacks) {
            int rating = feedback.getRating();
            if (rating >= 1 && rating <= 5) {
                ratingDistribution.put(rating, ratingDistribution.get(rating) + 1);
            }
        }

        analytics.put("ratingDistribution", ratingDistribution);
        analytics.put("feedbacks", feedbacks);

        return analytics;
    }

    /**
     * Get all feedbacks for completed events
     */
    public List<Feedback> getAllCompletedEventsFeedback() {
        return feedbackRepository.findFeedbackForCompletedEvents();
    }
}
