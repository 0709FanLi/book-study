package com.example.studyroom.repository;

import com.example.studyroom.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserIdAndStatus(Long userId, String status);

    @Query("SELECT r.seat.id FROM Reservation r WHERE r.status = 'ACTIVE' AND ((r.startTime < :endTime) AND (r.endTime > :startTime))")
    List<Long> findReservedSeatIds(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 数据分析相关查询方法
    long countByStatus(String status);
    
    @Query("SELECT DATE(r.startTime), COUNT(r) FROM Reservation r WHERE r.startTime >= :startTime AND r.startTime <= :endTime GROUP BY DATE(r.startTime)")
    List<Object[]> getReservationCountByDateRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT s.seatNumber, COUNT(r) FROM Reservation r JOIN r.seat s GROUP BY s.seatNumber ORDER BY COUNT(r) DESC")
    List<Object[]> getSeatUsageStats();
    
    @Query("SELECT u.fullName, COUNT(r) FROM Reservation r JOIN r.user u GROUP BY u.fullName ORDER BY COUNT(r) DESC")
    List<Object[]> getUserActivityStats();
    
    @Query("SELECT HOUR(r.startTime), COUNT(r) FROM Reservation r GROUP BY HOUR(r.startTime)")
    List<Object[]> getHourlyUsageStats();
    
    // 续约和退座相关查询方法
    
    // 查找用户当前活跃的预约
    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId AND r.status = 'ACTIVE' AND r.endTime > :currentTime")
    List<Reservation> findActiveReservationsByUserId(@Param("userId") Long userId, @Param("currentTime") LocalDateTime currentTime);
    
    // 查找指定预约ID且属于指定用户的活跃预约
    @Query("SELECT r FROM Reservation r WHERE r.id = :reservationId AND r.user.id = :userId AND r.status = 'ACTIVE'")
    Optional<Reservation> findActiveReservationByIdAndUserId(@Param("reservationId") Long reservationId, @Param("userId") Long userId);
    
    // 检查座位在指定时间段是否有冲突（续约时间检查）
    @Query("SELECT r FROM Reservation r WHERE r.seat.id = :seatId AND r.status = 'ACTIVE' AND r.id != :excludeReservationId AND ((r.startTime < :endTime) AND (r.endTime > :startTime))")
    List<Reservation> findConflictingReservations(@Param("seatId") Long seatId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("excludeReservationId") Long excludeReservationId);
    
    // 查找即将到期的预约（提醒续约）
    @Query("SELECT r FROM Reservation r WHERE r.status = 'ACTIVE' AND r.endTime BETWEEN :startTime AND :endTime")
    List<Reservation> findReservationsEndingSoon(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 统计用户今日续约次数
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId AND DATE(r.lastExtendedAt) = DATE(:today)")
    Long countTodayExtensionsByUserId(@Param("userId") Long userId, @Param("today") LocalDateTime today);
    
    // 查找用户历史预约记录（包括已完成和已取消）
    @Query("SELECT r FROM Reservation r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    List<Reservation> findAllReservationsByUserId(@Param("userId") Long userId);
} 