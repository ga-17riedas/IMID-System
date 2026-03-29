package com.example.medicalsystem.controller;

import com.example.medicalsystem.dto.AnalyticsDataDTO;
import com.example.medicalsystem.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@RestController
@RequestMapping("/api/admin/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AnalyticsDataDTO> getAnalyticsData(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        try {
            // 尝试解析日期时间字符串
            System.out.println("收到的日期参数: startDate=" + startDate + ", endDate=" + endDate);
            
            LocalDateTime startDateTime;
            LocalDateTime endDateTime;
            
            try {
                // 尝试解析 yyyy/M/d HH:mm:ss 格式
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss");
                startDateTime = LocalDateTime.parse(startDate, formatter);
                endDateTime = LocalDateTime.parse(endDate, formatter);
            } catch (DateTimeParseException e1) {
                try {
                    // 尝试解析 yyyy-MM-dd HH:mm 格式
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    startDateTime = LocalDateTime.parse(startDate, formatter);
                    endDateTime = LocalDateTime.parse(endDate, formatter);
                } catch (DateTimeParseException e2) {
                    // 尝试解析 yyyy-MM-dd 格式
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    startDateTime = LocalDate.parse(startDate, formatter).atStartOfDay();
                    endDateTime = LocalDate.parse(endDate, formatter).atStartOfDay();
                }
            }
            
            // 转换为LocalDate以保持与服务层接口兼容
            LocalDate startLocalDate = startDateTime.toLocalDate();
            LocalDate endLocalDate = endDateTime.toLocalDate();
            
            // 打印日期范围，用于调试
            System.out.println("解析后的日期范围: " + startLocalDate + " 至 " + endLocalDate);
            
            AnalyticsDataDTO data = analyticsService.getAnalyticsData(startLocalDate, endLocalDate);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            System.err.println("日期解析错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
} 