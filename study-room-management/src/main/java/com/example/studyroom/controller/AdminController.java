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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    // 数据分析页面
    @GetMapping("/analytics") 
    public String analytics(Model model) {
        // 基础统计数据
        long totalUsers = userRepository.count();
        long totalSeats = seatRepository.count();
        long totalReservations = reservationRepository.count();
        long activeReservations = reservationRepository.countByStatus("ACTIVE");
        
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalSeats", totalSeats);
        model.addAttribute("totalReservations", totalReservations);
        model.addAttribute("activeReservations", activeReservations);
        
        return "admin/analytics";
    }
    
    // 获取最近7天的预约数据(API)
    @GetMapping("/analytics/reservation-trend")
    @ResponseBody
    public Map<String, Object> getReservationTrend() {
        List<Object[]> data = reservationRepository.getReservationCountByDateRange(
            LocalDateTime.now().minusDays(6).toLocalDate().atStartOfDay(),
            LocalDateTime.now()
        );
        
        List<String> dates = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        
        // 创建最近7天的日期列表
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            dates.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
        }
        
        // 初始化计数为0
        Map<String, Long> countMap = new HashMap<>();
        for (String date : dates) {
            countMap.put(date, 0L);
        }
        
        // 填充实际数据
        for (Object[] row : data) {
            LocalDate date = ((java.sql.Date) row[0]).toLocalDate();
            String dateStr = date.format(DateTimeFormatter.ofPattern("MM-dd"));
            Long count = (Long) row[1];
            countMap.put(dateStr, count);
        }
        
        // 转换为列表
        for (String date : dates) {
            counts.add(countMap.get(date));
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("counts", counts);
        
        return result;
    }
    
    // 获取座位使用率数据(API)
    @GetMapping("/analytics/seat-usage")
    @ResponseBody
    public Map<String, Object> getSeatUsage() {
        List<Object[]> data = reservationRepository.getSeatUsageStats();
        
        List<String> seatNumbers = new ArrayList<>();
        List<Long> usageCounts = new ArrayList<>();
        
        for (Object[] row : data) {
            seatNumbers.add((String) row[0]);
            usageCounts.add((Long) row[1]);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("seatNumbers", seatNumbers);
        result.put("usageCounts", usageCounts);
        
        return result;
    }
    
    // 获取用户活跃度数据(API)
    @GetMapping("/analytics/user-activity")
    @ResponseBody
    public Map<String, Object> getUserActivity() {
        List<Object[]> data = reservationRepository.getUserActivityStats();
        
        List<String> userNames = new ArrayList<>();
        List<Long> reservationCounts = new ArrayList<>();
        
        for (Object[] row : data) {
            userNames.add((String) row[0]);
            reservationCounts.add((Long) row[1]);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("userNames", userNames);
        result.put("reservationCounts", reservationCounts);
        
        return result;
    }
    
    // 获取时间段分析数据(API)
    @GetMapping("/analytics/hourly-usage")
    @ResponseBody
    public Map<String, Object> getHourlyUsage() {
        List<Object[]> data = reservationRepository.getHourlyUsageStats();
        
        List<String> hours = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        
        // 初始化24小时数据
        for (int i = 0; i < 24; i++) {
            hours.add(String.format("%02d:00", i));
            counts.add(0L);
        }
        
        // 填充实际数据
        for (Object[] row : data) {
            Integer hour = (Integer) row[0];
            Long count = (Long) row[1];
            counts.set(hour, count);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("hours", hours);
        result.put("counts", counts);
        
        return result;
    }
}