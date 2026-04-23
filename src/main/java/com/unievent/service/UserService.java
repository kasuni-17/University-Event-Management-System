package com.unievent.service;

import com.unievent.entity.User;
import com.unievent.entity.Role;
import com.unievent.entity.Registration;
import com.unievent.entity.ClubMembership;
import com.unievent.repository.FeedbackRepository;
import com.unievent.repository.RegistrationRepository;
import com.unievent.repository.ClubMembershipRepository;
import com.unievent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private ClubMembershipRepository clubMembershipRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public User createUser(User user) {
        if (user == null || user.getEmail() == null) {
            throw new RuntimeException("Invalid user data: Student Email is required");
        }
        
        String email = user.getEmail().trim().toLowerCase();
        String personalEmail = (user.getPersonalEmail() != null) ? user.getPersonalEmail().trim().toLowerCase() : null;
        
        User record;
        Optional<User> existingUser = userRepository.findByEmail(email);
        
        if (existingUser.isPresent()) {
            record = existingUser.get();
            if (record.isActive()) {
                throw new RuntimeException("This student email is already registered and active.");
            }
            System.out.println(">>> User " + email + " is inactive. Overwriting.");
        } else {
            record = new User();
            record.setEmail(email);
        }

        // Domain validation
        if (user.getRole() == Role.STUDENT || user.getRole() == Role.CLUB_ADMIN) {
            if (!email.endsWith("@my.sliit.lk")) {
                throw new RuntimeException("University email required (must end with @my.sliit.lk)");
            }
            if (personalEmail == null || !personalEmail.endsWith("@gmail.com")) {
                throw new RuntimeException("Personal Gmail required (must end with @gmail.com)");
            }
        }
        
        // Basic detail validation
        String password = user.getPassword();
        if (password == null || password.length() < 8) {
            throw new RuntimeException("Password must be at least 8 characters long");
        }
        
        // Password complexity
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        boolean hasLower = password.chars().anyMatch(Character::isLowerCase);
        boolean hasSpecial = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");

        if (!hasUpper || !hasLower || !hasSpecial) {
            throw new RuntimeException("Password must contain at least one uppercase letter, one lowercase letter, and one special character");
        }

        // Update details
        record.setName(user.getName());
        record.setUniversityId(user.getUniversityId());
        record.setRole(user.getRole());
        record.setPersonalEmail(personalEmail);
        record.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Both STUDENT and CLUB_ADMIN require admin approval now
        record.setApprovalStatus(User.ApprovalStatus.PENDING);
        record.setActive(false); // Account inactive until approved by admin
        
        record.setVerified(true);
        
        return userRepository.save(record);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setName(userDetails.getName());
        if (userDetails.getEmail() != null && !userDetails.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDetails.getEmail())) {
                throw new RuntimeException("Email already taken");
            }
            user.setEmail(userDetails.getEmail());
        }
        // Only update password if provided
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        user.setUniversityId(userDetails.getUniversityId());
        user.setRole(userDetails.getRole());
        user.setActive(userDetails.isActive());
        
        if (userDetails.getPersonalEmail() != null) {
            user.setPersonalEmail(userDetails.getPersonalEmail());
        }
        if (userDetails.getAvatarUrl() != null) {
            user.setAvatarUrl(userDetails.getAvatarUrl());
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Soft delete: mark as inactive and rejected
        user.setActive(false);
        user.setApprovalStatus(User.ApprovalStatus.REJECTED);
        userRepository.save(user);
        
        System.out.println("User deactivated: " + user.getEmail());
    }

    public User loginUser(String email, String password) {
        String trimmedEmail = email.trim().toLowerCase();
        String trimmedPassword = password.trim();

        User user = userRepository.findByEmail(trimmedEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + trimmedEmail));

        if (!passwordEncoder.matches(trimmedPassword, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (!user.isActive()) {
            throw new RuntimeException("User account is inactive or not verified");
        }

        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }

    // ============ ADMIN APPROVAL METHODS ============

    /**
     * Get all pending club admin accounts (awaiting approval)
     */
    public List<User> getPendingClubAdmins() {
        return userRepository.findByRoleAndApprovalStatus(Role.CLUB_ADMIN, User.ApprovalStatus.PENDING);
    }

    /**
     * Get all approved club admins
     */
    public List<User> getApprovedClubAdmins() {
        return userRepository.findByRoleAndApprovalStatus(Role.CLUB_ADMIN, User.ApprovalStatus.APPROVED);
    }

    /**
     * Get users by role
     */
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    /**
     * Get all pending users (any role)
     */
    public List<User> getPendingUsers() {
        return userRepository.findByApprovalStatus(User.ApprovalStatus.PENDING);
    }

    /**
     * Get pending users by role
     */
    public List<User> getPendingUsersByRole(Role role) {
        return userRepository.findByRoleAndApprovalStatus(role, User.ApprovalStatus.PENDING);
    }

    /**
     * Get users by approval status
     */
    public List<User> getUsersByApprovalStatus(User.ApprovalStatus status) {
        return userRepository.findByApprovalStatus(status);
    }

    /**
     * Approve a user account
     */
    public User approveUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setApprovalStatus(User.ApprovalStatus.APPROVED);
        user.setActive(true);
        user.setApprovalDate(LocalDateTime.now());
        
        // Send notification for both students and club admins
        if (user.getRole() == Role.CLUB_ADMIN) {
            emailService.sendClubAdminApprovalEmail(user, true, null);
        } else if (user.getRole() == Role.STUDENT) {
            emailService.sendStudentApprovalEmail(user, true, null);
        }
        
        return userRepository.save(user);
    }

    /**
     * Reject a user account
     */
    public User rejectUser(Long userId, String rejectionReason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        user.setApprovalStatus(User.ApprovalStatus.REJECTED);
        user.setActive(false);
        user.setRejectionReason(rejectionReason);
        user.setApprovalDate(LocalDateTime.now());
        
        // Send notification for both students and club admins
        if (user.getRole() == Role.CLUB_ADMIN) {
            emailService.sendClubAdminApprovalEmail(user, false, rejectionReason);
        } else if (user.getRole() == Role.STUDENT) {
            emailService.sendStudentApprovalEmail(user, false, rejectionReason);
        }
        
        return userRepository.save(user);
    }

    /**
     * Approve a club admin specifically (legacy method, delegates to approveUser)
     */
    public User approveClubAdmin(Long userId) {
        return approveUser(userId);
    }

    /**
     * Reject a club admin specifically (legacy method, delegates to rejectUser)
     */
    public User rejectClubAdmin(Long userId, String rejectionReason) {
        return rejectUser(userId, rejectionReason);
    }

    // ============ SEARCH METHODS ============

    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    public List<User> searchUsersByNameAndRole(String name, Role role) {
        return userRepository.findByNameContainingIgnoreCaseAndRole(name, role);
    }

    // ============ STATUS CHECKING METHODS ============

    public boolean isUserRegisteredForEvent(Long userId, Long eventId) {
        return registrationRepository.findByStudentIdAndEventId(userId, eventId)
                .map(registration -> registration.getStatus() == Registration.RegistrationStatus.CONFIRMED)
                .orElse(false);
    }

    public List<Registration> getUserRegisteredEvents(Long userId) {
        return registrationRepository.findByStudentIdAndStatus(userId, Registration.RegistrationStatus.CONFIRMED);
    }

    public List<ClubMembership> getUserJoinedClubs(Long userId) {
        return clubMembershipRepository.findByStudentIdAndStatus(userId, ClubMembership.MembershipStatus.APPROVED);
    }

    public boolean isUserJoinedClub(Long userId, Long clubId) {
        return clubMembershipRepository.findByStudentIdAndClubId(userId, clubId)
                .map(membership -> membership.getStatus() == ClubMembership.MembershipStatus.APPROVED)
                .orElse(false);
    }

    public boolean isStudent(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRole() == Role.STUDENT)
                .orElse(false);
    }

    public boolean isClubAdmin(Long userId) {
        return userRepository.findById(userId)
                .map(user -> user.getRole() == Role.CLUB_ADMIN && user.isActive())
                .orElse(false);
    }
}
