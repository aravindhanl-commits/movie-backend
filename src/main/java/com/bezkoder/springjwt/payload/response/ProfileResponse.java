package com.bezkoder.springjwt.payload.response;

import java.util.List;

public class ProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String memberSince;
    private List<BookingInfo> bookings;

    // Nested class for simplified booking details
    public static class BookingInfo {
        private String bookingId;
        private String movieTitle;
        private String theaterName;
        private String showTime;
        private String seatNumbers;
        private double totalAmount;
        private String paymentStatus;
        private String bookingDate;

        public BookingInfo() {}

        public BookingInfo(String bookingId, String movieTitle, String theaterName,
                           String showTime, String seatNumbers, double totalAmount,
                           String paymentStatus, String bookingDate) {
            this.bookingId = bookingId;
            this.movieTitle = movieTitle;
            this.theaterName = theaterName;
            this.showTime = showTime;
            this.seatNumbers = seatNumbers;
            this.totalAmount = totalAmount;
            this.paymentStatus = paymentStatus;
            this.bookingDate = bookingDate;
        }

        // Getters & Setters
        public String getBookingId() { return bookingId; }
        public void setBookingId(String bookingId) { this.bookingId = bookingId; }

        public String getMovieTitle() { return movieTitle; }
        public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

        public String getTheaterName() { return theaterName; }
        public void setTheaterName(String theaterName) { this.theaterName = theaterName; }

        public String getShowTime() { return showTime; }
        public void setShowTime(String showTime) { this.showTime = showTime; }

        public String getSeatNumbers() { return seatNumbers; }
        public void setSeatNumbers(String seatNumbers) { this.seatNumbers = seatNumbers; }

        public double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

        public String getPaymentStatus() { return paymentStatus; }
        public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

        public String getBookingDate() { return bookingDate; }
        public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }
    }

    // Main getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getMemberSince() { return memberSince; }
    public void setMemberSince(String memberSince) { this.memberSince = memberSince; }

    public List<BookingInfo> getBookings() { return bookings; }
    public void setBookings(List<BookingInfo> bookings) { this.bookings = bookings; }
}
