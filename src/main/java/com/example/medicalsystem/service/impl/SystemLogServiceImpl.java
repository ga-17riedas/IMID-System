package com.example.medicalsystem.service.impl;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.medicalsystem.model.SystemLog;
import com.example.medicalsystem.model.SystemLog.LogLevel;
import com.example.medicalsystem.repository.SystemLogRepository;
import com.example.medicalsystem.service.SystemLogService;

/**
 * 系统日志服务实现类
 */
@Service
public class SystemLogServiceImpl implements SystemLogService {

    private static final Logger logger = LoggerFactory.getLogger(SystemLogServiceImpl.class);
    private static final int MAX_MEMORY_LOGS = 500; // 内存中最多保存500条日志
    // 使用线程安全的队列存储最近的日志
    private final ConcurrentLinkedQueue<SystemLog> recentLogs = new ConcurrentLinkedQueue<>();
    
    @Autowired
    private SystemLogRepository systemLogRepository;
    
    @Override
    public void log(LogLevel level, String source, String message) {
        SystemLog log = new SystemLog(LocalDateTime.now(), level, source, message);
        // 保存到数据库
        systemLogRepository.save(log);
        // 同时添加到内存队列
        addToRecentLogs(log);
        // 输出到控制台日志
        logToConsole(log);
    }

    @Override
    public void log(LogLevel level, String source, String message, String details) {
        SystemLog log = new SystemLog(LocalDateTime.now(), level, source, message, details);
        // 保存到数据库
        systemLogRepository.save(log);
        // 同时添加到内存队列
        addToRecentLogs(log);
        // 输出到控制台日志
        logToConsole(log);
    }
    
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
    public void logFull(LogLevel level, String source, String message, 
                         String stackTrace, Long userId, String ipAddress, 
                         String userAgent, String details) {
        SystemLog log = new SystemLog(
            LocalDateTime.now(), level, source, message, 
            stackTrace, userId, ipAddress, userAgent, details
        );
        // 保存到数据库
        systemLogRepository.save(log);
        // 同时添加到内存队列
        addToRecentLogs(log);
        // 输出到控制台日志
        logToConsole(log);
    }
    
    @Override
    public void logUserAction(String action, String message) {
        // 简化实现，避免依赖Spring Security上下文
        log(LogLevel.INFO, "USER_ACTION", action + ": " + message);
        
        // 记录到控制台
        logger.info("用户操作: [{}] {}", action, message);
    }
    
    /**
     * 输出日志到控制台
     * @param log 日志对象
     */
    private void logToConsole(SystemLog log) {
        String logMessage = String.format("[%s] %s: %s", 
                log.getLevel(), log.getSource(), log.getMessage());
        
        switch (log.getLevel()) {
            case ERROR:
                logger.error(logMessage);
                break;
            case WARNING:
                logger.warn(logMessage);
                break;
            case INFO:
                logger.info(logMessage);
                break;
            case DEBUG:
                logger.debug(logMessage);
                break;
        }
    }

    /**
     * 添加日志到内存队列，并保持队列大小不超过最大限制
     */
    private void addToRecentLogs(SystemLog log) {
        // 添加新日志
        recentLogs.add(log);
        
        // 如果队列超过大小限制，移除最旧的日志
        while (recentLogs.size() > MAX_MEMORY_LOGS) {
            recentLogs.poll();
        }
    }

    @Override
    public Page<SystemLog> getSystemLogs(String level, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        // 如果没有指定筛选条件，返回所有日志
        if (level == null && startDate == null && endDate == null) {
            return systemLogRepository.findAll(pageable);
        }
        
        // 处理未指定开始日期或结束日期的情况
        LocalDateTime effectiveStartDate = startDate != null ? startDate : LocalDateTime.now().minusYears(100);
        LocalDateTime effectiveEndDate = endDate != null ? endDate : LocalDateTime.now();
        
        // 根据指定的筛选条件进行查询
        if (level != null && !level.isEmpty()) {
            try {
                LogLevel logLevel = LogLevel.valueOf(level.toUpperCase());
                return systemLogRepository.findByLevelAndTimestampBetween(
                    logLevel, effectiveStartDate, effectiveEndDate, pageable);
            } catch (IllegalArgumentException e) {
                // 如果日志级别无效，则只按时间范围筛选
                return systemLogRepository.findByTimestampBetween(
                    effectiveStartDate, effectiveEndDate, pageable);
            }
        } else {
            // 只按时间范围筛选
            return systemLogRepository.findByTimestampBetween(
                effectiveStartDate, effectiveEndDate, pageable);
        }
    }

    @Override
    public List<SystemLog> getRecentLogs(int limit) {
        // 优先从内存中获取最近的日志
        if (!recentLogs.isEmpty()) {
            logger.debug("Returning {} recent logs from memory cache", Math.min(limit, recentLogs.size()));
            
            // 将队列转换为列表，并按时间倒序排序
            return recentLogs.stream()
                    .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                    .limit(limit)
                    .collect(Collectors.toList());
        }
        
        // 如果内存中没有日志，则从数据库中查询
        logger.debug("No logs in memory cache, querying database for {} recent logs", limit);
        
        // 使用排序确保最新的日志显示在前面
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<SystemLog> page = systemLogRepository.findAll(pageable);
        return page.getContent();
    }

