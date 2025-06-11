package com.example.studyroom.repository;

import com.example.studyroom.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
     @Query("SELECT c FROM Complaint c ORDER BY c.createdAt DESC")
     List<Complaint> findAllByOrderByCreatedAtDesc();
} 