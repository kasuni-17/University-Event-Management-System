package com.unievent.service;

import com.unievent.entity.Registration;
import com.unievent.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EmailService emailService;

    public Registration registerStudent(Registration registration) {
        if (registrationRepository.findByStudentIdAndEventId(
                registration.getStudent().getId(), registration.getEvent().getId()).isPresent()) {
            throw new RuntimeException("Student already registered for this event");
        }
        registration.setRegistrationDate(LocalDateTime.now());
        
        // If event registration is immediate (confirmed)
        registration.setStatus(Registration.RegistrationStatus.CONFIRMED);
        Registration saved = registrationRepository.save(registration);
        
        // Send confirmation email
        emailService.sendEventRegistrationConfirmationEmail(saved.getStudent(), saved.getEvent());
        
        return saved;
    }

    public List<Registration> getRegistrationsByEvent(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    public List<Registration> getRegistrationsByStudent(Long studentId) {
        return registrationRepository.findByStudentId(studentId);
    }

    public Registration updateStatus(Long id, Registration.RegistrationStatus status) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        registration.setStatus(status);
        return registrationRepository.save(registration);
    }

    public void cancelRegistration(Long id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found"));
        registration.setStatus(Registration.RegistrationStatus.CANCELLED);
        registrationRepository.save(registration);
        // Or hard delete: registrationRepository.deleteById(id);
    }
}
