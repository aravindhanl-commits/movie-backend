package com.bezkoder.springjwt.models;

public class BookingRequest {
    private Long userId;
    private Long movieId;
    private Long theaterId;
    private Long showId;
    private String seatNumbers; // "A1,A2"
    private double totalAmount;
    private String userEmail; // ✅ added for email sending

    // getters and setters
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

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
}
