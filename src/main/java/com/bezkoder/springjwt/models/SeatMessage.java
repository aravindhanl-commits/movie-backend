package com.bezkoder.springjwt.models;
import java.time.LocalDateTime;

public class SeatMessage {
    private Long showId;
    private String seatNumber;
    private String status; // LOCKED, AVAILABLE, BOOKED
    private Long userId; // optional - who locked or booked
    private LocalDateTime lockExpiresAt; // optional

    public SeatMessage() {}

    // getters & setters
    public Long getShowId() { return showId; }
    public void setShowId(Long showId) { this.showId = showId; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public LocalDateTime getLockExpiresAt() { return lockExpiresAt; }
    public void setLockExpiresAt(LocalDateTime lockExpiresAt) { this.lockExpiresAt = lockExpiresAt; }
}
