package com.unievent;

import com.unievent.entity.User;
import com.unievent.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class

UserVerificationRunner implements CommandLineRunner {
    private final UserRepository userRepository;

    public UserVerificationRunner(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        System.out.println("--- DB USER VERIFICATION ---");
        userRepository.findAll().forEach(u -> 
            System.out.println("Email: " + u.getEmail() + ", Role: " + u.getRole() + ", Active: " + u.isActive())
        );
        System.out.println("---------------------------");
    }
}
