package com.example.medicalsystem.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.medicalsystem.model.SystemLog;
import com.example.medicalsystem.model.SystemLog.LogLevel;

/**
 * 系统日志数据访问接口
 */
@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

    /**
     * 根据日志级别查找日志
     * @param level 日志级别
     * @param pageable 分页参数
     * @return 分页的日志数据
     */
    Page<SystemLog> findByLevel(LogLevel level, Pageable pageable);
    
    /**
     * 根据时间范围查找日志
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param pageable 分页参数
     * @return 分页的日志数据
     */
    Page<SystemLog> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * 根据日志级别和时间范围查找日志
     * @param level 日志级别
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param pageable 分页参数
     * @return 分页的日志数据
     */
    Page<SystemLog> findByLevelAndTimestampBetween(LogLevel level, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * 查找错误日志
     * @return 错误日志列表
     */
    List<SystemLog> findByLevelOrderByTimestampDesc(LogLevel level);
    
    /**
     * 获取最近的10条日志
     * @return 日志列表
     */
    List<SystemLog> findTop10ByOrderByTimestampDesc();
    
    /**
     * 获取最近的N条日志
     * @param limit 数量限制
     * @return 日志列表
     */
    @Query(value = "SELECT * FROM system_logs ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<SystemLog> findLatestLogs(@Param("limit") int limit);
    
    /**
     * 获取指定级别的最近N条日志
     * @param level 日志级别
     * @param limit 数量限制
     * @return 日志列表
     */
    @Query(value = "SELECT * FROM system_logs WHERE level = :level ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<SystemLog> findLatestLogsByLevel(@Param("level") String level, @Param("limit") int limit);
    
    /**
     * 清除指定时间范围内的日志
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 删除的记录数
     */
    @Modifying
    @Query("DELETE FROM SystemLog s WHERE s.timestamp BETWEEN ?1 AND ?2")
    int deleteByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 清除指定日志级别和时间范围内的日志
     * @param level 日志级别
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return 删除的记录数
     */
    @Modifying
    @Query("DELETE FROM SystemLog s WHERE s.level = ?1 AND s.timestamp BETWEEN ?2 AND ?3")
    int deleteByLevelAndTimestampBetween(LogLevel level, LocalDateTime startDate, LocalDateTime endDate);
} 