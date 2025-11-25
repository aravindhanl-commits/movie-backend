package com.bezkoder.springjwt.booking;

import com.bezkoder.springjwt.SpringBootSecurityJwtApplication;
import com.bezkoder.springjwt.controllers.MovieController;
import com.bezkoder.springjwt.models.Movie;
import com.bezkoder.springjwt.security.services.MovieService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SpringBootSecurityJwtApplication.class)
@AutoConfigureMockMvc
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    // ----------------------- GET ALL MOVIES -----------------------------
    @Test
    void testGetAllMovies() throws Exception {
        Movie m1 = new Movie();
        m1.setId(1L); m1.setTitle("Movie A");

        Movie m2 = new Movie();
        m2.setId(2L); m2.setTitle("Movie B");

        when(movieService.getAllMovies()).thenReturn(List.of(m1, m2));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Movie A"))
                .andExpect(jsonPath("$[1].title").value("Movie B"));
    }

    // ----------------------- GET MOVIE BY ID ---------------------------
    @Test
    void testGetMovieById() throws Exception {
        Movie movie = new Movie();
        movie.setId(10L);
        movie.setTitle("Inception");

        when(movieService.getMovieById(10L)).thenReturn(Optional.of(movie));

        mockMvc.perform(get("/api/movies/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Inception"));
    }

    // ----------------------- CREATE MOVIE (ADMIN) ----------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testCreateMovie() throws Exception {

        MockMultipartFile posterFile = new MockMultipartFile(
                "posterFile",
                "poster.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy image".getBytes()
        );

        Movie saved = new Movie();
        saved.setId(100L);
        saved.setTitle("New Movie");
        saved.setPosterUrl("12345_poster.jpg");

        when(movieService.saveMovieWithFile(
                anyString(), anyString(), anyInt(), anyDouble(),
                anyString(), anyString(), anyString(),
                anyString(), anyList(), any()
        )).thenReturn(saved);

        mockMvc.perform(multipart("/api/movies/admin")
                        .file(posterFile)
                        .param("title", "New Movie")
                        .param("genre", "Action")
                        .param("duration", "120")
                        .param("rating", "4.5")
                        .param("description", "Test movie")
                        .param("director", "John")
                        .param("language", "English")
                        .param("releaseDate", "2024-01-01")
                        .param("cast", "Actor A", "Actor B"))
                .andExpect(status().isOk());
               // .andExpect(jsonPath("$.title").value("New Movie"));
    }

    // ----------------------- UPDATE MOVIE (ADMIN) ----------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateMovie() throws Exception {

        MockMultipartFile posterFile = new MockMultipartFile(
                "posterFile",
                "newposter.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image".getBytes()
        );

        Movie updated = new Movie();
        updated.setId(50L);
        updated.setTitle("Updated Movie");

        when(movieService.updateMovieWithFile(
        eq(50L),
        anyString(), anyString(), anyInt(), anyDouble(),
        anyString(), anyString(), anyString(),
        anyString(),
        ArgumentMatchers.<String>anyList(),
        any()
)).thenReturn(updated);


        mockMvc.perform(multipart("/api/movies/admin/50")
                        .file(posterFile)
                        .with(request -> { request.setMethod("PUT"); return request; }) // Required for PUT + multipart
                        .param("title", "Updated Movie")
                        .param("genre", "Drama")
                        .param("duration", "150")
                        .param("rating", "3.8")
                        .param("description", "Test Update")
                        .param("director", "Director X")
                        .param("language", "Hindi")
                        .param("releaseDate", "2024-05-01"))
                .andExpect(status().isOk());
               // .andExpect(jsonPath("$.title").value("Updated Movie"));
    }

    // ----------------------- DELETE MOVIE ------------------------------
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteMovie() throws Exception {
        doNothing().when(movieService).deleteMovie(33L);

        mockMvc.perform(delete("/api/movies/33"))
                .andExpect(status().isOk());
    }
}
