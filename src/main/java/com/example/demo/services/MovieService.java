package com.example.demo.services;

import com.example.demo.dto.MovieDto;
import com.example.demo.dto.UserMovieRequestDto;
import com.example.demo.models.Movie;
import com.example.demo.models.MovieCategory;
import com.example.demo.models.User;
import com.example.demo.models.UserMovie;
import com.example.demo.repositories.MovieRepository;
import com.example.demo.repositories.UserMovieRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserMovieRepository userMovieRepository;

    @Autowired
    private TmdbApiService tmdbApiService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Movie saveMovieFromTmdb(Long tmdbId) {
        // Check if movie already exists
        Optional<Movie> existingMovie = movieRepository.findByTmdbId(tmdbId);
        if (existingMovie.isPresent()) {
            return existingMovie.get();
        }

        // Fetch movie details from TMDB API
        MovieDto movieDto = tmdbApiService.getMovieDetails(tmdbId);
        if (movieDto == null) {
            throw new RuntimeException("Failed to fetch movie details from TMDB API");
        }

        // Create and save movie entity
        Movie movie = new Movie();
        movie.setTmdbId(movieDto.getTmdbId());
        movie.setTitle(movieDto.getTitle());
        // Truncate overview to prevent database truncation error
        String overview = movieDto.getOverview();
        if (overview != null && overview.length() > 255) {
            overview = overview.substring(0, 255);
        }
        movie.setOverview(overview);
        movie.setPosterPath(movieDto.getPosterPath());
        movie.setBackdropPath(movieDto.getBackdropPath());
        movie.setReleaseDate(movieDto.getReleaseDate());
        movie.setVoteAverage(movieDto.getVoteAverage());
        movie.setVoteCount(movieDto.getVoteCount());
        movie.setOriginalLanguage(movieDto.getOriginalLanguage());
        movie.setOriginalTitle(movieDto.getOriginalTitle());
        movie.setAdult(movieDto.getAdult());
        movie.setStatus(movieDto.getStatus());
        movie.setTagline(movieDto.getTagline());
        movie.setRuntime(movieDto.getRuntime());

        // Convert genres list to JSON string
        try {
            movie.setGenres(objectMapper.writeValueAsString(movieDto.getGenres()));
        } catch (JsonProcessingException e) {
            movie.setGenres("[]");
        }

        return movieRepository.save(movie);
    }

    public void addMovieToUserCategory(User user, Long tmdbId, MovieCategory category) {
        // Save movie to database if it doesn't exist
        Movie movie = saveMovieFromTmdb(tmdbId);

        // Check if relationship already exists
        if (!userMovieRepository.existsByUserAndMovieAndCategory(user, movie, category)) {
            UserMovie userMovie = new UserMovie();
            userMovie.setUser(user);
            userMovie.setMovie(movie);
            userMovie.setCategory(category);
            userMovieRepository.save(userMovie);
        }
    }

    public void removeMovieFromUserCategory(User user, Long tmdbId, MovieCategory category) {
        Optional<Movie> movie = movieRepository.findByTmdbId(tmdbId);
        if (movie.isPresent()) {
            userMovieRepository.deleteByUserAndMovieAndCategory(user, movie.get(), category);
        }
    }

    public List<MovieDto> getUserMoviesByCategory(User user, MovieCategory category) {
        // Get TMDB IDs for the user's movies in the specified category
        List<Long> tmdbIds = userMovieRepository.findTmdbIdsByUserAndCategory(user, category);

        // Fetch detailed movie information from TMDB API
        List<MovieDto> movies = tmdbApiService.getMoviesByIds(tmdbIds);

        // Set the category for each movie
        for (MovieDto movie : movies) {
            movie.setCategory(category.name());
            movie.setIsInUserList(true);
        }

        return movies;
    }

    public boolean isMovieInUserCategory(User user, Long tmdbId, MovieCategory category) {
        Optional<Movie> movie = movieRepository.findByTmdbId(tmdbId);
        if (movie.isPresent()) {
            return userMovieRepository.existsByUserAndMovieAndCategory(user, movie.get(), category);
        }
        return false;
    }

    public void handleUserMovieRequest(User user, UserMovieRequestDto request) {
        MovieCategory category = MovieCategory.valueOf(request.getCategory().toUpperCase());

        if ("ADD".equalsIgnoreCase(request.getAction())) {
            addMovieToUserCategory(user, request.getTmdbId(), category);
        } else if ("REMOVE".equalsIgnoreCase(request.getAction())) {
            removeMovieFromUserCategory(user, request.getTmdbId(), category);
        }
    }

    public Long getUserMovieCountByCategory(User user, MovieCategory category) {
        return userMovieRepository.countByUserAndCategory(user, category);
    }
}