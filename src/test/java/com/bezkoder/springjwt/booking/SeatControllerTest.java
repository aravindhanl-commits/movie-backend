package com.bezkoder.springjwt.booking;

import com.bezkoder.springjwt.controllers.SeatController;
import com.bezkoder.springjwt.models.Seat;
import com.bezkoder.springjwt.security.services.SeatService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SeatController.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class SeatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeatService seatService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Test
    void testGetSeatsByShow() throws Exception {
        Seat s1 = new Seat(1L, "A1", "AVAILABLE");
        Seat s2 = new Seat(1L, "A2", "LOCKED");

        when(seatService.getSeatsForShow(1L))
                .thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/seats/show/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].seatNumber").value("A1"))
                .andExpect(jsonPath("$[1].seatNumber").value("A2"));
    }

    @Test
    void testLockSeatSuccess() throws Exception {
        Seat seat = new Seat(1L, "A1", "LOCKED");
        seat.setLockedByUserId(5L);
        seat.setLockExpiresAt(LocalDateTime.now());

        when(seatService.lockSeat(1L, "A1", 5L))
                .thenReturn(Optional.of(seat));

        mockMvc.perform(post("/api/seats/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"showId":1, "seatNumber":"A1", "userId":5}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.seatNumber").value("A1"))
                .andExpect(jsonPath("$.status").value("LOCKED"));
    }

    @Test
    void testLockSeatFail() throws Exception {
        when(seatService.lockSeat(1L, "A1", 5L))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/seats/lock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"showId":1, "seatNumber":"A1", "userId":5}
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testBookSeatSuccess() throws Exception {
        Seat seat = new Seat(1L, "A1", "BOOKED");

        when(seatService.bookSeat(1L, "A1", 5L))
                .thenReturn(Optional.of(seat));

        mockMvc.perform(post("/api/seats/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"showId":1, "seatNumber":"A1", "userId":5}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BOOKED"));
    }

    @Test
    void testBookSeatFail() throws Exception {
        when(seatService.bookSeat(1L, "A1", 5L))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/seats/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"showId":1, "seatNumber":"A1", "userId":5}
                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUnlockSeatSuccess() throws Exception {
        Seat seat = new Seat(1L, "A1", "AVAILABLE");

        when(seatService.unlockSeat(1L, "A1"))
                .thenReturn(Optional.of(seat));

        mockMvc.perform(post("/api/seats/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"showId":1, "seatNumber":"A1"}
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void testUnlockSeatFail() throws Exception {
        when(seatService.unlockSeat(1L, "A1"))
                .thenReturn(Optional.empty());

        mockMvc.perform(post("/api/seats/unlock")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"showId":1, "seatNumber":"A1"}
                """))
                .andExpect(status().isBadRequest());
    }
}