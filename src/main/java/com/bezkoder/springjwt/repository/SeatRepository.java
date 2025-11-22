package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByShowId(Long showId);
    Optional<Seat> findByShowIdAndSeatNumber(Long showId, String seatNumber);
}