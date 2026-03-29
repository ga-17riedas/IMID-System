package com.example.medicalsystem.repository;

import com.example.medicalsystem.model.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 系统设置仓库接口
 * 提供对system_settings表的数据访问操作
 */
@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, Integer> {
    
    /**
     * 根据设置键查找设置
     * @param key 设置键
     * @return 设置项（可选）
     */
    Optional<SystemSetting> findByKey(String key);
    
    /**
     * 根据设置组查找设置
     * @param group 设置组
     * @return 设置列表
     */
    List<SystemSetting> findByGroup(String group);
    
    /**
     * 根据设置组查找设置，并按排序号排序
     * @param group 设置组
     * @return 排序后的设置列表
     */
    List<SystemSetting> findByGroupOrderByOrderNumAsc(String group);
    
    /**
     * 查询所有设置，并按组和排序号排序
     * @return 排序后的设置列表
     */
    List<SystemSetting> findAllByOrderByGroupAscOrderNumAsc();
    
    /**
     * 根据键删除设置
     * @param key 设置键
     * @return 影响的行数
     */
    void deleteByKey(String key);
    
    /**
     * 检查键是否存在
     * @param key 设置键
     * @return 是否存在
     */
    boolean existsByKey(String key);
    
    /**
     * 获取所有设置组
     * @return 设置组列表
     */
    @Query("SELECT DISTINCT s.group FROM SystemSetting s")
    List<String> findDistinctGroup();
} 