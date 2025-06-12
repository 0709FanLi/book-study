package com.example.studyroom.controller;

import com.example.studyroom.model.User;
import com.example.studyroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String showProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        
        model.addAttribute("user", currentUser);
        
        // 根据角色返回不同的页面
        if (currentUser.getRole().equals("ROLE_ADMIN")) {
            return "profile/admin-profile";
        } else {
            return "profile/student-profile";
        }
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam String fullName,
                               @RequestParam(required = false) String studentId,
                               @RequestParam(required = false) String className,
                               @RequestParam(required = false) String grade,
                               @RequestParam(required = false) String employeeId,
                               @RequestParam(required = false) String officeNumber,
                               @RequestParam(required = false) String phone,
                               Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("用户未找到"));

        // 更新基本信息
        currentUser.setFullName(fullName);
        currentUser.setPhone(phone);

        // 根据角色更新不同字段
        if (currentUser.getRole().equals("ROLE_ADMIN")) {
            currentUser.setEmployeeId(employeeId);
            currentUser.setOfficeNumber(officeNumber);
        } else {
            currentUser.setStudentId(studentId);
            currentUser.setClassName(className);
            currentUser.setGrade(grade);
        }

        userRepository.save(currentUser);
        
        model.addAttribute("user", currentUser);
        model.addAttribute("success", "个人信息更新成功！");
        
        // 根据角色返回不同的页面
        if (currentUser.getRole().equals("ROLE_ADMIN")) {
            return "profile/admin-profile";
        } else {
            return "profile/student-profile";
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("用户未找到"));

        // 验证当前密码
        if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
            model.addAttribute("user", currentUser);
            model.addAttribute("passwordError", "当前密码不正确！");
            if (currentUser.getRole().equals("ROLE_ADMIN")) {
                return "profile/admin-profile";
            } else {
                return "profile/student-profile";
            }
        }

        // 验证新密码确认
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("user", currentUser);
            model.addAttribute("passwordError", "新密码与确认密码不一致！");
            if (currentUser.getRole().equals("ROLE_ADMIN")) {
                return "profile/admin-profile";
            } else {
                return "profile/student-profile";
            }
        }

        // 更新密码
        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);

        model.addAttribute("user", currentUser);
        model.addAttribute("passwordSuccess", "密码修改成功！");
        
        if (currentUser.getRole().equals("ROLE_ADMIN")) {
            return "profile/admin-profile";
        } else {
            return "profile/student-profile";
        }
    }
} 