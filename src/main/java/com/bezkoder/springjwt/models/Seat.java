package com.bezkoder.springjwt.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "seats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"show_id", "seat_number"})
})
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "show_id", nullable = false)
    private Long showId;

    @Column(name = "seat_number", nullable = false)
    private String seatNumber; // "A1", "B10"

    @Column(nullable = false)
    private String status; // AVAILABLE, LOCKED, BOOKED

    private Long lockedByUserId; // who locked it (optional)

    private LocalDateTime lockExpiresAt; // when lock expires (nullable)

    @Version
    private Long version; // optimistic locking

    // constructors
    public Seat() {}

    public Seat(Long showId, String seatNumber, String status) {
        this.showId = showId;
        this.seatNumber = seatNumber;
        this.status = status;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getShowId() { return showId; }
    public void setShowId(Long showId) { this.showId = showId; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getLockedByUserId() { return lockedByUserId; }
    public void setLockedByUserId(Long lockedByUserId) { this.lockedByUserId = lockedByUserId; }

    public LocalDateTime getLockExpiresAt() { return lockExpiresAt; }
    public void setLockExpiresAt(LocalDateTime lockExpiresAt) { this.lockExpiresAt = lockExpiresAt; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
