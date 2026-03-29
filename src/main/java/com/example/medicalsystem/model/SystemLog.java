package com.example.medicalsystem.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 系统日志实体类
 * 
 * 用于记录系统运行过程中的各种日志信息，包括调试信息、错误信息等。
 * 日志按级别分为DEBUG、INFO、WARNING、ERROR四种类型。
 * 存储在system_logs表中，支持查询和统计分析。
 * 
 * @author Medical System Team
 * @version 1.0
 */
@Entity
@Table(name = "system_logs")
public class SystemLog {

    /**
     * 日志ID，主键，自增长
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 日志记录时间戳
     * 记录日志产生的确切时间
     */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /**
     * 日志级别
     * 使用枚举类型表示日志的严重程度：DEBUG、INFO、WARNING、ERROR
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LogLevel level;

    /**
     * 日志来源
     * 记录产生日志的组件、类或模块名称
     */
    @Column(length = 100)
    private String source;

    /**
     * 日志消息内容
     * 记录日志的主要信息
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    /**
     * 堆栈跟踪信息
     * 当出现异常时记录的堆栈信息，主要用于ERROR级别日志
     */
    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;
    
    /**
     * 用户ID
     * 记录产生日志时的操作用户ID，便于追踪用户相关问题
     */
    @Column(name = "user_id")
    private Long userId;
    
    /**
     * IP地址
     * 记录产生日志时的客户端IP地址
     */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;
    
    /**
     * 用户代理信息
     * 记录产生日志时的浏览器或客户端信息
     */
    @Column(name = "user_agent", length = 255)
    private String userAgent;

    /**
     * 详细信息
     * 用于存储额外的日志详情，如请求参数、上下文信息等
     */
    @Column(length = 4000)
    private String details;

    /**
     * 日志级别枚举
     * 定义系统支持的日志级别类型
     */
    public enum LogLevel {
        /** 调试信息，仅在开发和调试环境使用 */
        DEBUG, 
        /** 普通信息，记录系统正常运行的信息 */
        INFO, 
        /** 警告信息，可能存在的问题但不影响系统运行 */
        WARNING, 
        /** 错误信息，系统运行过程中的错误和异常 */
        ERROR
    }

    /**
     * 默认构造函数
     * 用于JPA实体创建
     */
    public SystemLog() {
    }

    /**
     * 基本日志构造函数
     * 
     * @param timestamp 日志时间戳
     * @param level 日志级别
     * @param source 日志来源
     * @param message 日志消息
     */
    public SystemLog(LocalDateTime timestamp, LogLevel level, String source, String message) {
        this.timestamp = timestamp;
        this.level = level;
        this.source = source;
        this.message = message;
    }

    /**
     * 带详情的日志构造函数
     * 
     * @param timestamp 日志时间戳
     * @param level 日志级别
     * @param source 日志来源
     * @param message 日志消息
     * @param details 详细信息
     */
    public SystemLog(LocalDateTime timestamp, LogLevel level, String source, String message, String details) {
        this.timestamp = timestamp;
        this.level = level;
        this.source = source;
        this.message = message;
        this.details = details;
    }
    
    /**
     * 完整构造函数
     * 包含所有日志字段的完整构造方法
     * 
     * @param timestamp 日志时间戳
     * @param level 日志级别
     * @param source 日志来源
     * @param message 日志消息
     * @param stackTrace 堆栈跟踪信息
     * @param userId 用户ID
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @param details 详细信息
     */
    public SystemLog(LocalDateTime timestamp, LogLevel level, String source, String message, 
                    String stackTrace, Long userId, String ipAddress, String userAgent, String details) {
        this.timestamp = timestamp;
        this.level = level;
        this.source = source;
        this.message = message;
        this.stackTrace = stackTrace;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.details = details;
    }

    // Getters and Setters
    /**
     * 获取日志ID
     * @return 日志ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置日志ID
     * @param id 日志ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取日志时间戳
     * @return 日志时间戳
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * 设置日志时间戳
     * @param timestamp 日志时间戳
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 获取日志级别
     * @return 日志级别
     */
    public LogLevel getLevel() {
        return level;
    }

    /**
     * 设置日志级别
     * @param level 日志级别
     */
    public void setLevel(LogLevel level) {
        this.level = level;
    }

    /**
     * 获取日志来源
     * @return 日志来源
     */
    public String getSource() {
        return source;
    }

    /**
     * 设置日志来源
     * @param source 日志来源
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 获取日志消息
     * @return 日志消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置日志消息
     * @param message 日志消息
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * 获取堆栈跟踪信息
     * @return 堆栈跟踪信息
     */
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * 设置堆栈跟踪信息
     * @param stackTrace 堆栈跟踪信息
     */
    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    /**
     * 获取用户ID
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     * @param userId 用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取IP地址
     * @return IP地址
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * 设置IP地址
     * @param ipAddress IP地址
     */
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * 获取用户代理信息
     * @return 用户代理信息
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * 设置用户代理信息
     * @param userAgent 用户代理信息
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * 获取详细信息
     * @return 详细信息
     */
    public String getDetails() {
        return details;
    }

    /**
     * 设置详细信息
     * @param details 详细信息
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * 重写toString方法
     * 用于日志打印和调试
     * 
     * @return 日志对象的字符串表示
     */
    @Override
    public String toString() {
        return "SystemLog [id=" + id + ", timestamp=" + timestamp + ", level=" + level + ", source=" + source
                + ", message=" + message + "]";
    }
} 