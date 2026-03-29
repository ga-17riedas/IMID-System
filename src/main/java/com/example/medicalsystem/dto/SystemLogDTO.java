package com.example.medicalsystem.dto;

import java.time.LocalDateTime;

import com.example.medicalsystem.model.SystemLog;
import com.example.medicalsystem.model.SystemLog.LogLevel;

/**
 * 系统日志数据传输对象
 * 用于前后端交互，隐藏实体类的具体实现细节
 */
public class SystemLogDTO {
    
    private Long id;
    private LocalDateTime timestamp;
    private String level;
    private String source;
    private String message;
    private String stackTrace;
    private Long userId;
    private String ipAddress;
    private String userAgent;
    private String details;
    
    /**
     * 默认构造函数
     */
    public SystemLogDTO() {
    }
    
    /**
     * 从SystemLog实体转换为DTO
     * @param log SystemLog实体
     * @return SystemLogDTO对象
     */
    public static SystemLogDTO fromEntity(SystemLog log) {
        if (log == null) {
            return null;
        }
        
        SystemLogDTO dto = new SystemLogDTO();
        dto.setId(log.getId());
        dto.setTimestamp(log.getTimestamp());
        dto.setLevel(log.getLevel().name());
        dto.setSource(log.getSource());
        dto.setMessage(log.getMessage());
        dto.setStackTrace(log.getStackTrace());
        dto.setUserId(log.getUserId());
        dto.setIpAddress(log.getIpAddress());
        dto.setUserAgent(log.getUserAgent());
        dto.setDetails(log.getDetails());
        
        return dto;
    }
    
    /**
     * 转换为SystemLog实体
     * @return SystemLog实体
     */
    public SystemLog toEntity() {
        SystemLog log = new SystemLog();
        log.setId(this.id);
        
        if (this.timestamp != null) {
            log.setTimestamp(this.timestamp);
        } else {
            log.setTimestamp(LocalDateTime.now());
        }
        
        try {
            log.setLevel(LogLevel.valueOf(this.level));
        } catch (Exception e) {
            log.setLevel(LogLevel.INFO);
        }
        
        log.setSource(this.source);
        log.setMessage(this.message);
        log.setStackTrace(this.stackTrace);
        log.setUserId(this.userId);
        log.setIpAddress(this.ipAddress);
        log.setUserAgent(this.userAgent);
        log.setDetails(this.details);
        
        return log;
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
} 