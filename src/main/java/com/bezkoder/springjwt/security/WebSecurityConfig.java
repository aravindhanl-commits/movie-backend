package com.bezkoder.springjwt.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.bezkoder.springjwt.security.jwt.AuthEntryPointJwt;
import com.bezkoder.springjwt.security.jwt.AuthTokenFilter;
import com.bezkoder.springjwt.security.services.UserDetailsServiceImpl;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // 🔹 JWT filter
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    // 🔹 Authentication provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 🔹 Authentication manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // 🔹 Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Enable CORS for frontend apps
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",   // React (default)
            "http://localhost:5173",   // Vite
            "http://localhost:5500",   // VSCode Live Server
            "http://127.0.0.1:5500",
            "http://localhost:4200",   // Angular
            "http://localhost:8081",   // Local dev port
            "http://localhost:8082"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // ✅ Ignore static resource paths
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                    "/uploads/**",
                    "/images/**",
                    "/css/**",
                    "/js/**",
                    "/favicon.ico"
                );
    }

    // ✅ Main Security Configuration
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Enable CORS and disable CSRF
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())

            // Handle unauthorized exceptions
            .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))

            // Stateless session (JWT-based)
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Authorization rules
            .authorizeHttpRequests(auth -> auth

                // --- Public endpoints ---
                .requestMatchers(
                    "/api/auth/**",
                    "/api/test/**",
                    "/ws/**",
                    "/app/**",
                    "/topic/**"
                ).permitAll()

                // --- Public GET endpoints ---
                .requestMatchers(HttpMethod.GET,
                    "/api/movies/**",
                    "/api/theaters/**",
                    "/api/shows/**",
                    "/api/seats/**"
                ).permitAll()
                
                  .requestMatchers(HttpMethod.GET, "/api/profile/**").hasAnyRole("USER", "ADMIN")
                // --- Booking endpoints ---
                // Create booking (USER or ADMIN)
                .requestMatchers(HttpMethod.POST, "/api/bookings/**").hasAnyRole("USER", "ADMIN")

                // Confirm booking payment (USER or ADMIN)
                .requestMatchers(HttpMethod.PUT, "/api/bookings/**").hasAnyRole("USER", "ADMIN")

                // View bookings (GET by user or admin)
                .requestMatchers(HttpMethod.GET, "/api/bookings/**").hasAnyRole("USER", "ADMIN")

                // --- Admin-only CRUD operations for other APIs ---
                .requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")

                // --- Everything else requires authentication ---
                .anyRequest().authenticated()
            );

        // Add authentication provider and JWT filter
        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
