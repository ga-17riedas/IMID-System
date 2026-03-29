package com.example.medicalsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 * 处理用户认证和通用操作
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    /**
     * 用户登录
     * @param loginRequest 登录请求参数（用户名和密码）
     * @return 登录结果和用户信息
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        // TODO: 实现实际的用户认证逻辑
        
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> userData = new HashMap<>();
        
        // 模拟用户数据
        userData.put("id", "u1001");
        userData.put("username", username);
        userData.put("name", "测试用户");
        userData.put("role", "ADMIN"); // 或 "DOCTOR", "PATIENT"
        userData.put("avatar", "https://example.com/avatar.jpg");
        
        // 生成token (这里仅做示例)
        String token = "sample-jwt-token-" + System.currentTimeMillis();
        
        response.put("token", token);
        response.put("user", userData);
        response.put("message", "登录成功");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取当前用户信息
     * @return 用户详细信息
     */
    @GetMapping("/info")
    public ResponseEntity<?> getUserInfo() {
        // TODO: 从安全上下文中获取当前用户信息
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", "u1001");
        userInfo.put("username", "testuser");
        userInfo.put("name", "测试用户");
        userInfo.put("email", "test@example.com");
        userInfo.put("role", "ADMIN");
        userInfo.put("avatar", "https://example.com/avatar.jpg");
        userInfo.put("department", "医学影像部");
        userInfo.put("createdAt", "2023-01-15T08:30:00Z");
        userInfo.put("lastLogin", "2023-07-20T14:25:30Z");
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", userInfo);
        response.put("message", "获取用户信息成功");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 用户注销
     * @return 注销结果
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // TODO: 实现用户注销逻辑，清除会话等
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "注销成功");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 修改用户密码
     * @param passwordData 密码数据（旧密码和新密码）
     * @return 修改结果
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordData) {
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        
        // TODO: 实现密码修改逻辑
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "密码修改成功");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 更新用户个人资料
     * @param profileData 用户资料数据
     * @return 更新结果
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> profileData) {
        // TODO: 实现个人资料更新逻辑
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "个人资料更新成功");
        response.put("status", "success");
        response.put("data", profileData);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取用户消息通知
     * @return 用户消息列表
     */
    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications() {
        // TODO: 实现获取用户通知的逻辑
        
        // 模拟通知数据
        Map<String, Object> notification1 = new HashMap<>();
        notification1.put("id", "n001");
        notification1.put("title", "系统通知");
        notification1.put("content", "医疗系统已更新到最新版本");
        notification1.put("time", "2023-07-19T09:30:00Z");
        notification1.put("read", true);
        
        Map<String, Object> notification2 = new HashMap<>();
        notification2.put("id", "n002");
        notification2.put("title", "报告提醒");
        notification2.put("content", "您有一份新的医疗报告待查看");
        notification2.put("time", "2023-07-20T14:15:00Z");
        notification2.put("read", false);
        
        // 创建通知列表
        List<Map<String, Object>> notifications = new ArrayList<>();
        notifications.add(notification1);
        notifications.add(notification2);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", notifications);
        response.put("message", "获取通知成功");
        response.put("status", "success");
        response.put("unreadCount", 1);
        
        return ResponseEntity.ok(response);
    }
} 