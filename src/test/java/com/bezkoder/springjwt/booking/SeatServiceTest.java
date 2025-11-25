package com.bezkoder.springjwt.booking;

import com.bezkoder.springjwt.models.Seat;
import com.bezkoder.springjwt.repository.SeatRepository;
import com.bezkoder.springjwt.security.services.SeatService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SeatServiceTest {

    @Mock
    private SeatRepository seatRepository;

    @InjectMocks
    private SeatService seatService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSeatsForShow() {
        List<Seat> seats = List.of(
                new Seat(1L, "A1", "AVAILABLE"),
                new Seat(1L, "A2", "LOCKED")
        );

        when(seatRepository.findByShowId(1L)).thenReturn(seats);

        List<Seat> result = seatService.getSeatsForShow(1L);

        assertEquals(2, result.size());
    }

    @Test
    void testLockSeatSuccess() {
        Seat seat = new Seat(1L, "A1", "AVAILABLE");

        when(seatRepository.findByShowIdAndSeatNumber(1L, "A1"))
                .thenReturn(Optional.of(seat));

        Optional<Seat> result = seatService.lockSeat(1L, "A1", 5L);

        assertTrue(result.isPresent());
        assertEquals("LOCKED", result.get().getStatus());
        assertEquals(5L, result.get().getLockedByUserId());
        assertNotNull(result.get().getLockExpiresAt());
    }

    @Test
    void testLockSeatFailWhenNotAvailable() {
        Seat seat = new Seat(1L, "A1", "BOOKED");

        when(seatRepository.findByShowIdAndSeatNumber(1L, "A1"))
                .thenReturn(Optional.of(seat));

        Optional<Seat> result = seatService.lockSeat(1L, "A1", 5L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testBookSeatSuccess() {
        Seat seat = new Seat(1L, "A1", "LOCKED");

        when(seatRepository.findByShowIdAndSeatNumber(1L, "A1"))
                .thenReturn(Optional.of(seat));

        Optional<Seat> result = seatService.bookSeat(1L, "A1", 5L);

        assertTrue(result.isPresent());
        assertEquals("BOOKED", result.get().getStatus());
    }

    @Test
    void testUnlockSeatSuccess() {
        Seat seat = new Seat(1L, "A1", "LOCKED");

        when(seatRepository.findByShowIdAndSeatNumber(1L, "A1"))
                .thenReturn(Optional.of(seat));

        Optional<Seat> result = seatService.unlockSeat(1L, "A1");

        assertTrue(result.isPresent());
        assertEquals("AVAILABLE", result.get().getStatus());
    }
}