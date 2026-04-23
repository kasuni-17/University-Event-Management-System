package com.unievent.controller;

import com.unievent.entity.User;
import com.unievent.entity.Role;
import com.unievent.service.UserService;
import com.unievent.service.OtpService;
import com.unievent.service.EmailService;
import com.unievent.dto.OtpRequestDTO;
import com.unievent.dto.VerifyOtpRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allow frontend access
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private EmailService emailService;

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        System.out.println(">>> RECEIVED SIGNUP REQUEST for: " + (user != null ? user.getEmail() : "null"));
        try {
            if (user == null) {
                return ResponseEntity.badRequest().body("Request body is missing");
            }
            if (user.getRole() == null) {
                return ResponseEntity.badRequest().body("Role is required (STUDENT or CLUB_ADMIN)");
            }
            // Normalizing email
            if (user.getEmail() != null) {
                user.setEmail(user.getEmail().trim().toLowerCase());
            } else {
                return ResponseEntity.badRequest().body("Email is required");
            }

            User savedUser = userService.createUser(user);
            System.out.println(">>> SIGNUP SUCCESS FOR: " + savedUser.getEmail());
            return ResponseEntity.ok(savedUser);
        } catch (RuntimeException e) {
            String msg = (e.getMessage() != null && !e.getMessage().trim().isEmpty()) 
                         ? e.getMessage() : "Validation Error: " + e.getClass().getSimpleName();
            System.err.println(">>> SIGNUP VALIDATION FAILED: " + msg);
            return ResponseEntity.badRequest().body(msg);
        } catch (Throwable t) {
            System.err.println(">>> SIGNUP CRITICAL ERROR: " + t.getClass().getName() + " - " + t.getMessage());
            t.printStackTrace();
            String msg = (t.getMessage() != null) ? t.getMessage() : "System Error: " + t.getClass().getSimpleName();
            return ResponseEntity.status(500).body(msg);
        }
    }

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody OtpRequestDTO request) {
        System.out.println(">>> [API] OTP Request Received");
        try {
            if (request == null || request.getEmail() == null || request.getEmail().isBlank()) {
                System.err.println(">>> [API] Error: Missing email in request");
                return ResponseEntity.badRequest().body("Email address is missing from the request.");
            }
            
            String email = request.getEmail().trim().toLowerCase();
            System.out.println(">>> [API] Processing OTP for: " + email);
            
            // Basic domain validation
            if (!email.endsWith("@my.sliit.lk")) {
                return ResponseEntity.badRequest().body("University email required (must end with @my.sliit.lk)");
            }

            // Check existence
            if (userService.getUserByEmail(email).map(User::isActive).orElse(false)) {
                return ResponseEntity.badRequest().body("This student email is already registered and active.");
            }

            String otp = otpService.generateOtp(email);
            System.out.println(">>> [API] OTP Generated. Calling email service...");
            
            // Synchronous call (for now) to catch immediate SMTP errors
            emailService.sendOtpEmail(email, otp);
            
            return ResponseEntity.ok("OTP successfully sent to " + email);
        } catch (Exception e) {
            System.err.println(">>> [API] CRITICAL OTP ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpRequestDTO request) {
        if (request == null || request.getEmail() == null || request.getOtp() == null) {
            return ResponseEntity.badRequest().body("Email and OTP are required.");
        }
        String email = request.getEmail().trim().toLowerCase();
        String otp = request.getOtp();
        System.out.println(">>> OTP VERIFICATION ATTEMPT FOR: " + email);

        if (otpService.verifyOtp(email, otp)) {
            return ResponseEntity.ok("OTP verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid or expired OTP");
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            return ResponseEntity.ok(userService.updateUser(id, userDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println(">>> LOGIN ATTEMPT: " + loginRequest.getEmail());
            User user = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
            System.out.println(">>> LOGIN SUCCESS: " + user.getEmail() + " (Role: " + user.getRole() + ")");
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            System.err.println(">>> LOGIN FAILED for " + loginRequest.getEmail() + ": " + e.getMessage());
            return ResponseEntity.status(401).body(e.getMessage());
        } catch (Exception e) {
            System.err.println(">>> LOGIN CRITICAL ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    // ============ ADMIN ENDPOINTS ============

    /**
     * Get all pending users
     */
    @GetMapping("/admin/pending")
    public ResponseEntity<List<User>> getPendingUsers() {
        return ResponseEntity.ok(userService.getPendingUsers());
    }

    /**
     * Get pending users by role
     */
    @GetMapping("/admin/pending/role/{role}")
    public ResponseEntity<List<User>> getPendingUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getPendingUsersByRole(role));
    }

    /**
     * Get users by role (e.g., STUDENT, CLUB_ADMIN, DEPT_ADMIN, SUPER_ADMIN)
     */
    @GetMapping("/admin/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    /**
     * Search users by name
     */
    @GetMapping("/admin/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String name) {
        return ResponseEntity.ok(userService.searchUsersByName(name));
    }

    /**
     * Search users by name and role
     */
    @GetMapping("/admin/search/role/{role}")
    public ResponseEntity<List<User>> searchUsersByRole(@RequestParam String name, @PathVariable Role role) {
        return ResponseEntity.ok(userService.searchUsersByNameAndRole(name, role));
    }

    /**
     * Get users by approval status
     */
    @GetMapping("/admin/approval/{status}")
    public ResponseEntity<List<User>> getUsersByApprovalStatus(@PathVariable User.ApprovalStatus status) {
        return ResponseEntity.ok(userService.getUsersByApprovalStatus(status));
    }

    /**
     * Approve user registration
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<User> approveUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.approveUser(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Reject user registration
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<User> rejectUser(@PathVariable Long id, @RequestParam(required = false) String reason) {
        try {
            return ResponseEntity.ok(userService.rejectUser(id, reason));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
