package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Booking;
import com.bezkoder.springjwt.repository.BookingRepository;
import com.bezkoder.springjwt.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Create booking and mark seats as BOOKED in a transaction.
     */
    @Transactional
    public Booking createBooking(Booking booking) {
        // assign booking id and time
        booking.setBookingId(generateBookingId());
        booking.setBookingTime(LocalDateTime.now());
        Booking savedBooking = bookingRepository.save(booking);

        // mark seats booked
        String[] seats = booking.getSeatNumbers().split(",");
        Long showId = booking.getShowId();
        Long userId = booking.getUserId();

        for (String s : seats) {
            String seatNum = s.trim();
            seatRepository.findByShowIdAndSeatNumber(showId, seatNum).ifPresent(seat -> {
                seat.setStatus("BOOKED");
                seat.setLockedByUserId(userId);
                seat.setLockExpiresAt(null);
                seatRepository.save(seat);
                // broadcast seat update
                messagingTemplate.convertAndSend("/topic/show/" + showId, seat);
            });
        }

        return savedBooking;
    }

    private String generateBookingId() {
        // short feasible booking id — you can replace with custom logic
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}
