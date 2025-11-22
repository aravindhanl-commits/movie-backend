package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.*;
import com.bezkoder.springjwt.payload.response.ProfileResponse;
import com.bezkoder.springjwt.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ShowRepository showRepository;

    @GetMapping("/{email}")
    public ProfileResponse getProfileByEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Booking> bookings = bookingRepository.findByUserId(user.getId());
        List<ProfileResponse.BookingInfo> bookingInfos = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Booking booking : bookings) {
            String movieTitle = movieRepository.findById(booking.getMovieId())
                    .map(Movie::getTitle)
                    .orElse("Unknown Movie");

            String theaterName = theaterRepository.findById(booking.getTheaterId())
                    .map(Theater::getName)
                    .orElse("Unknown Theater");

            String showTime = showRepository.findById(booking.getShowId())
                    .map(Show::getShowTime)
                    .orElse("Unknown Time");

            bookingInfos.add(new ProfileResponse.BookingInfo(
                    booking.getBookingId(),
                    movieTitle,
                    theaterName,
                    showTime,
                    booking.getSeatNumbers(),
                    booking.getTotalAmount(),
                    booking.getPaymentStatus(),
                    booking.getBookingTime().format(formatter)
            ));
        }

        ProfileResponse response = new ProfileResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setMemberSince("2025-01-01"); // You can replace this with user.getCreatedAt() if you add it
        response.setBookings(bookingInfos);

        return response;
    }
}
