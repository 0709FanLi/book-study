package com.example.studyroom.service;

import com.example.studyroom.model.Reservation;
import com.example.studyroom.model.User;
import com.example.studyroom.repository.ReservationRepository;
import com.example.studyroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservationService {
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // 业务规则常量
    private static final int MAX_EXTENSION_COUNT = 3; // 每个预约最多续约3次
    private static final int MAX_DAILY_EXTENSIONS = 5; // 每用户每天最多续约5次
    private static final int MAX_EXTENSION_HOURS = 2; // 每次续约最多2小时
    private static final int MIN_EXTENSION_MINUTES = 30; // 每次续约最少30分钟
    
    /**
     * 续约预约
     * @param reservationId 预约ID
     * @param username 用户名
     * @param extensionHours 续约小时数
     * @return 续约结果信息
     */
    public ReservationResult extendReservation(Long reservationId, String username, int extensionHours) {
        try {
            // 1. 获取用户信息
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (!userOpt.isPresent()) {
                return ReservationResult.failure("用户不存在");
            }
            User user = userOpt.get();
            
            // 2. 验证预约是否存在且属于当前用户
            Optional<Reservation> reservationOpt = reservationRepository.findActiveReservationByIdAndUserId(reservationId, user.getId());
            if (!reservationOpt.isPresent()) {
                return ReservationResult.failure("预约不存在或已结束");
            }
            Reservation reservation = reservationOpt.get();
            
            // 3. 验证续约时间参数
            if (extensionHours < 1 || extensionHours > MAX_EXTENSION_HOURS) {
                return ReservationResult.failure("续约时间必须在1-" + MAX_EXTENSION_HOURS + "小时之间");
            }
            
            // 4. 检查续约次数限制
            if (reservation.getExtensionCount() >= MAX_EXTENSION_COUNT) {
                return ReservationResult.failure("该预约已达到最大续约次数（" + MAX_EXTENSION_COUNT + "次）");
            }
            
            // 5. 检查用户今日续约次数
            Long todayExtensions = reservationRepository.countTodayExtensionsByUserId(user.getId(), LocalDateTime.now());
            if (todayExtensions >= MAX_DAILY_EXTENSIONS) {
                return ReservationResult.failure("您今日续约次数已达上限（" + MAX_DAILY_EXTENSIONS + "次）");
            }
            
            // 6. 检查是否在预约结束前30分钟内（只能在即将结束时续约）
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endTime = reservation.getEndTime();
            if (now.isBefore(endTime.minusMinutes(30))) {
                return ReservationResult.failure("只能在预约结束前30分钟内申请续约");
            }
            
            // 7. 计算新的结束时间
            LocalDateTime newEndTime = endTime.plusHours(extensionHours);
            
            // 8. 检查续约时间段是否与其他预约冲突
            List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                reservation.getSeat().getId(), endTime, newEndTime, reservationId);
            if (!conflicts.isEmpty()) {
                return ReservationResult.failure("续约时间段与其他预约冲突，请选择其他时间");
            }
            
            // 9. 执行续约
            reservation.setEndTime(newEndTime);
            reservation.setExtensionCount(reservation.getExtensionCount() + 1);
            reservation.setLastExtendedAt(LocalDateTime.now());
            reservation.setStatus("EXTENDED");
            
            reservationRepository.save(reservation);
            
            return ReservationResult.success("续约成功！新的结束时间：" + newEndTime.toString(), reservation);
            
        } catch (Exception e) {
            return ReservationResult.failure("续约失败：" + e.getMessage());
        }
    }
    
    /**
     * 退座（提前结束预约）
     * @param reservationId 预约ID
     * @param username 用户名
     * @return 退座结果信息
     */
    public ReservationResult checkOut(Long reservationId, String username) {
        try {
            // 1. 获取用户信息
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (!userOpt.isPresent()) {
                return ReservationResult.failure("用户不存在");
            }
            User user = userOpt.get();
            
            // 2. 验证预约是否存在且属于当前用户
            Optional<Reservation> reservationOpt = reservationRepository.findActiveReservationByIdAndUserId(reservationId, user.getId());
            if (!reservationOpt.isPresent()) {
                return ReservationResult.failure("预约不存在或已结束");
            }
            Reservation reservation = reservationOpt.get();
            
            // 3. 检查是否已经开始使用（在开始时间之后）
            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(reservation.getStartTime())) {
                return ReservationResult.failure("预约尚未开始，无法退座");
            }
            
            // 4. 执行退座
            reservation.setStatus("COMPLETED");
            reservation.setActualEndTime(now);
            
            reservationRepository.save(reservation);
            
            // 5. 计算实际使用时长
            long usedMinutes = java.time.Duration.between(reservation.getStartTime(), now).toMinutes();
            
            return ReservationResult.success("退座成功！实际使用时长：" + usedMinutes + "分钟", reservation);
            
        } catch (Exception e) {
            return ReservationResult.failure("退座失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户当前活跃预约
     */
    public List<Reservation> getUserActiveReservations(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (!userOpt.isPresent()) {
            return java.util.Collections.emptyList();
        }
        return reservationRepository.findActiveReservationsByUserId(userOpt.get().getId(), LocalDateTime.now());
    }
    
    /**
     * 获取即将到期的预约（用于提醒）
     */
    public List<Reservation> getReservationsEndingSoon() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);
        return reservationRepository.findReservationsEndingSoon(now, oneHourLater);
    }
    
    /**
     * 预约操作结果类
     */
    public static class ReservationResult {
        private boolean success;
        private String message;
        private Reservation reservation;
        
        private ReservationResult(boolean success, String message, Reservation reservation) {
            this.success = success;
            this.message = message;
            this.reservation = reservation;
        }
        
        public static ReservationResult success(String message, Reservation reservation) {
            return new ReservationResult(true, message, reservation);
        }
        
        public static ReservationResult failure(String message) {
            return new ReservationResult(false, message, null);
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Reservation getReservation() { return reservation; }
    }
} 