package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Seat;
import com.bezkoder.springjwt.models.Show;
import com.bezkoder.springjwt.models.Theater;
import com.bezkoder.springjwt.models.SeatingLayout;
import com.bezkoder.springjwt.repository.SeatRepository;
import com.bezkoder.springjwt.repository.ShowRepository;
import com.bezkoder.springjwt.repository.TheaterRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final TheaterRepository theaterRepository;

    public ShowService(ShowRepository showRepository, SeatRepository seatRepository, TheaterRepository theaterRepository) {
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
        this.theaterRepository = theaterRepository;
    }

    public List<Show> getAllShows() {
        return showRepository.findAll();
    }

    public Optional<Show> getShowById(Long id) {
        return showRepository.findById(id);
    }

    public List<Show> getShowsByMovieId(Long movieId) {
        return showRepository.findByMovieId(movieId);
    }

    public List<Show> getShowsByTheaterId(Long theaterId) {
        return showRepository.findByTheaterId(theaterId);
    }

    public Show saveShow(Show show) {
        Show savedShow = showRepository.save(show);

        // ✅ Get theater info for seat layout
        Optional<Theater> theaterOpt = theaterRepository.findById(savedShow.getTheaterId());
        if (theaterOpt.isPresent()) {
            Theater theater = theaterOpt.get();
            SeatingLayout layout = theater.getSeatingLayout();

            if (layout != null) {
                int totalRows = layout.getRows();
                int seatsPerRow = layout.getSeatsPerRow();
                generateSeatsForShow(savedShow.getId(), totalRows, seatsPerRow);
            } else {
                System.out.println("⚠️ No seating layout found for theater: " + theater.getName());
            }
        } else {
            System.out.println("⚠️ Theater not found for showId=" + savedShow.getId());
        }

        return savedShow;
    }

    public void deleteShow(Long id) {
        showRepository.deleteById(id);
    }

    /**
     * ✅ Generate seats for the show based on the theater’s seating layout
     */
    private void generateSeatsForShow(Long showId, int totalRows, int seatsPerRow) {
        List<Seat> existingSeats = seatRepository.findByShowId(showId);
        if (!existingSeats.isEmpty()) {
            return; // Seats already exist for this show
        }

        List<Seat> newSeats = new ArrayList<>();
        for (char row = 'A'; row < 'A' + totalRows; row++) {
            for (int num = 1; num <= seatsPerRow; num++) {
                String seatNumber = row + String.valueOf(num);
                Seat seat = new Seat();
                seat.setShowId(showId);
                seat.setSeatNumber(seatNumber);
                seat.setStatus("AVAILABLE");
                newSeats.add(seat);
            }
        }

        seatRepository.saveAll(newSeats);
        System.out.println("✅ Generated " + newSeats.size() + " seats for show ID: " + showId);
    }
}
