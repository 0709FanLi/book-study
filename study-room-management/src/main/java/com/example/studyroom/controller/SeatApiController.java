package com.example.studyroom.controller;

import com.example.studyroom.dto.SeatStatusDTO;
import com.example.studyroom.model.Seat;
import com.example.studyroom.model.Reservation;
import com.example.studyroom.service.SeatService;
import com.example.studyroom.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/seats")
public class SeatApiController {

    @Autowired
    private SeatService seatService;

    @Autowired
    private ReservationService reservationService;

    @GetMapping("/map-status")
    public ResponseEntity<List<SeatStatusDTO>> getSeatMapStatus() {
        try {
            List<Seat> seats = seatService.findAll();
            List<SeatStatusDTO> seatStatusList = seats.stream()
                .map(this::convertToSeatStatusDTO)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(seatStatusList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private SeatStatusDTO convertToSeatStatusDTO(Seat seat) {
        SeatStatusDTO dto = new SeatStatusDTO();
        dto.setId(seat.getId());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setSeatRow(seat.getSeatRow() != null ? seat.getSeatRow() : 1);
        dto.setSeatCol(seat.getSeatCol() != null ? seat.getSeatCol() : 1);
        dto.setSeatArea(seat.getSeatArea() != null ? seat.getSeatArea() : "A");
        
        // 检查座位是否被预约
        Reservation currentReservation = reservationService.findActiveBySeat(seat);
        if (currentReservation != null) {
            dto.setAvailable(false);
            dto.setCurrentUserName(currentReservation.getUser().getUsername());
            dto.setReservationEndTime(currentReservation.getEndTime()
                .format(DateTimeFormatter.ofPattern("HH:mm")));
        } else {
            dto.setAvailable(true);
            dto.setCurrentUserName(null);
            dto.setReservationEndTime(null);
        }
        
        return dto;
    }
} 