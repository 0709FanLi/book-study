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
} 