package com.example.studyroom.controller;

import com.example.studyroom.model.Announcement;
import com.example.studyroom.model.Complaint;
import com.example.studyroom.model.Reservation;
import com.example.studyroom.repository.AnnouncementRepository;
import com.example.studyroom.repository.ComplaintRepository;
import com.example.studyroom.repository.ReservationRepository;
import com.example.studyroom.repository.UserRepository;
import com.example.studyroom.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private AnnouncementRepository announcementRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private ComplaintRepository complaintRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private SeatRepository seatRepository;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    // Announcements Management
    @GetMapping("/announcements")
    public String manageAnnouncements(Model model) {
        model.addAttribute("announcements", announcementRepository.findAllByOrderByCreatedAtDesc());
        return "admin/announcements";
    }

    @PostMapping("/announcements")
    public String addAnnouncement(@RequestParam String title, @RequestParam String content) {
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcementRepository.save(announcement);
        return "redirect:/admin/announcements";
    }
    
    @PostMapping("/announcements/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id) {
        announcementRepository.deleteById(id);
        return "redirect:/admin/announcements";
    }
    
    // View all reservations
    @GetMapping("/reservations")
    public String viewAllReservations(Model model) {
        List<Reservation> allReservations = reservationRepository.findAll();
        model.addAttribute("reservations", allReservations);
        return "admin/reservations";
    }

    // View all complaints
    @GetMapping("/complaints")
    public String viewAllComplaints(Model model) {
        List<Complaint> allComplaints = complaintRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("complaints", allComplaints);
        return "admin/complaints";
    }
    
    // Analytics main page
    @GetMapping("/analytics")
    public String analytics(Model model) {
        // 统计数据
        long totalUsers = userRepository.count();
        long totalSeats = seatRepository.count();
        long totalReservations = reservationRepository.count();
        
        // 当前活跃预约数
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> allReservations = reservationRepository.findAll();
        long activeReservations = allReservations.stream()
            .filter(r -> "ACTIVE".equals(r.getStatus()) || "EXTENDED".equals(r.getStatus()))
            .filter(r -> r.getStartTime().isBefore(now) && r.getEndTime().isAfter(now))
            .count();
        
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalSeats", totalSeats);
        model.addAttribute("totalReservations", totalReservations);
        model.addAttribute("activeReservations", activeReservations);
        
        return "admin/analytics";
    }
    
    // API: 预约趋势数据（最近7天）
    @GetMapping("/analytics/reservation-trend")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getReservationTrend() {
        List<Reservation> allReservations = reservationRepository.findAll();
        
        // 获取最近7天的数据
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        
        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            final LocalDate currentDate = date; // 创建final变量用于lambda表达式
            dates.add(currentDate.format(DateTimeFormatter.ofPattern("MM-dd")));
            
            long count = allReservations.stream()
                .filter(r -> r.getCreatedAt() != null)
                .filter(r -> r.getCreatedAt().toLocalDate().equals(currentDate))
                .count();
            counts.add(count);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("counts", counts);
        
        return ResponseEntity.ok(result);
    }
    
    // API: 座位使用率数据
    @GetMapping("/analytics/seat-usage")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSeatUsage() {
        List<Reservation> allReservations = reservationRepository.findAll();
        
        // 按座位分组统计使用次数
        Map<String, Long> seatUsageMap = allReservations.stream()
            .collect(Collectors.groupingBy(
                r -> r.getSeat().getSeatNumber(),
                Collectors.counting()
            ));
        
        // 转换为列表格式
        List<String> seatNumbers = new ArrayList<>(seatUsageMap.keySet());
        List<Long> usageCounts = seatNumbers.stream()
            .map(seatUsageMap::get)
            .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("seatNumbers", seatNumbers);
        result.put("usageCounts", usageCounts);
        
        return ResponseEntity.ok(result);
    }
    
    // API: 用户活跃度数据
    @GetMapping("/analytics/user-activity")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserActivity() {
        List<Reservation> allReservations = reservationRepository.findAll();
        
        // 按用户分组统计预约次数
        Map<String, Long> userActivityMap = allReservations.stream()
            .collect(Collectors.groupingBy(
                r -> r.getUser().getFullName() != null ? r.getUser().getFullName() : r.getUser().getUsername(),
                Collectors.counting()
            ));
        
        // 按预约次数降序排序
        List<Map.Entry<String, Long>> sortedEntries = userActivityMap.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .collect(Collectors.toList());
        
        List<String> userNames = sortedEntries.stream()
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        List<Long> reservationCounts = sortedEntries.stream()
            .map(Map.Entry::getValue)
            .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("userNames", userNames);
        result.put("reservationCounts", reservationCounts);
        
        return ResponseEntity.ok(result);
    }
    
    // API: 24小时时间段使用分析
    @GetMapping("/analytics/hourly-usage")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getHourlyUsage() {
        List<Reservation> allReservations = reservationRepository.findAll();
        
        // 按小时分组统计
        Map<Integer, Long> hourlyUsageMap = allReservations.stream()
            .filter(r -> r.getStartTime() != null)
            .collect(Collectors.groupingBy(
                r -> r.getStartTime().getHour(),
                Collectors.counting()
            ));
        
        // 生成0-23小时的完整数据
        List<String> hours = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        
        for (int hour = 0; hour < 24; hour++) {
            hours.add(String.format("%02d:00", hour));
            counts.add(hourlyUsageMap.getOrDefault(hour, 0L));
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("hours", hours);
        result.put("counts", counts);
        
        return ResponseEntity.ok(result);
    }
}