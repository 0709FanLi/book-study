package com.example.studyroom.model;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
public class Seat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String seatNumber;
    @Column(nullable = false)
    private Integer seatRow;  // 行号 (1-8)
    @Column(nullable = false)
    private Integer seatCol;  // 列号 (1-10)
    @Column(nullable = false)
    private String seatArea; // 区域 (A, B, C, D)
} 