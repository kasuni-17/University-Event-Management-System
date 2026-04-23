package com.unievent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.core.env.Environment;
import java.util.Optional;

@SpringBootApplication
@EnableAsync
public class UniEventApplication {

	public static void main(String[] args) {
		try {
			SpringApplication app = new SpringApplication(UniEventApplication.class);
			app.run(args);
		} catch (Exception e) {
			System.err.println("=== APPLICATION STARTUP FAILED ===");
			System.err.println("Error: " + e.getMessage());
			System.err.println("\n=== POSSIBLE SOLUTIONS ===");
			System.err.println("1. Ensure MySQL server is running on localhost:3306");
			System.err.println("2. Verify database credentials in application.properties");
			System.err.println("3. Check if database 'unievent_db' exists");
			System.err.println("4. Verify MySQL user 'root' has necessary permissions");
			System.err.println("\n=== DETAILED ERROR ===");
			e.printStackTrace();
			System.exit(1);
		}
	}

    @org.springframework.context.annotation.Bean
    public org.springframework.boot.CommandLineRunner initData(
            com.unievent.repository.UserRepository userRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        return args -> {
            try {
                String adminEmail = "superadmin@gmail.com".toLowerCase().trim();
                System.out.println(">>> SEED: Initializing admin check for: " + adminEmail);
                
                Optional<com.unievent.entity.User> existingAdmin = userRepository.findByEmail(adminEmail);
                com.unievent.entity.User admin;

                if (existingAdmin.isPresent()) {
                    System.out.println(">>> SEED: Found existing record, updating for fresh state...");
                    admin = existingAdmin.get();
                } else {
                    System.out.println(">>> SEED: Creating new super admin...");
                    admin = new com.unievent.entity.User();
                    admin.setEmail(adminEmail);
                }

                admin.setName("Super Admin");
                admin.setPassword(passwordEncoder.encode("Admin2004@"));
                admin.setRole(com.unievent.entity.Role.ADMIN);
                admin.setApprovalStatus(com.unievent.entity.User.ApprovalStatus.APPROVED);
                admin.setActive(true);
                admin.setUniversityId("ADMIN01");
                admin.setVerified(true);
                
                userRepository.save(admin);
                System.out.println(">>> SEED SUCCESS: Super Admin fully stored: " + adminEmail + " / Admin2004@");
                
            } catch (Exception e) {
                System.err.println(">>> SEED ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

}
