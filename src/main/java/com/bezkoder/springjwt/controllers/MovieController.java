package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Movie;
import com.bezkoder.springjwt.security.services.MovieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "http://localhost:8081")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("/{id}")
    public Movie getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    // Create movie with image upload
    @PostMapping("/admin")
    public Movie createMovie(
            @RequestParam String title,
            @RequestParam String genre,
            @RequestParam int duration,
            @RequestParam double rating,
            @RequestParam(required = false, defaultValue = "") String description,
            @RequestParam(required = false, defaultValue = "") String director,
            @RequestParam(required = false, defaultValue = "") String language,
            @RequestParam(required = false) String releaseDate, // as yyyy-MM-dd
            @RequestParam(required = false) List<String> cast,
            @RequestParam(required = false) MultipartFile posterFile
    ) {
        return movieService.saveMovieWithFile(title, genre, duration, rating, description,
                director, language, releaseDate, cast, posterFile);
    }

    // Optional: update with new image
    @PutMapping("/admin/{id}")
    public Movie updateMovie(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String genre,
            @RequestParam int duration,
            @RequestParam double rating,
            @RequestParam(required = false, defaultValue = "") String description,
            @RequestParam(required = false, defaultValue = "") String director,
            @RequestParam(required = false, defaultValue = "") String language,
            @RequestParam(required = false) String releaseDate,
            @RequestParam(required = false) List<String> cast,
            @RequestParam(required = false) MultipartFile posterFile
    ) {
        return movieService.updateMovieWithFile(id, title, genre, duration, rating,
                description, director, language, releaseDate, cast, posterFile);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok().build();
    }
}
