package com.example.medicalsystem.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.medicalsystem.model.SystemLog;
import com.example.medicalsystem.model.SystemLog.LogLevel;

/**
 * 系统日志服务接口
 */
public interface SystemLogService {
    
    /**
     * 记录日志
     * @param level 日志级别
     * @param source 日志来源
     * @param message 日志消息
     */
    void log(LogLevel level, String source, String message);
    
    /**
     * 记录带详情的日志
     * @param level 日志级别
     * @param source 日志来源
     * @param message 日志消息
     * @param details 详细信息
     */
    void log(LogLevel level, String source, String message, String details);
    
    /**
     * 完整版日志记录方法
     * @param level 日志级别
     * @param source 来源
     * @param message 消息
     * @param stackTrace 堆栈跟踪
     * @param userId 用户ID
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     * @param details 详细信息
     */
    void logFull(LogLevel level, String source, String message, 
                String stackTrace, Long userId, String ipAddress, 
                String userAgent, String details);
    
    /**
     * 记录用户操作日志
     * @param action 操作类型
     * @param message 操作描述
     */
    void logUserAction(String action, String message);
    
    /**
     * 获取系统日志
     * @param level 日志级别
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param pageable 分页参数
     * @return 分页的系统日志数据
     */
    Page<SystemLog> getSystemLogs(String level, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * 获取最近的系统日志
     * @param limit 数量限制
     * @return 系统日志列表
     */
    List<SystemLog> getRecentLogs(int limit);
    
    /**
     * 获取错误日志
     * @return 错误日志列表
     */
    List<SystemLog> getErrorLogs();
    
    /**
     * 获取最新的N条日志（不超过500条）
     * @param level 日志级别，可为null表示所有级别
     * @param limit 获取的数量上限，最大500
     * @return 最新的日志列表
     */
    List<SystemLog> getLatestLogs(String level, int limit);
    
    /**
     * 清除系统日志
     * @param level 日志级别
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 删除的记录数
     */
    int clearSystemLogs(String level, LocalDateTime startDate, LocalDateTime endDate);
} 