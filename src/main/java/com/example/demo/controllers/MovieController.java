package com.example.demo.controllers;

import com.example.demo.dto.MovieDto;
import com.example.demo.dto.UserMovieRequestDto;
import com.example.demo.models.MovieCategory;
import com.example.demo.models.User;
import com.example.demo.services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "http://localhost:4200")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @PostMapping("/user-movie")
    public ResponseEntity<?> handleUserMovieRequest(@RequestBody UserMovieRequestDto request) {
        try {
            // TODO: Get current user from authentication context
            // For now, we'll use a mock user - replace this with actual user authentication
            User currentUser = getCurrentUser(); // This should be implemented based on your auth system

            movieService.handleUserMovieRequest(currentUser, request);
            return ResponseEntity.ok()
                    .body(Map.of("message", "Movie " + request.getAction().toLowerCase() + "d successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{category}")
    public ResponseEntity<List<MovieDto>> getUserMoviesByCategory(@PathVariable String category) {
        try {
            // TODO: Get current user from authentication context
            User currentUser = getCurrentUser(); // This should be implemented based on your auth system

            MovieCategory movieCategory = MovieCategory.valueOf(category.toUpperCase());
            List<MovieDto> movies = movieService.getUserMoviesByCategory(currentUser, movieCategory);

            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/favorites")
    public ResponseEntity<List<MovieDto>> getUserFavorites() {
        return getUserMoviesByCategory("FAVORITE");
    }

    @GetMapping("/user/watched")
    public ResponseEntity<List<MovieDto>> getUserWatched() {
        return getUserMoviesByCategory("WATCHED");
    }

    @GetMapping("/user/watchlist")
    public ResponseEntity<List<MovieDto>> getUserWatchlist() {
        return getUserMoviesByCategory("WATCHLIST");
    }

    @GetMapping("/check-status/{tmdbId}")
    public ResponseEntity<Map<String, Boolean>> checkMovieStatus(@PathVariable Long tmdbId) {
        try {
            User currentUser = getCurrentUser();

            Map<String, Boolean> status = new HashMap<>();
            status.put("isFavorite", movieService.isMovieInUserCategory(currentUser, tmdbId, MovieCategory.FAVORITE));
            status.put("isWatched", movieService.isMovieInUserCategory(currentUser, tmdbId, MovieCategory.WATCHED));
            status.put("isInWatchlist",
                    movieService.isMovieInUserCategory(currentUser, tmdbId, MovieCategory.WATCHLIST));

            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/statistics")
    public ResponseEntity<Map<String, Long>> getUserMovieStatistics() {
        try {
            User currentUser = getCurrentUser();

            Map<String, Long> statistics = new HashMap<>();
            statistics.put("favorites", movieService.getUserMovieCountByCategory(currentUser, MovieCategory.FAVORITE));
            statistics.put("watched", movieService.getUserMovieCountByCategory(currentUser, MovieCategory.WATCHED));
            statistics.put("watchlist", movieService.getUserMovieCountByCategory(currentUser, MovieCategory.WATCHLIST));

            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // TODO: Implement this method based on your authentication system
    private User getCurrentUser() {
        // This is a placeholder - implement based on your authentication system
        // You might get the user from JWT token, session, or security context
        User mockUser = new User();
        mockUser.setId(1L); // Replace with actual user ID from authentication
        return mockUser;
    }
}