package com.example.medicalsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.medicalsystem.model.SystemLog;
import com.example.medicalsystem.service.SystemLogService;
import com.example.medicalsystem.service.SystemMetricsService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 * 提供系统管理相关的API接口
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private SystemLogService systemLogService;
    
    @Autowired
    private SystemMetricsService systemMetricsService;
    
    // 系统日志相关接口
    
    /**
     * 获取系统日志
     * @param level 日志级别
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param pageable 分页参数
     * @return 分页的系统日志数据
     */
    @GetMapping("/system/logs")
    public ResponseEntity<?> getSystemLogs(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        
        // 调用服务层获取系统日志
        Page<SystemLog> logs = systemLogService.getSystemLogs(level, startDate, endDate, pageable);
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", logs);
        response.put("message", "获取系统日志成功");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 清除系统日志
     * @param params 包含开始日期和结束日期的参数
     * @return 操作结果
     */
    @PostMapping("/system/logs/clear")
    public ResponseEntity<?> clearSystemLogs(@RequestBody Map<String, String> params) {
        String levelStr = params.get("level");
        String startDateStr = params.get("startDate");
        String endDateStr = params.get("endDate");
        
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        
        if (startDateStr != null && !startDateStr.isEmpty()) {
            startDate = LocalDateTime.parse(startDateStr);
        }
        
        if (endDateStr != null && !endDateStr.isEmpty()) {
            endDate = LocalDateTime.parse(endDateStr);
        }
        
        int deletedCount = systemLogService.clearSystemLogs(levelStr, startDate, endDate);
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", deletedCount);
        response.put("message", "系统日志清除成功");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取错误日志
     * @return 错误日志列表
     */
    @GetMapping("/system/logs/errors")
    public ResponseEntity<?> getErrorLogs() {
        List<SystemLog> errorLogs = systemLogService.getErrorLogs();
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", errorLogs);
        response.put("message", "获取错误日志成功");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取系统指标
     * @return 系统指标数据
     */
    @GetMapping("/system/metrics")
    public ResponseEntity<?> getSystemMetrics() {
        Map<String, Object> metrics = systemMetricsService.getSystemMetrics();
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", metrics);
        response.put("message", "获取系统指标成功");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取系统统计信息
     * @return 系统统计信息
     */
    @GetMapping("/system/statistics")
    public ResponseEntity<?> getSystemStatistics() {
        Map<String, Object> statistics = systemMetricsService.getSystemStatistics();
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", statistics);
        response.put("message", "获取系统统计信息成功");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取性能指标
     * @return 性能指标数据
     */
    @GetMapping("/system/performance")
    public ResponseEntity<?> getPerformanceMetrics() {
        Map<String, Object> performance = systemMetricsService.getPerformanceMetrics();
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", performance);
        response.put("message", "获取性能指标成功");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    // 模拟数据生成方法
    
    private List<Map<String, Object>> generateMockLogs() {
        List<Map<String, Object>> logs = new ArrayList<>();
        
        Map<String, Object> log1 = new HashMap<>();
        log1.put("id", "1");
        log1.put("timestamp", LocalDateTime.now().minusHours(1));
        log1.put("level", "INFO");
        log1.put("source", "UserService");
        log1.put("message", "用户登录成功: admin");
        logs.add(log1);
        
        Map<String, Object> log2 = new HashMap<>();
        log2.put("id", "2");
        log2.put("timestamp", LocalDateTime.now().minusHours(2));
        log2.put("level", "WARNING");
        log2.put("source", "SecurityService");
        log2.put("message", "检测到多次失败的登录尝试: IP=192.168.1.100");
        logs.add(log2);
        
        Map<String, Object> log3 = new HashMap<>();
        log3.put("id", "3");
        log3.put("timestamp", LocalDateTime.now().minusHours(3));
        log3.put("level", "ERROR");
        log3.put("source", "DatabaseService");
        log3.put("message", "数据库连接超时");
        logs.add(log3);
        
        Map<String, Object> log4 = new HashMap<>();
        log4.put("id", "4");
        log4.put("timestamp", LocalDateTime.now().minusHours(4));
        log4.put("level", "INFO");
        log4.put("source", "SystemService");
        log4.put("message", "系统备份完成");
        logs.add(log4);
        
        Map<String, Object> log5 = new HashMap<>();
        log5.put("id", "5");
        log5.put("timestamp", LocalDateTime.now().minusHours(5));
        log5.put("level", "INFO");
        log5.put("source", "ImageService");
        log5.put("message", "新的医学影像上传: patient_001.dcm");
        logs.add(log5);
        
        return logs;
    }
    
    private List<Map<String, Object>> generateMockErrorLogs() {
        List<Map<String, Object>> errorLogs = new ArrayList<>();
        
        Map<String, Object> error1 = new HashMap<>();
        error1.put("id", "1");
        error1.put("timestamp", LocalDateTime.now().minusDays(1));
        error1.put("source", "DatabaseService");
        error1.put("message", "数据库连接超时");
        error1.put("stackTrace", "java.sql.SQLException: Connection timed out...");
        errorLogs.add(error1);
        
        Map<String, Object> error2 = new HashMap<>();
        error2.put("id", "2");
        error2.put("timestamp", LocalDateTime.now().minusDays(2));
        error2.put("source", "FileService");
        error2.put("message", "无法创建临时文件");
        error2.put("stackTrace", "java.io.IOException: Permission denied...");
        errorLogs.add(error2);
        
        Map<String, Object> error3 = new HashMap<>();
        error3.put("id", "3");
        error3.put("timestamp", LocalDateTime.now().minusDays(3));
        error3.put("source", "ImageProcessingService");
        error3.put("message", "影像处理失败");
        error3.put("stackTrace", "java.lang.OutOfMemoryError: Java heap space...");
        errorLogs.add(error3);
        
        return errorLogs;
    }
    
    private List<Map<String, Object>> generatePerformanceTrend() {
        List<Map<String, Object>> trend = new ArrayList<>();
        
        Map<String, Object> point1 = new HashMap<>();
        point1.put("timestamp", LocalDateTime.now().minusHours(1));
        point1.put("responseTime", 230);
        point1.put("requestCount", 45);
        point1.put("errorCount", 0);
        trend.add(point1);
        
        Map<String, Object> point2 = new HashMap<>();
        point2.put("timestamp", LocalDateTime.now().minusHours(2));
        point2.put("responseTime", 245);
        point2.put("requestCount", 42);
        point2.put("errorCount", 1);
        trend.add(point2);
        
        Map<String, Object> point3 = new HashMap<>();
        point3.put("timestamp", LocalDateTime.now().minusHours(3));
        point3.put("responseTime", 228);
        point3.put("requestCount", 38);
        point3.put("errorCount", 0);
        trend.add(point3);
        
        Map<String, Object> point4 = new HashMap<>();
        point4.put("timestamp", LocalDateTime.now().minusHours(4));
        point4.put("responseTime", 250);
        point4.put("requestCount", 35);
        point4.put("errorCount", 0);
        trend.add(point4);
        
        return trend;
    }
    
    // TODO: 添加其他管理员接口，如用户管理、医生管理、患者管理等
} 