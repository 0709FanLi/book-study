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
    
    // 学生信息字段
    private String className;  // 班级
    private String grade;      // 年级
    
    // 管理员信息字段
    private String employeeId;    // 工号
    private String officeNumber;  // 办公室编号
    
    // 通用字段
    private String phone;      // 电话号码
    
    @OneToMany(mappedBy = "user")
    private List<Reservation> reservations;

    @OneToMany(mappedBy = "user")
    private List<Complaint> complaints;
} 