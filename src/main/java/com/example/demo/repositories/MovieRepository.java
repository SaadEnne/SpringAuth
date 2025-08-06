package com.example.demo.repositories;

import com.example.demo.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    
    Optional<Movie> findByTmdbId(Long tmdbId);
    
    boolean existsByTmdbId(Long tmdbId);
    
    @Query("SELECT m FROM Movie m WHERE m.tmdbId IN :tmdbIds")
    List<Movie> findByTmdbIds(@Param("tmdbIds") List<Long> tmdbIds);
} 