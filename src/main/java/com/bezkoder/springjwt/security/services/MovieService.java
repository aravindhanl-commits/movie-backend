package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Movie;
import com.bezkoder.springjwt.models.Show;
import com.bezkoder.springjwt.models.Booking;
import com.bezkoder.springjwt.repository.MovieRepository;
import com.bezkoder.springjwt.repository.ShowRepository;
import com.bezkoder.springjwt.repository.BookingRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;
    private final BookingRepository bookingRepository;
    private final Path uploadDir;

    public MovieService(MovieRepository movieRepository,
                        ShowRepository showRepository,
                        BookingRepository bookingRepository,
                        @Value("${file.upload-dir:uploads/movies}") String uploadDirStr) {
        this.movieRepository = movieRepository;
        this.showRepository = showRepository;
        this.bookingRepository = bookingRepository;
        this.uploadDir = Paths.get(uploadDirStr).toAbsolutePath().normalize();
    }

    @PostConstruct
    private void init() {
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public Optional<Movie> getMovieById(Long id) {
        return movieRepository.findById(id);
    }

    public Movie saveMovieWithFile(String title, String genre, int duration, double rating,
                                   String description, String director, String language,
                                   String releaseDateStr, List<String> cast, MultipartFile posterFile) {

        String posterFileName = null;
        if (posterFile != null && !posterFile.isEmpty()) {
            posterFileName = savePosterFile(posterFile);
        }

        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setDuration(duration);
        movie.setRating(rating);
        movie.setDescription(description);
        movie.setDirector(director);
        movie.setLanguage(language);

        if (releaseDateStr != null && !releaseDateStr.isEmpty()) {
            movie.setReleaseDate(LocalDate.parse(releaseDateStr));
        }

        movie.setCast(cast);
        movie.setPosterUrl(posterFileName);

        return movieRepository.save(movie);
    }

    public Movie updateMovieWithFile(Long id, String title, String genre, int duration, double rating,
                                     String description, String director, String language,
                                     String releaseDateStr, List<String> cast, MultipartFile posterFile) {
        return movieRepository.findById(id)
                .map(movie -> {
                    movie.setTitle(title);
                    movie.setGenre(genre);
                    movie.setDuration(duration);
                    movie.setRating(rating);
                    movie.setDescription(description);
                    movie.setDirector(director);
                    movie.setLanguage(language);

                    if (releaseDateStr != null && !releaseDateStr.isEmpty()) {
                        movie.setReleaseDate(LocalDate.parse(releaseDateStr));
                    }

                    movie.setCast(cast);

                    if (posterFile != null && !posterFile.isEmpty()) {
                        if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
                            deleteFileIfExists(movie.getPosterUrl());
                        }
                        String posterFileName = savePosterFile(posterFile);
                        movie.setPosterUrl(posterFileName);
                    }

                    return movieRepository.save(movie);
                })
                .orElseThrow(() -> new RuntimeException("Movie not found"));
    }

    /**
     * Delete a movie along with all related shows and bookings.
     */
    @Transactional
    public void deleteMovie(Long id) {
        movieRepository.findById(id).ifPresent(movie -> {

            // 1️⃣ Delete related bookings for all shows of this movie
            List<Show> relatedShows = showRepository.findByMovieId(id);
            for (Show show : relatedShows) {
                List<Booking> relatedBookings = bookingRepository.findByShowId(show.getId());
                bookingRepository.deleteAll(relatedBookings);
            }

            // 2️⃣ Delete related shows
            showRepository.deleteAll(relatedShows);

            // 3️⃣ Delete movie poster if exists
            if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
                deleteFileIfExists(movie.getPosterUrl());
            }

            // 4️⃣ Finally delete the movie
            movieRepository.deleteById(id);
        });
    }

    // Helper: save image file
    private String savePosterFile(MultipartFile posterFile) {
        String originalFileName = Paths.get(posterFile.getOriginalFilename()).getFileName().toString();
        String fileName = System.currentTimeMillis() + "_" + originalFileName.replaceAll("\\s+", "_");
        Path target = uploadDir.resolve(fileName);

        try {
            Files.copy(posterFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save poster image", e);
        }

        return fileName;
    }

    private void deleteFileIfExists(String filename) {
        try {
            Path filePath = uploadDir.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + filename + " - " + e.getMessage());
        }
    }

    public Path getUploadDir() {
        return uploadDir;
    }
}
