package com.bezkoder.springjwt.controllers;

import com.bezkoder.springjwt.models.Show;
import com.bezkoder.springjwt.security.services.ShowService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/shows")
@CrossOrigin(origins = "http://localhost:8081") // same as your MovieController
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping
    public List<Show> getAllShows() {
        return showService.getAllShows();
    }

    @GetMapping("/{id}")
    public Optional<Show> getShowById(@PathVariable Long id) {
        return showService.getShowById(id);
    }

    @GetMapping("/movie/{movieId}")
    public List<Show> getShowsByMovieId(@PathVariable Long movieId) {
        return showService.getShowsByMovieId(movieId);
    }

    @GetMapping("/theater/{theaterId}")
    public List<Show> getShowsByTheaterId(@PathVariable Long theaterId) {
        return showService.getShowsByTheaterId(theaterId);
    }

    @PostMapping("/admin")
    public Show createShow(@RequestBody Show show) {
        return showService.saveShow(show);
    }

    @PutMapping("/admin/{id}")
    public Show updateShow(@PathVariable Long id, @RequestBody Show updatedShow) {
        return showService.getShowById(id)
                .map(existingShow -> {
                    existingShow.setMovieId(updatedShow.getMovieId());
                    existingShow.setTheaterId(updatedShow.getTheaterId());
                    existingShow.setShowDate(updatedShow.getShowDate());
                    existingShow.setShowTime(updatedShow.getShowTime());
                    existingShow.setPrice(updatedShow.getPrice());
                    existingShow.setAvailableSeats(updatedShow.getAvailableSeats());
                    existingShow.setScreenNumber(updatedShow.getScreenNumber());
                    return showService.saveShow(existingShow);
                })
                .orElseThrow(() -> new RuntimeException("Show not found"));
    }

    @DeleteMapping("/{id}")
    public void deleteShow(@PathVariable Long id) {
        showService.deleteShow(id);
    }
}
