package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    // CORS is now handled by WebConfig.java

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
                .cors(cors -> cors.and())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/test").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/{userId}").permitAll() // Public profiles
                        .requestMatchers(HttpMethod.GET, "/api/test/**").permitAll() // Test endpoints

                        // Protected endpoints - authentication required
                        .requestMatchers(HttpMethod.POST, "/api/auth/logout").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/profile").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/profile").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/change-password").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/account").authenticated()

                        // Movie endpoints - authentication required
                        .requestMatchers(HttpMethod.POST, "/api/movies/user-movie").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/movies/user/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/movies/check-status/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/movies/test-auth").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/movies/cleanup-duplicates").authenticated()

                        // Legacy endpoints (if still needed)
                        .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/{id}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").authenticated()

                        // Allow all other requests
                        .anyRequest().permitAll())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}