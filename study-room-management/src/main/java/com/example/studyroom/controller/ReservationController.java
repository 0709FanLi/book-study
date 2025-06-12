package com.example.studyroom.controller;

import com.example.studyroom.dto.SeatStatusDTO;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
        if (auth != null && auth.getName() != null && !auth.getName().equals("anonymousUser")) {
            User currentUser = userRepository.findByUsername(auth.getName()).orElse(null);
            if (currentUser != null) {
                List<Reservation> myReservations = reservationService.getUserActiveReservations(currentUser);
                model.addAttribute("reservations", myReservations);
            }
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
        reservation.setCreatedAt(LocalDateTime.now());
        reservationRepository.save(reservation);

        return "redirect:/reservations?success=reservation_created";
    }

    @GetMapping("/reservations/my")
    public String myReservations(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Reservation> myReservations = reservationService.getUserActiveReservations(currentUser);
        model.addAttribute("reservations", myReservations);
        return "reservations";
    }
    
    @GetMapping("/reservations/history")
    public String reservationHistory(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        
        List<Reservation> historyReservations = reservationService.getUserReservationHistory(currentUser);
        model.addAttribute("historyReservations", historyReservations);
        return "reservation-history";
    }

    @PostMapping("/reservations/cancel/{id}")
    public String cancelReservation(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Reservation reservation = reservationRepository.findById(id).orElseThrow(() -> new RuntimeException("Reservation not found"));
        // Basic check to ensure user can only cancel their own reservation
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(reservation.getUser().getUsername().equals(auth.getName())) {
            // 检查是否可以取消（未确认入座的才能取消）
            if (reservation.canCancel()) {
                reservation.setStatus("CANCELLED");
                reservationRepository.save(reservation);
                redirectAttributes.addFlashAttribute("success", "预约已取消");
            } else {
                redirectAttributes.addFlashAttribute("error", "已确认入座的预约无法取消");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "无权取消此预约");
        }
        return "redirect:/reservations";
    }
    
    // 确认入座功能
    @PostMapping("/reservations/checkin/{id}")
    public String checkInReservation(@PathVariable Long id, 
                                    RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        
        String result = reservationService.checkInReservation(id, currentUser);
        
        if (result.contains("成功")) {
            redirectAttributes.addFlashAttribute("success", result);
        } else {
            redirectAttributes.addFlashAttribute("error", result);
        }
        
        return "redirect:/reservations";
    }
    
    // 续约功能
    @PostMapping("/reservations/extend/{id}")
    public String extendReservation(@PathVariable Long id, 
                                   @RequestParam int hours,
                                   RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        
        String result = reservationService.extendReservation(id, hours, currentUser);
        
        if (result.contains("成功")) {
            redirectAttributes.addFlashAttribute("success", result);
        } else {
            redirectAttributes.addFlashAttribute("error", result);
        }
        
        return "redirect:/reservations";
    }
    
    // 退座功能
    @PostMapping("/reservations/checkout/{id}")
    public String checkoutReservation(@PathVariable Long id,
                                     RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        
        String result = reservationService.checkoutReservation(id, currentUser);
        
        if (result.contains("成功")) {
            redirectAttributes.addFlashAttribute("success", result);
        } else {
            redirectAttributes.addFlashAttribute("error", result);
        }
        
        return "redirect:/reservations";
    }

    @GetMapping("/complaints")
    public String showComplaintForm(Model model) {
        return "admin/complaints";
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
        
        return "redirect:/?complaint_submitted";
    }

    // API: 获取座位状态（用于座位分布图）
    @GetMapping("/api/seats/status")
    @ResponseBody
    public ResponseEntity<List<SeatStatusDTO>> getSeatStatus() {
        List<Seat> allSeats = seatRepository.findAll();
        List<SeatStatusDTO> seatStatuses = new ArrayList<>();
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (Seat seat : allSeats) {
            SeatStatusDTO status = new SeatStatusDTO();
            status.setId(seat.getId());
            status.setSeatNumber(seat.getSeatNumber());
            status.setSeatRow(seat.getSeatRow());
            status.setSeatCol(seat.getSeatCol());
            status.setSeatArea(seat.getSeatArea());
            
            // 检查座位是否被预约
            List<Reservation> activeReservations = reservationRepository
                .findBySeatIdAndStatusAndEndTimeAfter(seat.getId(), "ACTIVE", now);
            
            if (activeReservations.isEmpty()) {
                status.setAvailable(true);
                status.setCurrentUserName(null);
                status.setReservationEndTime(null);
            } else {
                Reservation currentReservation = activeReservations.get(0);
                status.setAvailable(false);
                status.setCurrentUserName(currentReservation.getUser().getFullName());
                status.setReservationEndTime(currentReservation.getEndTime().format(formatter));
            }
            
            seatStatuses.add(status);
        }
        
        return ResponseEntity.ok(seatStatuses);
    }
} 