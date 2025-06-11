package com.example.studyroom.model;

import lombok.Data;
import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(unique = true)
    private String studentId;
    private String fullName;
    @Column(nullable = false)
    private String role;
    
    @OneToMany(mappedBy = "user")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "user")
    private List<Complaint> complaints;
} 