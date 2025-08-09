package com.example.demo.dto;

public class UserMovieRequestDto {
    private Long tmdbId;
    private String category; // FAVORITE, WATCHED, WATCHLIST
    private String action; // ADD, REMOVE

    // Default constructor
    public UserMovieRequestDto() {
    }

    // Constructor with all fields
    public UserMovieRequestDto(Long tmdbId, String category, String action) {
        this.tmdbId = tmdbId;
        this.category = category;
        this.action = action;
    }

    // === Getters ===
    public Long getTmdbId() {
        return tmdbId;
    }

    public String getCategory() {
        return category;
    }

    public String getAction() {
        return action;
    }

    // === Setters ===
    public void setTmdbId(Long tmdbId) {
        this.tmdbId = tmdbId;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setAction(String action) {
        this.action = action;
    }
}