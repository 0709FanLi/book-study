package com.example.studyroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
    
    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "Test endpoint is working!";
    }
    
    @GetMapping("/admin/test")
    @ResponseBody
    public String adminTest() {
        return "Admin test endpoint is working!";
    }
} 