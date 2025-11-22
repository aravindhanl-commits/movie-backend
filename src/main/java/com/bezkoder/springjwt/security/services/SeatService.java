package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Seat;
import com.bezkoder.springjwt.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    public List<Seat> getSeatsForShow(Long showId) {
        return seatRepository.findByShowId(showId);
    }

    public Optional<Seat> lockSeat(Long showId, String seatNumber, Long userId) {
        Optional<Seat> seatOpt = seatRepository.findByShowIdAndSeatNumber(showId, seatNumber);
        if (seatOpt.isPresent()) {
            Seat seat = seatOpt.get();
            if (!seat.getStatus().equals("AVAILABLE")) return Optional.empty();

            seat.setStatus("LOCKED");
            seat.setLockedByUserId(userId);
            seat.setLockExpiresAt(LocalDateTime.now().plusMinutes(5));
            seatRepository.save(seat);
            return Optional.of(seat);
        }
        return Optional.empty();
    }

    // ✅ Finalize seat as booked
    public Optional<Seat> bookSeat(Long showId, String seatNumber, Long userId) {
        Optional<Seat> seatOpt = seatRepository.findByShowIdAndSeatNumber(showId, seatNumber);
        if (seatOpt.isPresent()) {
            Seat seat = seatOpt.get();
            seat.setStatus("BOOKED");
            seat.setLockedByUserId(userId);
            seat.setLockExpiresAt(null);
            seatRepository.save(seat);
            return Optional.of(seat);
        }
        return Optional.empty();
    }

    public Optional<Seat> unlockSeat(Long showId, String seatNumber) {
        Optional<Seat> seatOpt = seatRepository.findByShowIdAndSeatNumber(showId, seatNumber);
        if (seatOpt.isPresent()) {
            Seat seat = seatOpt.get();
            seat.setStatus("AVAILABLE");
            seat.setLockedByUserId(null);
            seat.setLockExpiresAt(null);
            seatRepository.save(seat);
            return Optional.of(seat);
        }
        return Optional.empty();
    }
}
