package com.bezkoder.springjwt.booking;

import com.bezkoder.springjwt.models.Seat;
import com.bezkoder.springjwt.models.SeatingLayout;
import com.bezkoder.springjwt.models.Show;
import com.bezkoder.springjwt.models.Theater;
import com.bezkoder.springjwt.repository.SeatRepository;
import com.bezkoder.springjwt.repository.ShowRepository;
import com.bezkoder.springjwt.repository.TheaterRepository;
import com.bezkoder.springjwt.security.services.ShowService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ShowServiceTest {

    private ShowRepository showRepository;
    private SeatRepository seatRepository;
    private TheaterRepository theaterRepository;

    private ShowService showService;

    @BeforeEach
    void setup() {
        showRepository = mock(ShowRepository.class);
        seatRepository = mock(SeatRepository.class);
        theaterRepository = mock(TheaterRepository.class);

        showService = new ShowService(showRepository, seatRepository, theaterRepository);
    }

    @Test
    void testSaveShowAndGenerateSeats() {
        Show show = new Show();
        show.setId(100L);
        show.setTheaterId(5L);

        when(showRepository.save(any())).thenReturn(show);

        // mock seating layout
        SeatingLayout layout = new SeatingLayout();
        layout.setRows(2);
        layout.setSeatsPerRow(3);

        Theater theater = new Theater();
        theater.setSeatingLayout(layout);

        when(theaterRepository.findById(5L)).thenReturn(Optional.of(theater));

        when(seatRepository.findByShowId(100L)).thenReturn(new ArrayList<>());

        showService.saveShow(show);

        verify(seatRepository).saveAll(argThat(seats -> {
            List<Seat> list = new ArrayList<>();
            seats.forEach(list::add);
            return list.size() == 6;  // 2×3
        }));
    }

    @Test
    void testGetShowById() {
        Show show = new Show();
        show.setId(10L);

        when(showRepository.findById(10L)).thenReturn(Optional.of(show));

        Optional<Show> result = showService.getShowById(10L);

        assertTrue(result.isPresent());
        assertEquals(10L, result.get().getId());
    }

    @Test
    void testDeleteShow() {
        doNothing().when(showRepository).deleteById(7L);

        showService.deleteShow(7L);

        verify(showRepository, times(1)).deleteById(7L);
    }
}