    @Override
    public List<SystemLog> getErrorLogs() {
        // 优先从内存中筛选错误日志
        if (!recentLogs.isEmpty()) {
            List<SystemLog> errors = recentLogs.stream()
                    .filter(log -> log.getLevel() == LogLevel.ERROR)
                    .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                    .collect(Collectors.toList());
            
            if (!errors.isEmpty()) {
                logger.debug("Returning {} error logs from memory cache", errors.size());
                return errors;
            }
        }
        
        // 如果内存中没有错误日志，从数据库中查询
        logger.debug("No error logs in memory cache, querying database");
        return systemLogRepository.findByLevelOrderByTimestampDesc(LogLevel.ERROR);
    }

    @Override
    @Transactional
    public int clearSystemLogs(String level, LocalDateTime startDate, LocalDateTime endDate) {
        // 处理未指定开始日期或结束日期的情况
        LocalDateTime effectiveStartDate = startDate != null ? startDate : LocalDateTime.now().minusYears(100);
        LocalDateTime effectiveEndDate = endDate != null ? endDate : LocalDateTime.now();
        
        int deletedCount = 0;
        
        // 根据指定的筛选条件进行删除
        if (level != null && !level.isEmpty()) {
            try {
                LogLevel logLevel = LogLevel.valueOf(level.toUpperCase());
                deletedCount = systemLogRepository.deleteByLevelAndTimestampBetween(
                    logLevel, effectiveStartDate, effectiveEndDate);
            } catch (IllegalArgumentException e) {
                // 如果日志级别无效，则只按时间范围删除
                deletedCount = systemLogRepository.deleteByTimestampBetween(
                    effectiveStartDate, effectiveEndDate);
            }
        } else {
            // 只按时间范围删除
            deletedCount = systemLogRepository.deleteByTimestampBetween(
                effectiveStartDate, effectiveEndDate);
        }
        
        // 同时清除内存中的相关日志
        clearMemoryLogs(level, effectiveStartDate, effectiveEndDate);
        
        return deletedCount;
    }
    
    /**
     * 清除内存中符合条件的日志
     */
    private void clearMemoryLogs(String level, LocalDateTime startDate, LocalDateTime endDate) {
        if (level != null && !level.isEmpty()) {
            try {
                LogLevel logLevel = LogLevel.valueOf(level.toUpperCase());
                recentLogs.removeIf(log -> 
                    log.getLevel() == logLevel && 
                    !log.getTimestamp().isBefore(startDate) && 
                    !log.getTimestamp().isAfter(endDate)
                );
            } catch (IllegalArgumentException e) {
                // 日志级别无效，只按时间范围清除
                recentLogs.removeIf(log -> 
                    !log.getTimestamp().isBefore(startDate) && 
                    !log.getTimestamp().isAfter(endDate)
                );
            }
        } else {
            // 只按时间范围清除
            recentLogs.removeIf(log -> 
                !log.getTimestamp().isBefore(startDate) && 
                !log.getTimestamp().isAfter(endDate)
            );
        }
    }

