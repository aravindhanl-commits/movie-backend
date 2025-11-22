package com.bezkoder.springjwt.security.services;

import com.bezkoder.springjwt.models.Theater;
import com.bezkoder.springjwt.repository.TheaterRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TheaterService {

    private final TheaterRepository theaterRepository;

    public TheaterService(TheaterRepository theaterRepository) {
        this.theaterRepository = theaterRepository;
    }

    public List<Theater> getAllTheaters() {
        return theaterRepository.findAll();
    }

    public Optional<Theater> getTheaterById(Long id) {
        return theaterRepository.findById(id);
    }

    public Theater saveTheater(Theater theater) {
        return theaterRepository.save(theater);
    }

    public void deleteTheater(Long id) {
        theaterRepository.deleteById(id);
    }
}
