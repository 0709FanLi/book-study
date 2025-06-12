package com.example.studyroom.service;

import com.example.studyroom.model.Reservation;
import com.example.studyroom.model.User;
import com.example.studyroom.model.Seat;
import com.example.studyroom.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class ReservationService {
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    /**
     * 续约预约
     * @param reservationId 预约ID
     * @param hours 续约小时数
     * @param user 当前用户
     * @return 续约结果消息
     */
    public String extendReservation(Long reservationId, int hours, User user) {
        // 验证参数
        if (hours < 1 || hours > 2) {
            return "续约时长必须在1-2小时之间";
        }
        
        // 查找预约
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElse(null);
        
        if (reservation == null) {
            return "预约不存在";
        }
        
        // 验证用户权限
        if (!reservation.getUser().getId().equals(user.getId())) {
            return "无权操作此预约";
        }
        
        // 检查是否可以续约
        if (!reservation.canExtend()) {
            if (reservation.getExtensionCount() >= 3) {
                return "已达到最大续约次数（3次）";
            } else if (!"ACTIVE".equals(reservation.getStatus())) {
                return "只有进行中的预约可以续约";
            } else {
                return "只能在预约结束前30分钟内申请续约";
            }
        }
        
        // 检查用户每日续约次数限制
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        
        long todayExtensionCount = reservationRepository.countUserExtensionsToday(
            user.getId(), todayStart, todayEnd);
        
        if (todayExtensionCount >= 5) {
            return "每日最多可续约5次";
        }
        
        // 计算新的结束时间
        LocalDateTime newEndTime = reservation.getEndTime().plusHours(hours);
        
        // 检查时间冲突
        List<Long> conflictingSeats = reservationRepository.findReservedSeatIds(
            reservation.getEndTime(), newEndTime);
        
        if (conflictingSeats.contains(reservation.getSeat().getId())) {
            return "续约时间段与其他预约冲突";
        }
        
        // 执行续约
        reservation.setEndTime(newEndTime);
        reservation.setExtensionCount(reservation.getExtensionCount() + 1);
        reservation.setLastExtendedAt(LocalDateTime.now());
        reservation.setStatus("EXTENDED");
        
        reservationRepository.save(reservation);
        
        return "续约成功！已延长" + hours + "小时";
    }
    
    /**
     * 确认入座
     * @param reservationId 预约ID
     * @param user 当前用户
     * @return 确认入座结果消息
     */
    public String checkInReservation(Long reservationId, User user) {
        // 查找预约
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElse(null);
        
        if (reservation == null) {
            return "预约不存在";
        }
        
        // 验证用户权限
        if (!reservation.getUser().getId().equals(user.getId())) {
            return "无权操作此预约";
        }
        
        // 检查是否可以确认入座
        if (!reservation.canCheckIn()) {
            if (Boolean.TRUE.equals(reservation.getIsCheckedIn())) {
                return "已经确认入座，无需重复操作";
            } else {
                return "当前预约状态不允许确认入座";
            }
        }
        
        // 检查是否已经开始
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(reservation.getStartTime())) {
            return "预约尚未开始，无法确认入座";
        }
        
        // 检查是否超时（预约开始后30分钟内必须确认入座）
        LocalDateTime checkInDeadline = reservation.getStartTime().plusMinutes(30);
        if (now.isAfter(checkInDeadline)) {
            // 超时自动取消预约
            reservation.setStatus("CANCELLED");
            reservation.setRemarks("超时未确认入座，自动取消");
            reservationRepository.save(reservation);
            return "预约已超时（超过开始时间30分钟未确认入座），预约已自动取消";
        }
        
        // 执行确认入座
        reservation.setIsCheckedIn(true);
        reservation.setCheckedInAt(now);
        
        reservationRepository.save(reservation);
        
        return "确认入座成功！座位已锁定，无法取消预约或提前退座";
    }

    /**
     * 退座
     * @param reservationId 预约ID
     * @param user 当前用户
     * @return 退座结果消息
     */
    public String checkoutReservation(Long reservationId, User user) {
        // 查找预约
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElse(null);
        
        if (reservation == null) {
            return "预约不存在";
        }
        
        // 验证用户权限
        if (!reservation.getUser().getId().equals(user.getId())) {
            return "无权操作此预约";
        }
        
        // 检查是否可以退座
        if (!reservation.canCheckout()) {
            return "当前预约状态不允许退座";
        }
        
        // 检查是否已经开始
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(reservation.getStartTime())) {
            return "预约尚未开始，请取消预约";
        }
        
        // 执行退座
        reservation.setActualEndTime(now);
        reservation.setStatus("COMPLETED");
        
        reservationRepository.save(reservation);
        
        // 计算实际使用时长
        long minutes = java.time.Duration.between(reservation.getStartTime(), now).toMinutes();
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        
        String duration = hours > 0 ? 
            hours + "小时" + (remainingMinutes > 0 ? remainingMinutes + "分钟" : "") :
            remainingMinutes + "分钟";
        
        return "退座成功！实际使用时长：" + duration;
    }
    
    /**
     * 获取用户当前活跃预约
     */
    public List<Reservation> getUserActiveReservations(User user) {
        return reservationRepository.findByUserIdAndStatusIn(
            user.getId(), 
            Arrays.asList("ACTIVE", "EXTENDED")
        );
    }
    
    /**
     * 获取用户预约历史
     */
    public List<Reservation> getUserReservationHistory(User user) {
        return reservationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }
    
    /**
     * 查找座位的当前活跃预约
     */
    public Reservation findActiveBySeat(Seat seat) {
        LocalDateTime now = LocalDateTime.now();
        return reservationRepository.findBySeatIdAndStatusInAndStartTimeBeforeAndEndTimeAfter(
            seat.getId(),
            Arrays.asList("ACTIVE", "EXTENDED"),
            now,
            now
        );
    }
} 