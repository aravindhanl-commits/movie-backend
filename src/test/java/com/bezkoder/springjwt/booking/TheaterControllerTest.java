package com.bezkoder.springjwt.booking;

import com.bezkoder.springjwt.controllers.TheaterController;
import com.bezkoder.springjwt.models.Theater;
import com.bezkoder.springjwt.security.services.TheaterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TheaterControllerTest {

    @Mock
    private TheaterService theaterService;

    @InjectMocks
    private TheaterController theaterController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTheaters() {
        Theater t1 = new Theater();
        t1.setName("PVR");

        Theater t2 = new Theater();
        t2.setName("INOX");

        when(theaterService.getAllTheaters()).thenReturn(Arrays.asList(t1, t2));

        var response = theaterController.getAllTheaters();

        assertEquals(2, response.size());
        verify(theaterService, times(1)).getAllTheaters();
    }

    @Test
    void testGetTheaterById() {
        Theater theater = new Theater();
        theater.setId(1L);
        theater.setName("PVR");

        when(theaterService.getTheaterById(1L)).thenReturn(Optional.of(theater));

        Optional<Theater> result = theaterController.getTheaterById(1L);

        assertTrue(result.isPresent());
        assertEquals("PVR", result.get().getName());
    }

    @Test
    void testCreateTheater() {
        Theater theater = new Theater();
        theater.setName("PVR");

        when(theaterService.saveTheater(theater)).thenReturn(theater);

Theater result = theaterController.createTheater(theater);

assertEquals("PVR", result.getName());
verify(theaterService, times(1)).saveTheater(theater);

    }

    @Test
    void testDeleteTheater() {
        theaterController.deleteTheater(10L);
        verify(theaterService, times(1)).deleteTheater(10L);
    }
}
