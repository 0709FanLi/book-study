package com.example.studyroom.controller;

import com.example.studyroom.model.Announcement;
import com.example.studyroom.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @GetMapping("/")
    public String home(Model model) {
        List<Announcement> announcements = announcementRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("announcements", announcements);
        return "index";
    }
} 