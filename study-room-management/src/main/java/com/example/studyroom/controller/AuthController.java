package com.example.studyroom.controller;

import com.example.studyroom.model.User;
import com.example.studyroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String studentId,
                               @RequestParam String fullName,
                               Model model) {
        if (userRepository.findByUsername(username).isPresent()) {
            model.addAttribute("error", "用户名已存在！");
            return "auth/register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setStudentId(studentId);
        user.setFullName(fullName);
        user.setRole("ROLE_USER"); // All registrations are for students
        userRepository.save(user);

        return "redirect:/login?registered";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String resetPassword(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String confirmPassword,
                               Model model) {
        // 验证密码一致性
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "两次输入的密码不一致！");
            return "auth/forgot-password";
        }

        // 验证密码长度
        if (password.length() < 6) {
            model.addAttribute("error", "密码长度至少为6位！");
            return "auth/forgot-password";
        }

        // 查找用户
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            model.addAttribute("error", "用户名不存在！");
            return "auth/forgot-password";
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        // 重定向到登录页面，显示成功消息
        model.addAttribute("success", "密码重置成功！请使用新密码登录。");
        return "auth/login";
    }
} 