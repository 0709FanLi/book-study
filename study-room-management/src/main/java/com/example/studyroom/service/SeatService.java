package com.example.studyroom.service;

import com.example.studyroom.model.Seat;
import com.example.studyroom.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatService {

    @Autowired
    private SeatRepository seatRepository;

    public List<Seat> findAll() {
        return seatRepository.findAll();
    }

    public Optional<Seat> findById(Long id) {
        return seatRepository.findById(id);
    }

    public Seat save(Seat seat) {
        return seatRepository.save(seat);
    }

    public void deleteById(Long id) {
        seatRepository.deleteById(id);
    }

    public List<Seat> findAvailableSeats() {
        // 这里可以根据实际需求实现逻辑
        return seatRepository.findAll();
    }
} 