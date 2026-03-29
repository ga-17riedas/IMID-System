package com.example.medicalsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.medicalsystem.dto.SystemLogDTO;
import com.example.medicalsystem.model.SystemLog;
import com.example.medicalsystem.model.SystemLog.LogLevel;
import com.example.medicalsystem.service.SystemLogService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统日志控制器
 * 
 * 提供系统日志管理相关的API接口，包括日志查询、添加和清除等功能。
 * 处理来自前端的日志操作请求，调用日志服务实现具体的业务逻辑。
 * 支持日志级别和日期范围筛选，以及分页查询。
 * 提供最近日志和错误日志的快速访问接口。
 * 
 * @author Medical System Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/system/logs")
public class SystemLogController {

    /**
     * 系统日志服务
     * 处理日志相关的业务逻辑
     */
    @Autowired
    private SystemLogService systemLogService;
    
    /**
     * 获取系统日志列表
     * 
     * 支持根据日志级别和日期范围筛选，以及分页查询。
     * 将服务层返回的实体对象转换为DTO后再返回给前端。
     * 
     * @param level 日志级别，可选参数
     * @param startDate 开始日期，可选参数
     * @param endDate 结束日期，可选参数
     * @param pageable 分页参数
     * @return 包含分页日志数据的响应
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getLogs(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        
        // 调用服务层获取系统日志
        Page<SystemLog> logsPage = systemLogService.getSystemLogs(level, startDate, endDate, pageable);
        
        // 转换为DTO
        List<SystemLogDTO> logDtos = logsPage.getContent().stream()
                .map(SystemLogDTO::fromEntity)
                .collect(Collectors.toList());
        
        Page<SystemLogDTO> dtoPage = new PageImpl<>(
                logDtos, 
                logsPage.getPageable(), 
                logsPage.getTotalElements()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", dtoPage);
        response.put("message", "获取系统日志成功");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取最近的系统日志
     * 
     * 获取最近产生的指定数量的系统日志，无需分页。
     * 常用于实时监控和仪表盘显示最新的系统活动。
     * 
     * @param limit 要返回的日志数量，默认为10条
     * @return 包含最近日志数据的响应
     */
    @GetMapping("/recent")
    public ResponseEntity<Map<String, Object>> getRecentLogs(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<SystemLog> logs = systemLogService.getRecentLogs(limit);
        
        // 转换为DTO
        List<SystemLogDTO> logDtos = logs.stream()
                .map(SystemLogDTO::fromEntity)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", logDtos);
        response.put("message", "获取最近日志成功");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取错误日志
     * 
     * 获取所有ERROR级别的系统日志。
     * 用于快速查看系统中的错误情况，进行故障诊断。
     * 
     * @return 包含错误日志数据的响应
     */
    @GetMapping("/errors")
    public ResponseEntity<Map<String, Object>> getErrorLogs() {
        
        List<SystemLog> logs = systemLogService.getErrorLogs();
        
        // 转换为DTO
        List<SystemLogDTO> logDtos = logs.stream()
                .map(SystemLogDTO::fromEntity)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", logDtos);
        response.put("message", "获取错误日志成功");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 添加系统日志
     * 
     * 处理添加日志的请求，自动补充未提供的信息（如时间戳、IP地址和用户代理）。
     * 使用完整的日志记录方法存储日志信息，支持详细的日志内容和元数据。
     * 
     * @param logDto 日志数据传输对象
     * @param request HTTP请求对象，用于获取IP地址和用户代理信息
     * @return 操作结果响应
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addLog(
            @RequestBody SystemLogDTO logDto,
            HttpServletRequest request) {
        
        // 转换为实体并保存
        try {
            // 如果未提供时间戳，使用当前时间
            if (logDto.getTimestamp() == null) {
                logDto.setTimestamp(LocalDateTime.now());
            }
            
            // 自动填充IP地址和用户代理信息
            if (logDto.getIpAddress() == null || logDto.getIpAddress().isEmpty()) {
                logDto.setIpAddress(getClientIp(request));
            }
            
            if (logDto.getUserAgent() == null || logDto.getUserAgent().isEmpty()) {
                logDto.setUserAgent(request.getHeader("User-Agent"));
            }
            
            // 使用完整版的日志记录方法
            systemLogService.logFull(
                LogLevel.valueOf(logDto.getLevel()),
                logDto.getSource(),
                logDto.getMessage(),
                logDto.getStackTrace(),
                logDto.getUserId(),
                logDto.getIpAddress(),
                logDto.getUserAgent(),
                logDto.getDetails()
            );
            
            // 记录一条成功日志
            systemLogService.log(
                LogLevel.INFO, 
                "SystemLogController", 
                "日志添加成功: " + logDto.getMessage(), 
                "通过API添加日志"
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "日志添加成功");
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 记录错误
            systemLogService.log(
                LogLevel.ERROR, 
                "SystemLogController", 
                "日志添加失败: " + e.getMessage(), 
                e.toString()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "日志添加失败: " + e.getMessage());
            response.put("status", "error");
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 清除系统日志
     * 
     * 根据指定的条件（日志级别、时间范围）清除系统日志。
     * 操作完成后记录清除操作的结果，包括删除的日志数量。
     * 
     * @param params 包含日志级别、开始日期和结束日期的参数
     * @return 操作结果响应
     */
    @PostMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearLogs(@RequestBody Map<String, String> params) {
        
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
        
        // 记录清除操作
        systemLogService.log(
            LogLevel.INFO, 
            "SystemLogController", 
            String.format("清除了%d条日志记录", deletedCount),
            String.format("级别: %s, 开始时间: %s, 结束时间: %s", 
                levelStr != null ? levelStr : "全部", 
                startDate != null ? startDate.toString() : "无限制",
                endDate != null ? endDate.toString() : "无限制")
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", String.format("成功清除%d条日志记录", deletedCount));
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取客户端IP地址
     * 
     * 从HTTP请求中提取客户端的真实IP地址。
     * 考虑了代理服务器转发的情况，尝试从多个HTTP头字段中获取IP。
     * 
     * @param request HTTP请求
     * @return 客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        
        // 对于通过多个代理的情况，第一个IP为客户端真实IP
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        
        return ipAddress;
    }
    
    /**
     * 获取最新的系统日志
     * 
     * 获取最新的N条系统日志，默认为500条，支持过滤特定级别的日志。
     * 主要用于实时监控页面，支持定期刷新更新。
     * 
     * @param level 日志级别，可选参数
     * @param limit 限制返回的日志数量，默认500条
     * @return 包含最新日志数据的响应
     */
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestLogs(
            @RequestParam(required = false) String level,
            @RequestParam(defaultValue = "500") int limit) {
        
        List<SystemLog> logs = systemLogService.getLatestLogs(level, limit);
        
        // 转换为DTO
        List<SystemLogDTO> logDtos = logs.stream()
                .map(SystemLogDTO::fromEntity)
                .collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("data", logDtos);
        response.put("message", "获取最新日志成功");
        response.put("status", "success");
        
        return ResponseEntity.ok(response);
    }
} 