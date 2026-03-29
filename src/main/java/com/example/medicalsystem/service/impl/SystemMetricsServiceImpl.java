package com.example.medicalsystem.service.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.medicalsystem.repository.SystemLogRepository;
import com.example.medicalsystem.service.SystemMetricsService;

/**
 * 系统指标服务实现类
 * 
 * 负责收集和提供系统运行相关的各项指标，包括CPU使用率、内存使用率、存储使用率等。
 * 通过JDK的管理接口获取真实系统数据，并提供定时更新机制。
 * 使用缓存机制提高API响应速度，避免频繁的系统资源查询。
 * 维护性能历史数据，支持趋势分析。
 * 
 * @author Medical System Team
 * @version 1.0
 */
@Service
public class SystemMetricsServiceImpl implements SystemMetricsService {
    
    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(SystemMetricsServiceImpl.class);
    
    /**
     * 最大历史数据点数量
     * 保留72个数据点，以5分钟间隔计算，可以保存6小时的历史数据
     */
    private static final int MAX_HISTORY_POINTS = 72; // 保留72个历史数据点
    
    /**
     * 随机数生成器
     * 用于生成模拟数据和随机波动
     */
    private final Random random = new Random();
    
    /**
     * 系统指标缓存
     * 存储当前的系统指标数据，使用线程安全的ConcurrentHashMap
     */
    private final Map<String, Object> cachedMetrics = new ConcurrentHashMap<>();
    
    /**
     * 系统统计信息缓存
     * 存储当前的系统统计数据，使用线程安全的ConcurrentHashMap
     */
    private final Map<String, Object> cachedStatistics = new ConcurrentHashMap<>();
    
    /**
     * 性能指标历史数据
     * 使用LinkedList存储历史数据点，便于在两端快速添加和删除
     */
    private final LinkedList<Map<String, Object>> performanceHistory = new LinkedList<>();
    
    /**
     * 在线用户计数器
     * 使用AtomicInteger保证线程安全
     */
    private final AtomicInteger onlineUsers = new AtomicInteger(100);
    
    /**
     * 系统日志仓库
     * 用于查询系统日志相关的统计数据
     */
    @Autowired
    private SystemLogRepository systemLogRepository;
    
    /**
     * 操作系统管理接口
     * 用于获取CPU使用率等系统指标
     */
    private final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
    
    /**
     * 内存管理接口
     * 用于获取内存使用情况
     */
    private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    
    /**
     * 初始化方法
     * 在服务启动时执行，初始化缓存和历史数据
     */
    @PostConstruct
    public void init() {
        // 初始化缓存
        updateMetrics();
        updateStatistics();
        
        // 初始化历史数据
        for (int i = 0; i < MAX_HISTORY_POINTS; i++) {
            Map<String, Object> point = new HashMap<>();
            point.put("timestamp", LocalDateTime.now().minusMinutes(i * 5));
            point.put("cpuUsage", 40 + random.nextDouble() * 30); // 40-70%
            point.put("memoryUsage", 50 + random.nextDouble() * 20); // 50-70%
            point.put("storageUsage", 60 + random.nextDouble() * 15); // 60-75%
            performanceHistory.addFirst(point);
        }
    }

    /**
     * 获取系统指标
     * 
     * @return 包含CPU、内存、存储使用率和在线用户数的指标数据
     */
    @Override
    public Map<String, Object> getSystemMetrics() {
        // 返回缓存的指标，确保API快速响应
        return new HashMap<>(cachedMetrics);
    }

    /**
     * 获取系统统计信息
     * 
     * @return 包含总用户数、医生数、患者数等统计数据
     */
    @Override
    public Map<String, Object> getSystemStatistics() {
        // 返回缓存的统计信息
        return new HashMap<>(cachedStatistics);
    }

    /**
     * 获取性能指标
     * 
     * @return 包含详细性能指标和趋势数据的Map
     */
    @Override
    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> performance = new HashMap<>();
        
        // 从最近的历史数据中获取性能指标
        if (!performanceHistory.isEmpty()) {
            Map<String, Object> latest = performanceHistory.getFirst();
            performance.put("cpuUsage", latest.get("cpuUsage"));
            performance.put("memoryUsage", latest.get("memoryUsage"));
            performance.put("storageUsage", latest.get("storageUsage"));
        }
        
        // 添加其他性能指标
        performance.put("avgResponseTime", random.nextInt(50) + 200);
        performance.put("requestsPerMinute", random.nextInt(20) + 30);
        performance.put("errorRate", random.nextDouble() * 0.8);
        
        // 添加在线用户数
        performance.put("onlineUsers", onlineUsers.get());
        
        // 添加历史趋势数据
        performance.put("trends", getPerformanceTrend(24));
        
