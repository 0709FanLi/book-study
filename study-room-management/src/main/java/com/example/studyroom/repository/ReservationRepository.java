package com.example.studyroom.repository;

import com.example.studyroom.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserIdAndStatus(Long userId, String status);
    
    List<Reservation> findByUserIdAndStatusIn(Long userId, List<String> statuses);
    
    List<Reservation> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT r.seat.id FROM Reservation r WHERE (r.status = 'ACTIVE' OR r.status = 'EXTENDED') AND ((r.startTime < :endTime) AND (r.endTime > :startTime))")
    List<Long> findReservedSeatIds(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.user.id = :userId AND r.lastExtendedAt >= :startTime AND r.lastExtendedAt < :endTime")
    long countUserExtensionsToday(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 查找指定座位的当前有效预约
    List<Reservation> findBySeatIdAndStatusAndEndTimeAfter(Long seatId, String status, LocalDateTime now);
    
    // 查找座位的当前活跃预约
    @Query("SELECT r FROM Reservation r WHERE r.seat.id = :seatId AND r.status IN :statuses AND r.startTime <= :now AND r.endTime > :now")
    Reservation findBySeatIdAndStatusInAndStartTimeBeforeAndEndTimeAfter(
        @Param("seatId") Long seatId, 
        @Param("statuses") List<String> statuses, 
        @Param("now") LocalDateTime startTimeBefore, 
        @Param("now") LocalDateTime endTimeAfter
    );
} 