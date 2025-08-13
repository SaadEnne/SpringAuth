package com.example.demo.services;

import com.example.demo.dto.MovieDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

@Service
public class TmdbApiService {

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.base-url:https://api.themoviedb.org/3}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public TmdbApiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public MovieDto getMovieDetails(Long tmdbId) {
        // Try movie endpoint first
        String movieUrl = String.format("%s/movie/%d?api_key=%s&language=en-US", baseUrl, tmdbId, apiKey);
        try {
            String response = restTemplate.getForObject(movieUrl, String.class);
            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                return mapJsonToMovieOrTvDto(jsonNode, "movie");
            }
        } catch (RestClientException ex) {
            // Fallback to TV endpoint (IDs for TV series)
            String tvUrl = String.format("%s/tv/%d?api_key=%s&language=en-US", baseUrl, tmdbId, apiKey);
            try {
                String response = restTemplate.getForObject(tvUrl, String.class);
                if (response != null) {
                    JsonNode jsonNode = objectMapper.readTree(response);
                    return mapJsonToMovieOrTvDto(jsonNode, "tv");
                }
            } catch (Exception ignore) {
                // Will return null below
            }
        } catch (Exception e) {
            // Unknown error; return null to let caller handle
        }
        return null;
    }

    public List<MovieDto> getMoviesByIds(List<Long> tmdbIds) {
        List<MovieDto> movies = new ArrayList<>();

        for (Long tmdbId : tmdbIds) {
            MovieDto movie = getMovieDetails(tmdbId);
            if (movie != null) {
                movies.add(movie);
            }
        }

        return movies;
    }

    private MovieDto mapJsonToMovieOrTvDto(JsonNode jsonNode, String type) {
        MovieDto movieDto = new MovieDto();

        movieDto.setTmdbId(jsonNode.get("id").asLong());
        // title for movie, name for TV
        if (jsonNode.hasNonNull("title")) {
            movieDto.setTitle(jsonNode.get("title").asText());
        } else if (jsonNode.hasNonNull("name")) {
            movieDto.setTitle(jsonNode.get("name").asText());
        }
        movieDto.setOverview(jsonNode.get("overview").asText());
        movieDto.setPosterPath(jsonNode.get("poster_path").asText());
        movieDto.setBackdropPath(jsonNode.get("backdrop_path") != null ? jsonNode.get("backdrop_path").asText() : null);
        // release_date for movie, first_air_date for TV
        if (jsonNode.hasNonNull("release_date")) {
            movieDto.setReleaseDate(jsonNode.get("release_date").asText());
        } else if (jsonNode.hasNonNull("first_air_date")) {
            movieDto.setReleaseDate(jsonNode.get("first_air_date").asText());
        }
        movieDto.setVoteAverage(jsonNode.get("vote_average").asDouble());
        movieDto.setVoteCount(jsonNode.get("vote_count").asInt());
        movieDto.setOriginalLanguage(jsonNode.get("original_language").asText());
        // original title vs original name
        if (jsonNode.hasNonNull("original_title")) {
            movieDto.setOriginalTitle(jsonNode.get("original_title").asText());
        } else if (jsonNode.hasNonNull("original_name")) {
            movieDto.setOriginalTitle(jsonNode.get("original_name").asText());
        }
        movieDto.setAdult(
                jsonNode.has("adult") && !jsonNode.get("adult").isNull() ? jsonNode.get("adult").asBoolean() : false);
        movieDto.setStatus(
                jsonNode.has("status") && !jsonNode.get("status").isNull() ? jsonNode.get("status").asText() : null);
        movieDto.setTagline(
                jsonNode.has("tagline") && !jsonNode.get("tagline").isNull() ? jsonNode.get("tagline").asText() : null);
        // runtime for movie, episode_run_time (array) for TV
        if (jsonNode.hasNonNull("runtime")) {
            movieDto.setRuntime(jsonNode.get("runtime").asInt());
        } else if (jsonNode.has("episode_run_time") && jsonNode.get("episode_run_time").isArray()
                && jsonNode.get("episode_run_time").size() > 0) {
            movieDto.setRuntime(jsonNode.get("episode_run_time").get(0).asInt());
        } else {
            movieDto.setRuntime(null);
        }

        // Handle genres
        List<String> genres = new ArrayList<>();
        if (jsonNode.has("genres")) {
            JsonNode genresNode = jsonNode.get("genres");
            for (JsonNode genre : genresNode) {
                genres.add(genre.get("name").asText());
            }
        }
        movieDto.setGenres(genres);

        // Set the type
        movieDto.setType(type);

        return movieDto;
    }
}