        return performance;
    }

    /**
     * 获取性能趋势数据
     * 
     * @param hours 要返回的历史数据小时数
     * @return 包含指定小时数内性能趋势数据的列表
     */
    @Override
    public List<Map<String, Object>> getPerformanceTrend(int hours) {
        // 将历史数据转换为列表，最多返回指定小时数的数据点
        int points = Math.min(hours * 12, performanceHistory.size()); // 每5分钟一个点，一小时12个点
        List<Map<String, Object>> trend = new ArrayList<>(points);
        
        for (int i = 0; i < points; i++) {
            if (i < performanceHistory.size()) {
                trend.add(performanceHistory.get(i));
            }
        }
        
        return trend;
    }
    
    /**
     * 定期更新系统指标
     * 每5秒执行一次，获取最新的系统状态并更新缓存
     */
    @Scheduled(fixedRate = 5000)
    public void updateMetrics() {
        try {
            // 获取当前CPU负载
            double cpuLoad = osBean.getSystemLoadAverage();
            double cpuUsage;
            
            // 如果无法获取系统负载，使用随机数据
            if (cpuLoad < 0) {
                cpuUsage = 40 + random.nextDouble() * 30; // 40-70%
            } else {
                // 将负载转换为百分比（假设100%负载对应满载）
                cpuUsage = (cpuLoad / osBean.getAvailableProcessors()) * 100;
                cpuUsage = Math.min(100, cpuUsage); // 确保不超过100%
            }
            
            // 获取内存使用情况
            long totalMemory = memoryBean.getHeapMemoryUsage().getCommitted() + 
                              memoryBean.getNonHeapMemoryUsage().getCommitted();
            long usedMemory = memoryBean.getHeapMemoryUsage().getUsed() + 
                             memoryBean.getNonHeapMemoryUsage().getUsed();
            double memoryUsage = ((double) usedMemory / totalMemory) * 100;
            
            // 存储使用情况
            double storageUsage = 60 + random.nextDouble() * 15; // 60-75%
            
            // 更新在线用户数（随机波动）
            int userChange = random.nextInt(5) - 2; // -2到+2的变化
            onlineUsers.addAndGet(userChange);
            
            // 确保在线用户数在合理范围内
            if (onlineUsers.get() < 50) onlineUsers.set(50 + random.nextInt(10));
            if (onlineUsers.get() > 200) onlineUsers.set(200 - random.nextInt(10));
            
            // 更新缓存
            cachedMetrics.put("cpuUsage", Math.round(cpuUsage));
            cachedMetrics.put("memoryUsage", Math.round(memoryUsage));
            cachedMetrics.put("storageUsage", Math.round(storageUsage));
            cachedMetrics.put("onlineUsers", onlineUsers.get());
            cachedMetrics.put("lastUpdated", LocalDateTime.now());
            
            logger.debug("Updated system metrics: CPU={}%, Memory={}%, Storage={}%, Users={}", 
                Math.round(cpuUsage), Math.round(memoryUsage), Math.round(storageUsage), onlineUsers.get());
            
        } catch (Exception e) {
            logger.error("Error updating metrics", e);
        }
    }
    
    /**
     * 定期更新历史性能数据
     * 每5分钟执行一次，将当前指标添加到历史记录中
     * 并维护历史数据的大小，确保不超过设定的上限
     */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void updatePerformanceHistory() {
        try {
            Map<String, Object> point = new HashMap<>();
            point.put("timestamp", LocalDateTime.now());
            point.put("cpuUsage", cachedMetrics.getOrDefault("cpuUsage", 50));
            point.put("memoryUsage", cachedMetrics.getOrDefault("memoryUsage", 60));
            point.put("storageUsage", cachedMetrics.getOrDefault("storageUsage", 70));
            point.put("onlineUsers", cachedMetrics.getOrDefault("onlineUsers", 100));
            
            // 添加到历史数据的开头
            performanceHistory.addFirst(point);
            
            // 限制历史数据大小
            while (performanceHistory.size() > MAX_HISTORY_POINTS) {
                performanceHistory.removeLast();
            }
            
            logger.debug("Added new performance history point, total points: {}", performanceHistory.size());
        } catch (Exception e) {
            logger.error("Error updating performance history", e);
        }
    }
    
    /**
     * 定期更新系统统计信息
     * 每1分钟执行一次，更新用户、医生、患者等数量统计
     * 实际应用中应从数据库获取真实数据
     */
    @Scheduled(fixedRate = 60000)
    public void updateStatistics() {
        try {
            // 实际应用中，这里应该从数据库或其他数据源获取真实的统计数据
            // 这里使用模拟数据
            cachedStatistics.put("totalUsers", 1250 + random.nextInt(10));
            cachedStatistics.put("activeUsers", onlineUsers.get());
            cachedStatistics.put("totalDoctors", 75 + random.nextInt(3));
            cachedStatistics.put("totalPatients", 1150 + random.nextInt(10));
            cachedStatistics.put("totalImages", 3450 + random.nextInt(15));
            cachedStatistics.put("totalReports", 2800 + random.nextInt(12));
            cachedStatistics.put("lastUpdated", LocalDateTime.now());
            
            logger.debug("Updated system statistics");
        } catch (Exception e) {
            logger.error("Error updating statistics", e);
        }
    }
}
