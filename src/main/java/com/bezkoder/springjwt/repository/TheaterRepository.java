package com.bezkoder.springjwt.repository;

import com.bezkoder.springjwt.models.Theater;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long> { }
