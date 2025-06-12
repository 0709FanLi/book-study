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
    
    // 续约和退座相关字段
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "last_extended_at")
    private LocalDateTime lastExtendedAt;
    
    @Column(name = "extension_count")
    private Integer extensionCount = 0;
    
    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;
    
    @Column(name = "remarks")
    private String remarks;
    
    // 确认入座相关字段
    @Column(name = "is_checked_in")
    private Boolean isCheckedIn = false;
    
    @Column(name = "checked_in_at")
    private LocalDateTime checkedInAt;
    
    // 业务方法
    public boolean canExtend() {
        // 只有ACTIVE状态的预约可以续约
        if (!"ACTIVE".equals(this.status)) {
            return false;
        }
        
        // 检查续约次数限制
        if (this.extensionCount >= 3) {
            return false;
        }
        
        // 检查是否在续约时间窗口内（结束前30分钟）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime extendWindow = this.endTime.minusMinutes(30);
        
        return now.isAfter(extendWindow) && now.isBefore(this.endTime);
    }
    
    public boolean canCheckout() {
        // 只有ACTIVE或EXTENDED状态的预约可以退座，且未确认入座的才能退座
        return ("ACTIVE".equals(this.status) || "EXTENDED".equals(this.status)) && !Boolean.TRUE.equals(this.isCheckedIn);
    }
    
    public boolean canCancel() {
        // 只有未确认入座的预约才能取消
        return "ACTIVE".equals(this.status) && !Boolean.TRUE.equals(this.isCheckedIn);
    }
    
    public boolean canCheckIn() {
        // 只有ACTIVE状态且未确认入座的预约可以确认入座
        return "ACTIVE".equals(this.status) && !Boolean.TRUE.equals(this.isCheckedIn);
    }
    
    public String getStatusDisplay() {
        switch (this.status) {
            case "ACTIVE": return "进行中";
            case "EXTENDED": return "已续约";
            case "COMPLETED": return "已完成";
            case "CANCELLED": return "已取消";
            default: return this.status;
        }
    }
} 