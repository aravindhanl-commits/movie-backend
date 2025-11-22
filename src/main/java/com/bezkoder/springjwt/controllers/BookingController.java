package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Booking;
import com.bezkoder.springjwt.models.BookingRequest;
import com.bezkoder.springjwt.repository.BookingRepository;
import com.bezkoder.springjwt.security.services.BookingService;
import com.bezkoder.springjwt.security.services.EmailService;
import com.bezkoder.springjwt.security.services.SeatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private SeatService seatService;

    @Autowired
    private EmailService emailService; // ✅ Added email service

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        Booking booking = new Booking();
        booking.setUserId(request.getUserId());
        booking.setMovieId(request.getMovieId());
        booking.setTheaterId(request.getTheaterId());
        booking.setShowId(request.getShowId());
        booking.setSeatNumbers(request.getSeatNumbers());
        booking.setTotalAmount(request.getTotalAmount());
        booking.setPaymentStatus("PENDING");
        booking.setUserEmail(request.getUserEmail()); // ✅ store email

        Booking savedBooking = bookingService.createBooking(booking);

       

        return ResponseEntity.ok(savedBooking);
    }

    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> confirmPayment(@PathVariable Long id) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isEmpty()) return ResponseEntity.notFound().build();

        Booking booking = bookingOpt.get();
        booking.setPaymentStatus("PAID");
        bookingRepository.save(booking);

        // ✅ Mark seats as booked permanently
        if (booking.getSeatNumbers() != null && !booking.getSeatNumbers().isEmpty()) {
            String[] seats = booking.getSeatNumbers().split(",");
            for (String seatNum : seats) {
                seatService.bookSeat(booking.getShowId(), seatNum.trim(), booking.getUserId());
            }
        }

        // ✅ Send confirmation email
        if (booking.getUserEmail() != null && !booking.getUserEmail().isEmpty()) {
            emailService.sendBookingConfirmation(
                booking.getUserEmail(),
                booking.getBookingId(),
                booking.getSeatNumbers(),
                booking.getTotalAmount()
            );
        }

        return ResponseEntity.ok(booking);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getBookingById(@PathVariable Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        return booking.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public List<Booking> getBookingsByUser(@PathVariable Long userId) {
        return bookingRepository.findAll()
                .stream()
                .filter(b -> b.getUserId().equals(userId))
                .toList();
    }
}
