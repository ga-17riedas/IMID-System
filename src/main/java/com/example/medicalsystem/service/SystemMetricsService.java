package com.example.medicalsystem.service;

import java.util.List;
import java.util.Map;

/**
 * 系统指标服务接口
 * 用于获取系统资源使用情况和性能指标
 */
public interface SystemMetricsService {
    
    /**
     * 获取系统指标
     * @return 系统指标数据
     */
    Map<String, Object> getSystemMetrics();
    
    /**
     * 获取系统统计信息
     * @return 系统统计信息
     */
    Map<String, Object> getSystemStatistics();
    
    /**
     * 获取性能指标
     * @return 性能指标数据
     */
    Map<String, Object> getPerformanceMetrics();
    
    /**
     * 获取性能趋势数据
     * @param hours 小时数
     * @return 指定小时数内的性能趋势数据
     */
    List<Map<String, Object>> getPerformanceTrend(int hours);
} 