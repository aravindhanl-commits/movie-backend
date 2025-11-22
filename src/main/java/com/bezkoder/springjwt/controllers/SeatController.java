package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Seat;
import com.bezkoder.springjwt.security.services.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/seats")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // ✅ Get all seats for show
    @GetMapping("/show/{showId}")
    public List<Seat> getSeatsByShow(@PathVariable Long showId) {
        return seatService.getSeatsForShow(showId);
    }

    // ✅ Lock a seat
    @PostMapping("/lock")
    public ResponseEntity<?> lockSeat(@RequestBody Map<String, Object> body) {
        Long showId = Long.valueOf(body.get("showId").toString());
        String seatNumber = body.get("seatNumber").toString();
        Long userId = Long.valueOf(body.get("userId").toString());

        Optional<Seat> seat = seatService.lockSeat(showId, seatNumber, userId);
        return seat.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body("Seat not available"));
    }

    // ✅ Book a seat (finalize)
    @PostMapping("/book")
    public ResponseEntity<?> bookSeat(@RequestBody Map<String, Object> body) {
        Long showId = Long.valueOf(body.get("showId").toString());
        String seatNumber = body.get("seatNumber").toString();
        Long userId = Long.valueOf(body.get("userId").toString());

        Optional<Seat> seat = seatService.bookSeat(showId, seatNumber, userId);
        return seat.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body("Seat not found or locked"));
    }

    // ✅ Unlock seat manually
    @PostMapping("/unlock")
    public ResponseEntity<?> unlockSeat(@RequestBody Map<String, Object> body) {
        Long showId = Long.valueOf(body.get("showId").toString());
        String seatNumber = body.get("seatNumber").toString();

        Optional<Seat> seat = seatService.unlockSeat(showId, seatNumber);
        return seat.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body("Seat not found"));
    }

    // ✅ For debugging: send manual broadcast
    @PostMapping("/notify/{showId}")
    public void sendSeatUpdate(@PathVariable Long showId, @RequestBody Map<String, Object> body) {
        messagingTemplate.convertAndSend("/topic/seats/" + showId, body);
    }
}
