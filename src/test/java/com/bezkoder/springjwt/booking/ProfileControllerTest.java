package com.bezkoder.springjwt.booking;

import com.bezkoder.springjwt.controllers.ProfileController;
import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean private UserRepository userRepository;
    @MockBean private BookingRepository bookingRepository;
    @MockBean private MovieRepository movieRepository;
    @MockBean private TheaterRepository theaterRepository;
    @MockBean private ShowRepository showRepository;

    @Test
    void testGetProfileByEmailSuccess() throws Exception {

        User user = new User("john", "john@gmail.com", "pass");
        user.setId(1L);

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));

        Booking booking = new Booking();
        booking.setBookingId(String.valueOf(10L));
        booking.setUserId(1L);
        booking.setMovieId(5L);
        booking.setTheaterId(3L);
        booking.setShowId(8L);
        booking.setSeatNumbers("A1,A2");
        booking.setTotalAmount(200.0);
        booking.setPaymentStatus("PAID");
        booking.setBookingTime(LocalDateTime.of(2025, 1, 1, 10, 30));

        when(bookingRepository.findByUserId(1L))
                .thenReturn(List.of(booking));

        Movie movie = new Movie();
        movie.setTitle("Avatar 2");
        movie.setDuration(120);
        movie.setGenre("");
        movie.setDescription("");
        movie.setDirector("");
        movie.setLanguage("");

        when(movieRepository.findById(5L))
                .thenReturn(Optional.of(movie));

        Theater theater = new Theater();
        theater.setName("PVR Cinema");

        when(theaterRepository.findById(3L))
                .thenReturn(Optional.of(theater));

        Show show = new Show();
        show.setShowTime("2025-01-01 10:30");

        when(showRepository.findById(8L))
                .thenReturn(Optional.of(show));

        mockMvc.perform(get("/api/profile/john@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@gmail.com"))
                .andExpect(jsonPath("$.bookings[0].movieTitle").value("Avatar 2"))
                .andExpect(jsonPath("$.bookings[0].theaterName").value("PVR Cinema"))
                .andExpect(jsonPath("$.bookings[0].showTime").value("2025-01-01 10:30"))
                .andExpect(jsonPath("$.bookings[0].totalAmount").value(200.0));
    }

    @Test
    void testGetProfileUserNotFound() throws Exception {

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/profile/unknown@gmail.com"))
                .andExpect(status().isInternalServerError());
    }

    // ✅ MUST BE INSIDE THE TEST CLASS
    @TestConfiguration
    static class TestConfig {

        @RestControllerAdvice
        static class TestExceptionHandler {

            @ExceptionHandler(RuntimeException.class)
            public ResponseEntity<String> handleRuntime(RuntimeException ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
            }
        }
    }
}
