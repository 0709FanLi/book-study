package com.example.studyroom.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatStatusDTO {
    private Long id;
    private String seatNumber;
    private Integer seatRow;
    private Integer seatCol;
    private String seatArea;
    private boolean isAvailable;
    private String currentUserName; // 如果被占用，显示用户名
    private String reservationEndTime; // 预约结束时间
} 