package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Theater;
import com.bezkoder.springjwt.security.services.TheaterService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/theaters")
@CrossOrigin(origins = "http://localhost:8081") // same as your movie controller
public class TheaterController {

    private final TheaterService theaterService;

    public TheaterController(TheaterService theaterService) {
        this.theaterService = theaterService;
    }

    @GetMapping
    public List<Theater> getAllTheaters() {
        return theaterService.getAllTheaters();
    }

    @GetMapping("/{id}")
    public Optional<Theater> getTheaterById(@PathVariable Long id) {
        return theaterService.getTheaterById(id);
    }

    @PostMapping("/admin")
    public Theater createTheater(@RequestBody Theater theater) {
        return theaterService.saveTheater(theater);
    }

    @PutMapping("/admin/{id}")
    public Theater updateTheater(@PathVariable Long id, @RequestBody Theater updatedTheater) {
        return theaterService.getTheaterById(id)
                .map(existing -> {
                    existing.setName(updatedTheater.getName());
                    existing.setLocation(updatedTheater.getLocation());
                    existing.setScreens(updatedTheater.getScreens());
                    existing.setFacilities(updatedTheater.getFacilities());
                    existing.setSeatingLayout(updatedTheater.getSeatingLayout());
                    return theaterService.saveTheater(existing);
                })
                .orElseThrow(() -> new RuntimeException("Theater not found"));
    }

    @DeleteMapping("/{id}")
    public void deleteTheater(@PathVariable Long id) {
        theaterService.deleteTheater(id);
    }
}
