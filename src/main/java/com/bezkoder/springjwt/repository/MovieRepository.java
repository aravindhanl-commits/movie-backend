package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> { }
