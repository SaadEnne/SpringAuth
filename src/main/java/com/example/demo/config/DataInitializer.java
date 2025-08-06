package com.example.demo.config;

import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create a test user if it doesn't exist
        Optional<User> existingUser = userRepository.findByEmail("users@gmail.com");
        if (existingUser.isEmpty()) {
            User testUser = new User();
            testUser.setFirstName("Test");
            testUser.setLastName("User");
            testUser.setEmail("users@gmail.com");
            testUser.setPassword(passwordEncoder.encode("users@gmail.com"));
            testUser.setRole("USER");
            testUser.setIsActive(true);
            testUser.setEmailVerified(false);
            testUser.setCreatedAt(LocalDateTime.now());
            testUser.setProfileVisibility("public");
            testUser.setNotificationsEnabled(true);

            userRepository.save(testUser);
            System.out.println("Test user created successfully!");
        } else {
            System.out.println("Test user already exists!");
        }
    }
}