package com.example.demo.dto;

import java.util.List;

public class MovieDto {
    private Long id;
    private Long tmdbId;
    private String title;
    private String overview;
    private String posterPath;
    private String backdropPath;
    private String releaseDate;
    private Double voteAverage;
    private Integer voteCount;
    private String originalLanguage;
    private String originalTitle;
    private Boolean adult;
    private String status;
    private String tagline;
    private Integer runtime;
    private List<String> genres;
    private String type; // 'movie' or 'tv'
    private String category; // FAVORITE, WATCHED, WATCHLIST
    private Boolean isInUserList;

    // Default constructor
    public MovieDto() {
    }

    // Constructor with all fields
    public MovieDto(Long id, Long tmdbId, String title, String overview, String posterPath,
            String backdropPath, String releaseDate, Double voteAverage, Integer voteCount,
            String originalLanguage, String originalTitle, Boolean adult, String status,
            String tagline, Integer runtime, List<String> genres, String type, String category, Boolean isInUserList) {
        this.id = id;
        this.tmdbId = tmdbId;
        this.title = title;
        this.overview = overview;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.adult = adult;
        this.status = status;
        this.tagline = tagline;
        this.runtime = runtime;
        this.genres = genres;
        this.type = type;
        this.category = category;
        this.isInUserList = isInUserList;
    }

    // === Getters ===
    public Long getId() {
        return id;
    }

    public Long getTmdbId() {
        return tmdbId;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public Boolean getAdult() {
        return adult;
    }

    public String getStatus() {
        return status;
    }

    public String getTagline() {
        return tagline;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public List<String> getGenres() {
        return genres;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public Boolean getIsInUserList() {
        return isInUserList;
    }

    // === Setters ===
    public void setId(Long id) {
        this.id = id;
    }

    public void setTmdbId(Long tmdbId) {
        this.tmdbId = tmdbId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setIsInUserList(Boolean isInUserList) {
        this.isInUserList = isInUserList;
    }
}