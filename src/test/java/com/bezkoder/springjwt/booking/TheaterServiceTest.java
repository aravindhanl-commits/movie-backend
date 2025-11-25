package com.bezkoder.springjwt.booking;

import com.bezkoder.springjwt.models.Theater;
import com.bezkoder.springjwt.repository.TheaterRepository;
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

class TheaterServiceTest {

    @Mock
    private TheaterRepository theaterRepository;

    @InjectMocks
    private TheaterService theaterService;

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

        when(theaterRepository.findAll()).thenReturn(Arrays.asList(t1, t2));

        var theaters = theaterService.getAllTheaters();

        assertEquals(2, theaters.size());
        verify(theaterRepository, times(1)).findAll();
    }

    @Test
    void testGetTheaterById() {
        Theater theater = new Theater();
        theater.setId(1L);
        theater.setName("PVR");

        when(theaterRepository.findById(1L)).thenReturn(Optional.of(theater));

        Optional<Theater> result = theaterService.getTheaterById(1L);

        assertTrue(result.isPresent());
        assertEquals("PVR", result.get().getName());
    }

    @Test
    void testSaveTheater() {
        Theater theater = new Theater();
        theater.setName("PVR");

        when(theaterRepository.save(theater)).thenReturn(theater);

        Theater saved = theaterService.saveTheater(theater);

        assertNotNull(saved);
        assertEquals("PVR", saved.getName());
        verify(theaterRepository, times(1)).save(theater);
    }

    @Test
    void testDeleteTheater() {
        theaterService.deleteTheater(5L);

        verify(theaterRepository, times(1)).deleteById(5L);
    }
}