    /**
     * 定期从数据库加载最近的日志到内存
     * 每5秒执行一次，保持内存中的日志与数据库同步
     */
    @Scheduled(fixedRate = 5 * 1000)
    public void loadRecentLogsFromDatabase() {
        try {
            logger.debug("正在从数据库加载最新日志...");
            
            // 查询最近的MAX_MEMORY_LOGS条日志
            Pageable pageable = PageRequest.of(0, MAX_MEMORY_LOGS, Sort.by(Sort.Direction.DESC, "timestamp"));
            Page<SystemLog> logPage = systemLogRepository.findAll(pageable);
            
            // 清空当前内存中的日志
            recentLogs.clear();
            
            // 添加到内存队列
            for (SystemLog log : logPage.getContent()) {
                recentLogs.add(log);
            }
            
            logger.debug("已从数据库加载 {} 条日志", recentLogs.size());
        } catch (Exception e) {
            logger.error("从数据库加载日志出错: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 定期从应用程序日志文件中读取并保存到数据库
     * 每5秒执行一次，确保系统日志实时存入数据库
     */
    @Scheduled(fixedRate = 5 * 1000)
    public void importLogsFromFile() {
        try {
            logger.debug("开始从应用日志文件导入日志到数据库");
            
            // 从Spring Boot应用的日志文件路径
            String logFilePath = System.getProperty("logging.file.path");
            if (logFilePath == null) {
                logFilePath = "./logs"; // 默认日志路径
            }
            
            String logFileName = System.getProperty("logging.file.name");
            if (logFileName == null) {
                logFileName = "spring.log"; // 默认日志文件名
            }
            
            File logFile = new File(logFilePath, logFileName);
            if (!logFile.exists()) {
                // 尝试标准的日志位置
                logFile = new File("./logs/spring.log");
                if (!logFile.exists()) {
                    logger.warn("找不到日志文件: {}", logFile.getAbsolutePath());
                    return;
                }
            }
            
            // 获取上次读取的位置
            Long lastPosition = getLastProcessedPosition();
            
            // 使用RandomAccessFile从上次位置开始读取
            try (RandomAccessFile reader = new RandomAccessFile(logFile, "r")) {
                if (lastPosition != null) {
                    reader.seek(lastPosition);
                } else {
                    // 首次读取，从文件末尾开始
                    reader.seek(Math.max(0, reader.length() - 4096)); // 读取最后4KB
                }
                
                String line;
                int importedCount = 0;
                List<SystemLog> newLogs = new ArrayList<>();
                
                while ((line = reader.readLine()) != null) {
                    // 解析日志行
                    SystemLog log = parseLogLine(line);
                    if (log != null) {
                        newLogs.add(log);
                        importedCount++;
                    }
                    
                    // 限制每次导入数量，避免过多日志导致性能问题
                    if (importedCount >= 100) {
                        break;
                    }
                }
                
                // 批量保存解析的日志
                if (!newLogs.isEmpty()) {
                    systemLogRepository.saveAll(newLogs);
                    logger.debug("成功导入 {} 条日志到数据库", newLogs.size());
                }
                
                // 保存当前读取位置
                saveLastProcessedPosition(reader.getFilePointer());
            }
        } catch (Exception e) {
            logger.error("导入日志文件到数据库时出错: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 解析单行日志文本
     * @param line 日志行文本
     * @return 解析后的SystemLog对象，如果无法解析则返回null
     */
    private SystemLog parseLogLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 解析日志行，格式示例：
            // 2023-05-14 10:15:30.123 INFO [thread-name] com.example.Class : 日志消息
            
            // 提取日期时间
            Matcher dateMatcher = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3})").matcher(line);
            if (!dateMatcher.find()) {
                return null;
            }
            String dateTimeStr = dateMatcher.group(1);
            LocalDateTime timestamp = LocalDateTime.parse(
                dateTimeStr, 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
            );
            
            // 提取日志级别
            Matcher levelMatcher = Pattern.compile("(INFO|ERROR|WARN|DEBUG)").matcher(line);
            if (!levelMatcher.find()) {
                return null;
            }
            String levelStr = levelMatcher.group(1);
            LogLevel level;
            switch (levelStr) {
                case "ERROR": level = LogLevel.ERROR; break;
                case "WARN": level = LogLevel.WARNING; break;
                case "INFO": level = LogLevel.INFO; break;
                case "DEBUG": level = LogLevel.DEBUG; break;
                default: level = LogLevel.INFO;
            }
            
            // 提取源
            Matcher sourceMatcher = Pattern.compile("\\[(.*?)\\]\\s+([\\w.]+)\\s*:").matcher(line);
            String source = "System";
            if (sourceMatcher.find()) {
                source = sourceMatcher.group(2);
            }
            
            // 提取消息内容
            String message = line;
            int messageIndex = line.indexOf(" : ");
            if (messageIndex > 0) {
                message = line.substring(messageIndex + 3);
            }
            
            // 创建系统日志对象
            return new SystemLog(timestamp, level, source, message);
        } catch (Exception e) {
            logger.warn("解析日志行失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 获取上次处理的文件位置
     */
    private Long getLastProcessedPosition() {
        // 这里简化实现，实际应用中可以存储在数据库或缓存中
        File positionFile = new File("./logs/last_position.txt");
        if (!positionFile.exists()) {
            return null;
        }
        
        try {
            String content = new String(Files.readAllBytes(positionFile.toPath()));
            return Long.parseLong(content.trim());
        } catch (Exception e) {
            logger.warn("读取上次处理位置失败: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 保存最后处理的文件位置
     */
    private void saveLastProcessedPosition(long position) {
        try {
            File directory = new File("./logs");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            File positionFile = new File("./logs/last_position.txt");
            Files.write(positionFile.toPath(), String.valueOf(position).getBytes());
        } catch (Exception e) {
            logger.warn("保存处理位置失败: {}", e.getMessage());
        }
    }

    @Override
    public List<SystemLog> getLatestLogs(String level, int limit) {
        // 限制最大返回500条记录
        int effectiveLimit = Math.min(limit, 500);
        
        if (level != null && !level.isEmpty()) {
            try {
                // 直接使用字符串级别以避免异常
                return systemLogRepository.findLatestLogsByLevel(level, effectiveLimit);
            } catch (Exception e) {
                logger.error("Error getting latest logs by level: {}", e.getMessage());
                return systemLogRepository.findLatestLogs(effectiveLimit);
            }
        } else {
            return systemLogRepository.findLatestLogs(effectiveLimit);
        }
    }
} 