package com.example.demo.controllers;

import com.example.demo.dto.PasswordChangeDto;
import com.example.demo.dto.UserProfileDto;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import com.example.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Create new user (registration)
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            // Validate required fields
            if (user.getEmail() == null || user.getEmail().isEmpty() ||
                    user.getPassword() == null || user.getPassword().isEmpty() ||
                    user.getFirstName() == null || user.getFirstName().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email, password, and firstName are required"));
            }

            // Check if email already exists
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email already exists"));
            }

            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Set default values
            user.setIsActive(true);
            user.setRole("USER");
            user.setEmailVerified(false);
            user.setProfileVisibility("public");
            user.setNotificationsEnabled(true);

            User savedUser = userRepository.save(user);

            // Return user data without password
            Map<String, Object> response = new HashMap<>();
            response.put("id", savedUser.getId());
            response.put("firstName", savedUser.getFirstName());
            response.put("lastName", savedUser.getLastName());
            response.put("email", savedUser.getEmail());
            response.put("message", "User created successfully");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to create user: " + e.getMessage()));
        }
    }

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile(@RequestHeader("Authorization") String authorization) {
        try {
            // Extract user ID from JWT token
            String token = authorization.replace("Bearer ", "");
            String userEmail = jwtUtil.extractUsername(token);

            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            UserProfileDto profileDto = convertToDto(user);

            return ResponseEntity.ok(profileDto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve user profile"));
        }
    }

    /**
     * Update user profile
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(
            @RequestHeader("Authorization") String authorization,
            @RequestBody UserProfileDto profileDto) {
        try {
            // Extract user ID from JWT token
            String token = authorization.replace("Bearer ", "");
            String userEmail = jwtUtil.extractUsername(token);

            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();

            // Update user fields (excluding sensitive fields like email and password)
            user.setFirstName(profileDto.getFirstName());
            user.setLastName(profileDto.getLastName());
            user.setAvatar(profileDto.getAvatar());
            user.setBio(profileDto.getBio());
            user.setLocation(profileDto.getLocation());
            user.setWebsite(profileDto.getWebsite());
            user.setPhone(profileDto.getPhone());
            user.setDateOfBirth(profileDto.getDateOfBirth());
            user.setPreferredLanguage(profileDto.getPreferredLanguage());
            user.setTimezone(profileDto.getTimezone());
            user.setNotificationsEnabled(profileDto.getNotificationsEnabled());
            user.setProfileVisibility(profileDto.getProfileVisibility());

            User savedUser = userRepository.save(user);
            UserProfileDto updatedProfileDto = convertToDto(savedUser);

            return ResponseEntity.ok(updatedProfileDto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to update user profile"));
        }
    }

    /**
     * Change user password
     */
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authorization,
            @RequestBody PasswordChangeDto passwordDto) {
        try {
            // Extract user ID from JWT token
            String token = authorization.replace("Bearer ", "");
            String userEmail = jwtUtil.extractUsername(token);

            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();

            // Validate current password
            if (!passwordEncoder.matches(passwordDto.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Current password is incorrect"));
            }

            // Validate new password
            if (!passwordDto.getNewPassword().equals(passwordDto.getConfirmPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "New password and confirmation password do not match"));
            }

            if (passwordDto.getNewPassword().length() < 6) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "New password must be at least 6 characters long"));
            }

            // Update password
            user.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to change password"));
        }
    }

    /**
     * Get user by ID (for public profiles)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();

            // Check if profile is public
            if (!"public".equals(user.getProfileVisibility())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Profile is not public"));
            }

            UserProfileDto profileDto = convertToDto(user);
            return ResponseEntity.ok(profileDto);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to retrieve user profile"));
        }
    }

    /**
     * Delete user account
     */
    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.replace("Bearer ", "");
            String userEmail = jwtUtil.extractUsername(token);

            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            User user = userOpt.get();
            user.setIsActive(false);
            userRepository.save(user);

            return ResponseEntity.ok(Map.of("message", "Account deactivated successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to deactivate account"));
        }
    }

    /**
     * Test endpoint to verify controller is working
     */
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        return ResponseEntity.ok(Map.of("message", "User controller is working!"));
    }

    /**
     * Convert User entity to UserProfileDto
     */
    private UserProfileDto convertToDto(User user) {
        return new UserProfileDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getAvatar(),
                user.getBio(),
                user.getLocation(),
                user.getWebsite(),
                user.getPhone(),
                user.getDateOfBirth(),
                user.getPreferredLanguage(),
                user.getTimezone(),
                user.getNotificationsEnabled(),
                user.getProfileVisibility());
    }
}
