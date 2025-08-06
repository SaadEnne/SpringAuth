package com.example.demo.repositories;

import com.example.demo.models.MovieCategory;
import com.example.demo.models.User;
import com.example.demo.models.UserMovie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMovieRepository extends JpaRepository<UserMovie, Long> {

    List<UserMovie> findByUserAndCategory(User user, MovieCategory category);

    @Query("SELECT um FROM UserMovie um WHERE um.user = :user AND um.category = :category")
    List<UserMovie> findMoviesByUserAndCategory(@Param("user") User user, @Param("category") MovieCategory category);

    Optional<UserMovie> findByUserAndMovieAndCategory(User user, com.example.demo.models.Movie movie,
            MovieCategory category);

    boolean existsByUserAndMovieAndCategory(User user, com.example.demo.models.Movie movie, MovieCategory category);

    @Query("SELECT um.movie.tmdbId FROM UserMovie um WHERE um.user = :user AND um.category = :category")
    List<Long> findTmdbIdsByUserAndCategory(@Param("user") User user, @Param("category") MovieCategory category);

    void deleteByUserAndMovieAndCategory(User user, com.example.demo.models.Movie movie, MovieCategory category);

    long countByUserAndCategory(User user, MovieCategory category);
}