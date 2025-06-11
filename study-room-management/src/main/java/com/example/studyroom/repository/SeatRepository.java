package com.example.studyroom.repository;

import com.example.studyroom.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
} 