package com.bezkoder.springjwt.booking;

import com.bezkoder.springjwt.models.Movie;
import com.bezkoder.springjwt.models.Show;
import com.bezkoder.springjwt.models.Booking;
import com.bezkoder.springjwt.repository.MovieRepository;
import com.bezkoder.springjwt.repository.ShowRepository;
import com.bezkoder.springjwt.repository.BookingRepository;
import com.bezkoder.springjwt.security.services.MovieService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MovieServiceTest {

    private MovieRepository movieRepository;
    private ShowRepository showRepository;
    private BookingRepository bookingRepository;
    private MovieService movieService;
    private Path tempDir;

    @BeforeEach
    void setup() throws Exception {
        movieRepository = mock(MovieRepository.class);
        showRepository = mock(ShowRepository.class);
        bookingRepository = mock(BookingRepository.class);

        tempDir = Files.createTempDirectory("movies-test");

        movieService = new MovieService(movieRepository, showRepository, bookingRepository, tempDir.toString());
    }

    // ---------------------- CREATE MOVIE ------------------------------
    @Test
    void testSaveMovieWithFile() throws Exception {

        MockMultipartFile poster = new MockMultipartFile(
                "posterFile", "poster.jpg", "image/jpeg", "test image".getBytes()
        );

        Movie savedMovie = new Movie();
        savedMovie.setId(1L);
        savedMovie.setTitle("Test Movie");

        when(movieRepository.save(any(Movie.class))).thenReturn(savedMovie);

        Movie result = movieService.saveMovieWithFile(
                "Test Movie", "Action", 120, 4.5,
                "desc", "director", "English",
                "2024-01-01", List.of("Actor A", "Actor B"), poster
        );

        assertNotNull(result);
        assertEquals("Test Movie", result.getTitle());
    }

    // ---------------------- UPDATE MOVIE ------------------------------
    @Test
    void testUpdateMovieWithFile() throws Exception {

        Movie existing = new Movie();
        existing.setId(5L);
        existing.setTitle("Old");
        existing.setPosterUrl("old.jpg");

        when(movieRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(movieRepository.save(any(Movie.class))).thenReturn(existing);

        MockMultipartFile poster = new MockMultipartFile(
                "posterFile", "new.jpg", "image/jpeg", "newimg".getBytes()
        );

        Movie updated = movieService.updateMovieWithFile(
                5L, "New", "Drama", 100, 3.0,
                "Desc", "Dir", "EN",
                "2024-02-02", List.of("A", "B"), poster
        );

        assertEquals("New", updated.getTitle());
    }

    // ---------------------- DELETE MOVIE ------------------------------
    @Test
    void testDeleteMovie() {

        Movie movie = new Movie();
        movie.setId(99L);
        movie.setPosterUrl("poster.jpg");

        when(movieRepository.findById(99L)).thenReturn(Optional.of(movie));

        Show s1 = new Show(); s1.setId(1L);
        Show s2 = new Show(); s2.setId(2L);

        when(showRepository.findByMovieId(99L)).thenReturn(List.of(s1, s2));
        when(bookingRepository.findByShowId(anyLong()))
                .thenReturn(List.of(new Booking(), new Booking()));

        assertDoesNotThrow(() -> movieService.deleteMovie(99L));

        verify(bookingRepository, times(2)).deleteAll(anyList());
        verify(showRepository, times(1)).deleteAll(anyList());
        verify(movieRepository, times(1)).deleteById(99L);
    }

    // ---------------------- GET MOVIE ------------------------------
    @Test
    void testGetMovieById() {
        Movie m = new Movie();
        m.setId(20L);

        when(movieRepository.findById(20L)).thenReturn(Optional.of(m));

        Optional<Movie> result = movieService.getMovieById(20L);

        assertTrue(result.isPresent());
        assertEquals(20L, result.get().getId());
    }

    // ---------------------- GET ALL MOVIES ------------------------------
    @Test
    void testGetAllMovies() {
        when(movieRepository.findAll()).thenReturn(List.of(new Movie(), new Movie()));

        List<Movie> list = movieService.getAllMovies();

        assertEquals(2, list.size());
    }
}
