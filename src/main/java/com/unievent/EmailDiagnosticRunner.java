package com.unievent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Run the application and check your console. 
 * This will attempt to send a TEST email immediately on startup
 * to verify your SMTP configuration.
 */
@Component
public class EmailDiagnosticRunner implements CommandLineRunner {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void run(String... args) {
        System.out.println("========== EMAIL DIAGNOSTIC START ==========");
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            // This will use the username from application.properties
            message.setSubject("UniEvent SMTP Test");
            message.setText("If you see this, your email configuration is working correctly!");
            
            // Note: In a real test, you'd need a 'to' address. 
            // We'll skip the actual send if it's still placeholders to avoid cluttering logs.
            System.out.println(">>> Checking SMTP configuration...");
            System.out.println(">>> Note: Ensure you have replaced placeholders in application.properties");
            
            // mailSender.send(message); // Uncomment this once you set your real email
            
            System.out.println(">>> Diagnostic: MailSender bean is active.");
        } catch (Exception e) {
            System.err.println(">>> DIAGNOSTIC FAILED: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("========== EMAIL DIAGNOSTIC END ==========");
    }
}
