package com.bezkoder.springjwt.booking;

import com.bezkoder.springjwt.controllers.ShowController;
import com.bezkoder.springjwt.models.Show;
import com.bezkoder.springjwt.security.services.ShowService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShowController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ShowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ShowService showService;

    @Test
    void testGetAllShows() throws Exception {
        Show show = new Show();
        show.setId(1L);
        show.setMovieId(10L);

        when(showService.getAllShows()).thenReturn(List.of(show));

        mockMvc.perform(get("/api/shows"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].movieId").value(10L));
    }

    @Test
    void testGetShowById() throws Exception {
        Show show = new Show();
        show.setId(2L);

        when(showService.getShowById(2L)).thenReturn(Optional.of(show));

        mockMvc.perform(get("/api/shows/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    void testCreateShow() throws Exception {
        Show saved = new Show();
        saved.setId(3L);
        saved.setMovieId(11L);

        when(showService.saveShow(any(Show.class))).thenReturn(saved);

        String json = """
                {
                  "movieId": 11,
                  "theaterId": 6,
                  "showDate": "2025-01-01",
                  "showTime": "2:00 PM",
                  "price": 150,
                  "availableSeats": 60,
                  "screenNumber": 1
                }
                """;

        mockMvc.perform(post("/api/shows/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.movieId").value(11L));
    }

    @Test
    void testDeleteShow() throws Exception {
        mockMvc.perform(delete("/api/shows/4"))
                .andExpect(status().isOk());
    }
}
