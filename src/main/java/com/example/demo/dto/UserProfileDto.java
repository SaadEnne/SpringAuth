package com.example.demo.dto;

import java.time.LocalDate;

public class UserProfileDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String avatar;
    private String bio;
    private String location;
    private String website;
    private String phone;
    private LocalDate dateOfBirth;
    private String preferredLanguage;
    private String timezone;
    private Boolean notificationsEnabled;
    private String profileVisibility;

    // Default constructor
    public UserProfileDto() {
    }

    // Constructor with all fields
    public UserProfileDto(Long id, String firstName, String lastName, String email, String avatar,
            String bio, String location, String website, String phone, LocalDate dateOfBirth,
            String preferredLanguage, String timezone, Boolean notificationsEnabled, String profileVisibility) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.avatar = avatar;
        this.bio = bio;
        this.location = location;
        this.website = website;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.preferredLanguage = preferredLanguage;
        this.timezone = timezone;
        this.notificationsEnabled = notificationsEnabled;
        this.profileVisibility = profileVisibility;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getBio() {
        return bio;
    }

    public String getLocation() {
        return location;
    }

    public String getWebsite() {
        return website;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public String getTimezone() {
        return timezone;
    }

    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }

    public String getProfileVisibility() {
        return profileVisibility;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public void setProfileVisibility(String profileVisibility) {
        this.profileVisibility = profileVisibility;
    }
}