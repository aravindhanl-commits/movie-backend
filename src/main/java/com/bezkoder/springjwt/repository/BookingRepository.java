package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByShowId(Long showId);
    List<Booking> findByUserId(Long userId);
}
