package com.example.studyroom.controller;

import com.example.studyroom.model.*;
import com.example.studyroom.repository.*;
import com.example.studyroom.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ReservationController {
    @Autowired private SeatRepository seatRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ComplaintRepository complaintRepository;
    @Autowired private ReservationService reservationService;

    @GetMapping("/reservations")
    public String showReservationPage(Model model) {
        List<Seat> allSeats = seatRepository.findAll();
        model.addAttribute("seats", allSeats);
        
        // Load user's current active reservations
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            List<Reservation> myReservations = reservationService.getUserActiveReservations(auth.getName());
            model.addAttribute("reservations", myReservations);
            
            // Load reservations ending soon for notifications
            List<Reservation> endingSoon = reservationService.getReservationsEndingSoon();
            model.addAttribute("endingSoon", endingSoon);
        }
        return "reservations";
    }

    @PostMapping("/reservations")
    public String makeReservation(@RequestParam Long seatId,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
                                  @RequestParam(required = false) String remarks,
                                  Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));

        // Basic conflict check
        List<Long> reservedSeatIds = reservationRepository.findReservedSeatIds(startTime, endTime);
        if(reservedSeatIds.contains(seatId)) {
            model.addAttribute("error", "该时间段座位已被预约！");
            model.addAttribute("seats", seatRepository.findAll());
            return "reservations";
        }
        
        Reservation reservation = new Reservation();
        reservation.setUser(currentUser);
        reservation.setSeat(seatRepository.findById(seatId).orElseThrow(() -> new RuntimeException("Seat not found")));
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setStatus("ACTIVE");
        reservation.setRemarks(remarks);
        reservation.setExtensionCount(0);
        reservationRepository.save(reservation);

        return "redirect:/reservations?success=reservation_created";
    }
    
    /**
     * 续约API端点
     */
    @PostMapping("/reservations/extend/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> extendReservation(@PathVariable Long id, 
                                                                @RequestParam int hours) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        ReservationService.ReservationResult result = reservationService.extendReservation(id, username, hours);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());
        
        if (result.isSuccess()) {
            Reservation reservation = result.getReservation();
            response.put("newEndTime", reservation.getEndTime().toString());
            response.put("extensionCount", reservation.getExtensionCount());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 退座API端点
     */
    @PostMapping("/reservations/checkout/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkOut(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        ReservationService.ReservationResult result = reservationService.checkOut(id, username);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());
        
        if (result.isSuccess()) {
            Reservation reservation = result.getReservation();
            response.put("actualEndTime", reservation.getActualEndTime().toString());
            response.put("status", reservation.getStatus());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取用户预约历史
     */
    @GetMapping("/reservations/history")
    public String showReservationHistory(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            User currentUser = userRepository.findByUsername(auth.getName()).orElse(null);
            if (currentUser != null) {
                List<Reservation> allReservations = reservationRepository.findAllReservationsByUserId(currentUser.getId());
                model.addAttribute("historyReservations", allReservations);
            }
        }
        return "reservation-history";
    }
    
    /**
     * 检查预约状态API（用于前端轮询）
     */
    @GetMapping("/reservations/status/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getReservationStatus(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName()).orElse(null);
        
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Reservation reservation = reservationRepository.findActiveReservationByIdAndUserId(id, currentUser.getId()).orElse(null);
        
        Map<String, Object> response = new HashMap<>();
        if (reservation != null) {
            response.put("exists", true);
            response.put("status", reservation.getStatus());
            response.put("endTime", reservation.getEndTime().toString());
            response.put("extensionCount", reservation.getExtensionCount());
            
            // 计算剩余时间
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(reservation.getEndTime())) {
                long remainingMinutes = java.time.Duration.between(now, reservation.getEndTime()).toMinutes();
                response.put("remainingMinutes", remainingMinutes);
                response.put("canExtend", remainingMinutes <= 30 && reservation.getExtensionCount() < 3);
            } else {
                response.put("remainingMinutes", 0);
                response.put("canExtend", false);
            }
        } else {
            response.put("exists", false);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reservations/cancel/{id}")
    public String cancelReservation(@PathVariable Long id) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new RuntimeException("Reservation not found"));
        // Basic check to ensure user can only cancel their own reservation
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(reservation.getUser().getUsername().equals(auth.getName())) {
            reservation.setStatus("CANCELLED");
            reservationRepository.save(reservation);
        }
        return "redirect:/reservations?success=reservation_cancelled";
    }
    
    @PostMapping("/complaints")
    public String submitComplaint(@RequestParam String title, @RequestParam String content) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        
        Complaint complaint = new Complaint();
        complaint.setUser(currentUser);
        complaint.setTitle(title);
        complaint.setContent(content);
        complaintRepository.save(complaint);
        
        return "redirect:/reservations?success=complaint_submitted";
    }
} 