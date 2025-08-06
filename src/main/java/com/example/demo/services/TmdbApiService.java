package com.example.demo.services;

import com.example.demo.dto.MovieDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        try {
            String url = String.format("%s/movie/%d?api_key=%s&language=en-US", baseUrl, tmdbId, apiKey);
            String response = restTemplate.getForObject(url, String.class);

            if (response != null) {
                JsonNode jsonNode = objectMapper.readTree(response);
                return mapJsonToMovieDto(jsonNode);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    private MovieDto mapJsonToMovieDto(JsonNode jsonNode) {
        MovieDto movieDto = new MovieDto();

        movieDto.setTmdbId(jsonNode.get("id").asLong());
        movieDto.setTitle(jsonNode.get("title").asText());
        movieDto.setOverview(jsonNode.get("overview").asText());
        movieDto.setPosterPath(jsonNode.get("poster_path").asText());
        movieDto.setBackdropPath(jsonNode.get("backdrop_path") != null ? jsonNode.get("backdrop_path").asText() : null);
        movieDto.setReleaseDate(jsonNode.get("release_date").asText());
        movieDto.setVoteAverage(jsonNode.get("vote_average").asDouble());
        movieDto.setVoteCount(jsonNode.get("vote_count").asInt());
        movieDto.setOriginalLanguage(jsonNode.get("original_language").asText());
        movieDto.setOriginalTitle(jsonNode.get("original_title").asText());
        movieDto.setAdult(jsonNode.get("adult").asBoolean());
        movieDto.setStatus(jsonNode.get("status") != null ? jsonNode.get("status").asText() : null);
        movieDto.setTagline(jsonNode.get("tagline") != null ? jsonNode.get("tagline").asText() : null);
        movieDto.setRuntime(jsonNode.get("runtime") != null ? jsonNode.get("runtime").asInt() : null);

        // Handle genres
        List<String> genres = new ArrayList<>();
        if (jsonNode.has("genres")) {
            JsonNode genresNode = jsonNode.get("genres");
            for (JsonNode genre : genresNode) {
                genres.add(genre.get("name").asText());
            }
        }
        movieDto.setGenres(genres);

        return movieDto;
    }
}