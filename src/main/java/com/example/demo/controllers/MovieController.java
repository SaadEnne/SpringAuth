package com.example.demo.controllers;

import com.example.demo.dto.MovieDto;
import com.example.demo.dto.UserMovieRequestDto;
import com.example.demo.models.MovieCategory;
import com.example.demo.models.User;
import com.example.demo.services.MovieService;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/user-movie")
    public ResponseEntity<?> handleUserMovieRequest(@RequestBody UserMovieRequestDto request) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

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
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).build();
            }

            // Clean up duplicates first
            movieService.cleanupDuplicates(currentUser);

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
            if (currentUser == null) {
                return ResponseEntity.status(401).build();
            }

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
            if (currentUser == null) {
                return ResponseEntity.status(401).build();
            }

            // Clean up duplicates first
            movieService.cleanupDuplicates(currentUser);

            Map<String, Long> statistics = new HashMap<>();
            statistics.put("favorites", movieService.getUserMovieCountByCategory(currentUser, MovieCategory.FAVORITE));
            statistics.put("watched", movieService.getUserMovieCountByCategory(currentUser, MovieCategory.WATCHED));
            statistics.put("watchlist", movieService.getUserMovieCountByCategory(currentUser, MovieCategory.WATCHLIST));

            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/cleanup-duplicates")
    public ResponseEntity<Map<String, String>> cleanupDuplicates() {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(401).body(Map.of("error", "User not authenticated"));
            }

            movieService.cleanupDuplicates(currentUser);
            return ResponseEntity.ok(Map.of("message", "Duplicates cleaned up successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/test-auth")
    public ResponseEntity<Map<String, Object>> testAuthentication() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Authentication successful",
                "user", Map.of(
                        "id", currentUser.getId(),
                        "email", currentUser.getEmail(),
                        "firstName", currentUser.getFirstName(),
                        "lastName", currentUser.getLastName())));
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {
            User user = userRepository.findByEmail(authentication.getName()).orElse(null);
            if (user == null) {
                System.out.println("User not found in database: " + authentication.getName());
            }
            return user;
        }
        System.out.println("No authenticated user found - Authentication: " +
                (authentication != null ? authentication.getName() : "null"));
        return null;
    }
}