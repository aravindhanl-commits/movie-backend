package com.bezkoder.springjwt.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String bookingId; // e.g., BKZ3L20P5XC

    private Long userId;
    private Long movieId;
    private Long theaterId;
    private Long showId;

    @Column(length = 1000)
    private String seatNumbers; // comma separated "A1,A2"

    private double totalAmount;

    private LocalDateTime bookingTime;

    @Column(nullable = false)
    private String paymentStatus = "PENDING";

    private String userEmail; // ✅ added for email sending

    // constructors
    public Booking() {}

    public Booking(String bookingId, Long userId, Long movieId, Long theaterId, Long showId,
                   String seatNumbers, double totalAmount, LocalDateTime bookingTime, String userEmail) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.movieId = movieId;
        this.theaterId = theaterId;
        this.showId = showId;
        this.seatNumbers = seatNumbers;
        this.totalAmount = totalAmount;
        this.bookingTime = bookingTime;
        this.paymentStatus = "PENDING";
        this.userEmail = userEmail;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }

    public Long getTheaterId() { return theaterId; }
    public void setTheaterId(Long theaterId) { this.theaterId = theaterId; }

    public Long getShowId() { return showId; }
    public void setShowId(Long showId) { this.showId = showId; }

    public String getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(String seatNumbers) { this.seatNumbers = seatNumbers; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getBookingTime() { return bookingTime; }
    public void setBookingTime(LocalDateTime bookingTime) { this.bookingTime = bookingTime; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}
