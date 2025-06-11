package com.example.studyroom.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Reservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;
    @Column(nullable = false)
    private String status; // "ACTIVE", "CANCELLED", "COMPLETED", "EXTENDED"
    
    // 续约相关字段
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastExtendedAt; // 最后续约时间
    private Integer extensionCount = 0; // 续约次数
    private LocalDateTime actualEndTime; // 实际结束时间（退座时记录）
    
    // 预约原因/备注
    private String remarks;
} 