package com.example.medicalsystem.service;

import com.example.medicalsystem.dto.SettingDTO;
import com.example.medicalsystem.dto.SettingGroupDTO;
import com.example.medicalsystem.model.SystemSetting;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 系统设置服务接口
 * 定义系统设置相关的业务逻辑操作
 */
public interface SystemSettingsService {
    
    /**
     * 获取所有系统设置
     * @return 系统设置列表
     */
    List<SystemSetting> getAllSettings();
    
    /**
     * 按组获取系统设置
     * @return 按组分类的系统设置
     */
    List<SettingGroupDTO> getSettingsByGroups();
    
    /**
     * 根据键获取系统设置
     * @param key 设置键
     * @return 系统设置
     */
    SystemSetting getSettingByKey(String key);
    
    /**
     * 根据组获取系统设置
     * @param group 设置组
     * @return 设置值映射
     */
    Map<String, Object> getSettingsByGroup(String group);
    
    /**
     * 获取设置值
     * @param key 设置键
     * @param defaultValue 默认值
     * @return 设置值
     */
    String getSettingValue(String key, String defaultValue);
    
    /**
     * 获取Integer设置值
     * @param key 设置键
     * @param defaultValue 默认值
     * @return Integer设置值
     */
    Integer getIntegerValue(String key, Integer defaultValue);
    
    /**
     * 获取Boolean设置值
     * @param key 设置键
     * @param defaultValue 默认值
     * @return Boolean设置值
     */
    Boolean getBooleanValue(String key, Boolean defaultValue);
    
    /**
     * 更新系统设置
     * @param settings 设置列表
     */
    void updateSettings(List<SettingDTO> settings);
    
    /**
     * 更新单个系统设置
     * @param key 设置键
     * @param setting 设置值
     */
    void updateSetting(String key, SettingDTO setting);
    
    /**
     * 保存系统Logo
     * @param file Logo文件
     * @return Logo路径
     * @throws IOException 文件IO异常
     */
    String saveLogo(MultipartFile file) throws IOException;
    
    /**
     * 重置系统设置为默认值
     */
    void resetAllSettings();
    
    /**
     * 重置指定组的系统设置为默认值
     * @param group 设置组
     */
    void resetSettingsByGroup(String group);
} 