package com.example.medicalsystem.service.impl;

import com.example.medicalsystem.dto.SettingDTO;
import com.example.medicalsystem.dto.SettingGroupDTO;
import com.example.medicalsystem.model.SystemSetting;
import com.example.medicalsystem.repository.SystemSettingRepository;
import com.example.medicalsystem.service.SystemSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统设置服务实现类
 * 实现系统设置相关的业务逻辑
 */
@Service
public class SystemSettingsServiceImpl implements SystemSettingsService {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemSettingsServiceImpl.class);
    
    @Autowired
    private SystemSettingRepository systemSettingRepository;
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    @Value("${upload.path}")
    private String uploadPath;
    
    // 系统设置缓存
    private Map<String, String> settingsCache = new HashMap<>();
    
    /**
     * 设置组名称映射
     */
    private static final Map<String, String> GROUP_DISPLAY_NAMES = new HashMap<>();
    
    /**
     * 设置组图标映射
     */
    private static final Map<String, String> GROUP_ICONS = new HashMap<>();
    
    /**
     * 设置组描述映射
     */
    private static final Map<String, String> GROUP_DESCRIPTIONS = new HashMap<>();
    
    /**
     * 设置组排序映射
     */
    private static final Map<String, Integer> GROUP_ORDERS = new HashMap<>();
    
    static {
        // 初始化组显示名称
        GROUP_DISPLAY_NAMES.put("BASIC", "基本设置");
        GROUP_DISPLAY_NAMES.put("SECURITY", "安全设置");
        GROUP_DISPLAY_NAMES.put("LOG", "日志设置");
        GROUP_DISPLAY_NAMES.put("STORAGE", "存储设置");
        GROUP_DISPLAY_NAMES.put("BACKUP", "备份设置");
        GROUP_DISPLAY_NAMES.put("AI", "AI模型设置");
        
        // 初始化组图标
        GROUP_ICONS.put("BASIC", "el-icon-setting");
        GROUP_ICONS.put("SECURITY", "el-icon-lock");
        GROUP_ICONS.put("LOG", "el-icon-document");
        GROUP_ICONS.put("STORAGE", "el-icon-folder");
        GROUP_ICONS.put("BACKUP", "el-icon-copy-document");
        GROUP_ICONS.put("AI", "el-icon-cpu");
        
        // 初始化组描述
        GROUP_DESCRIPTIONS.put("BASIC", "系统基本配置，包括名称、描述、Logo等");
        GROUP_DESCRIPTIONS.put("SECURITY", "系统安全配置，包括密码策略、登录策略等");
        GROUP_DESCRIPTIONS.put("LOG", "系统日志配置，包括日志级别、保留时间等");
        GROUP_DESCRIPTIONS.put("STORAGE", "系统存储配置，包括存储方式、文件类型等");
        GROUP_DESCRIPTIONS.put("BACKUP", "系统备份配置，包括备份频率、保留数量等");
        GROUP_DESCRIPTIONS.put("AI", "AI模型配置，包括模型路径、检测阈值等");
        
        // 初始化组排序
        GROUP_ORDERS.put("BASIC", 1);
        GROUP_ORDERS.put("SECURITY", 2);
        GROUP_ORDERS.put("LOG", 3);
        GROUP_ORDERS.put("STORAGE", 4);
        GROUP_ORDERS.put("BACKUP", 5);
        GROUP_ORDERS.put("AI", 6);
    }
    
    /**
     * 刷新系统设置缓存
     */
    public void refreshSettingsCache() {
        logger.info("刷新系统设置缓存");
        // 清空现有缓存
        settingsCache.clear();
        
        // 重新加载所有设置到缓存
        List<SystemSetting> allSettings = systemSettingRepository.findAll();
        for (SystemSetting setting : allSettings) {
            settingsCache.put(setting.getKey(), setting.getValue());
        }
        
        logger.info("系统设置缓存已刷新，共加载 {} 条设置", settingsCache.size());
    }
    
    /**
     * 系统设置变更事件类
     */
    public static class SettingChangeEvent {
        private final String key;
        private final String oldValue;
        private final String newValue;
        private final String group;
        
        public SettingChangeEvent(String key, String oldValue, String newValue, String group) {
            this.key = key;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.group = group;
        }
        
        public String getKey() {
            return key;
        }
        
        public String getOldValue() {
            return oldValue;
        }
        
        public String getNewValue() {
            return newValue;
        }
        
        public String getGroup() {
            return group;
        }
    }
    
    @Override
    public List<SystemSetting> getAllSettings() {
        return systemSettingRepository.findAll();
    }
    
    @Override
    public List<SettingGroupDTO> getSettingsByGroups() {
        Map<String, List<SystemSetting>> groupedSettings = systemSettingRepository.findAll().stream()
                .collect(Collectors.groupingBy(SystemSetting::getGroup));
        
        List<SettingGroupDTO> result = new ArrayList<>();
        
        for (String group : groupedSettings.keySet()) {
            List<SettingDTO> settingDTOs = groupedSettings.get(group).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            SettingGroupDTO groupDTO = new SettingGroupDTO(
                    group,
                    GROUP_DISPLAY_NAMES.getOrDefault(group, group),
                    GROUP_DESCRIPTIONS.getOrDefault(group, ""),
                    GROUP_ICONS.getOrDefault(group, "el-icon-setting"),
                    GROUP_ORDERS.getOrDefault(group, 999),
                    settingDTOs
            );
            
            result.add(groupDTO);
        }
        
        // 按组排序号排序
        result.sort(Comparator.comparing(SettingGroupDTO::getOrderNum));
        
        return result;
    }
    
    @Override
    public SystemSetting getSettingByKey(String key) {
        return systemSettingRepository.findByKey(key)
                .orElseThrow(() -> new EntityNotFoundException("设置不存在: " + key));
    }
    
    @Override
    public Map<String, Object> getSettingsByGroup(String group) {
        List<SystemSetting> settings = systemSettingRepository.findByGroupOrderByOrderNumAsc(group);
        Map<String, Object> result = new HashMap<>();
        
        for (SystemSetting setting : settings) {
            Object value = convertValueByType(setting.getValue(), setting.getType());
            result.put(setting.getKey(), value);
        }
        
        return result;
    }
    
    @Override
    public String getSettingValue(String key, String defaultValue) {
        // 先从缓存中获取
        if (settingsCache.containsKey(key)) {
            return settingsCache.get(key);
        }
        
        // 缓存中没有，从数据库中获取并加入缓存
        Optional<SystemSetting> setting = systemSettingRepository.findByKey(key);
        if (setting.isPresent()) {
            String value = setting.get().getValue();
            settingsCache.put(key, value);
            return value;
        }
        
        return defaultValue;
    }
    
    @Override
    public Integer getIntegerValue(String key, Integer defaultValue) {
        String value = getSettingValue(key, null);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("无法将设置值转换为Integer: {}={}", key, value);
            return defaultValue;
        }
    }
    
    @Override
    public Boolean getBooleanValue(String key, Boolean defaultValue) {
        String value = getSettingValue(key, null);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    @Override
    @Transactional
    public void updateSettings(List<SettingDTO> settings) {
        for (SettingDTO settingDTO : settings) {
            Optional<SystemSetting> existingSetting = systemSettingRepository.findByKey(settingDTO.getKey());
            
            if (existingSetting.isPresent()) {
                SystemSetting setting = existingSetting.get();
                
                // 如果是只读设置，则不更新
                if (setting.getIsReadonly() != null && setting.getIsReadonly()) {
                    logger.warn("尝试更新只读设置: {}", setting.getKey());
                    continue;
                }
                
                // 获取旧值
                String oldValue = setting.getValue();
                
                // 更新值
                setting.setValue(settingDTO.getValue());
                systemSettingRepository.save(setting);
                
                // 更新缓存
                settingsCache.put(setting.getKey(), setting.getValue());
                
                // 发布设置变更事件
                eventPublisher.publishEvent(new SettingChangeEvent(
                        setting.getKey(), 
                        oldValue, 
                        setting.getValue(),
                        setting.getGroup()
                ));
                
                logger.info("更新设置: {}={}", setting.getKey(), setting.getValue());
            } else {
                logger.warn("设置不存在: {}", settingDTO.getKey());
            }
        }
    }
    
    @Override
    @Transactional
    public void updateSetting(String key, SettingDTO settingDTO) {
        SystemSetting setting = getSettingByKey(key);
        
        // 如果是只读设置，则不更新
        if (setting.getIsReadonly() != null && setting.getIsReadonly()) {
            logger.warn("尝试更新只读设置: {}", setting.getKey());
            throw new IllegalArgumentException("无法更新只读设置: " + key);
        }
        
        // 获取旧值
        String oldValue = setting.getValue();
        
        // 更新值
        setting.setValue(settingDTO.getValue());
        systemSettingRepository.save(setting);
        
        // 更新缓存
        settingsCache.put(setting.getKey(), setting.getValue());
        
        // 发布设置变更事件
        eventPublisher.publishEvent(new SettingChangeEvent(
                setting.getKey(), 
                oldValue, 
                setting.getValue(),
                setting.getGroup()
        ));
        
        logger.info("更新设置: {}={}", setting.getKey(), setting.getValue());
    }
    
    @Override
    public String saveLogo(MultipartFile file) throws IOException {
        // 确保上传目录存在
        Path logoDir = Paths.get(uploadPath, "logo");
        if (!Files.exists(logoDir)) {
            Files.createDirectories(logoDir);
        }
        
        // 生成文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = "logo_" + System.currentTimeMillis() + extension;
        
        // 保存文件
        Path filePath = logoDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath);
        
        // 构建访问路径
        String logoPath = "/uploads/logo/" + filename;
        
        // 更新系统Logo设置
        Optional<SystemSetting> logoSetting = systemSettingRepository.findByKey("system.logo");
        if (logoSetting.isPresent()) {
            SystemSetting setting = logoSetting.get();
            
            // 获取旧值
            String oldValue = setting.getValue();
            
            // 更新值
            setting.setValue(logoPath);
            systemSettingRepository.save(setting);
            
            // 更新缓存
            settingsCache.put(setting.getKey(), setting.getValue());
            
            // 发布设置变更事件
            eventPublisher.publishEvent(new SettingChangeEvent(
                    setting.getKey(), 
                    oldValue, 
                    setting.getValue(),
                    setting.getGroup()
            ));
            
            logger.info("更新系统Logo: {}", logoPath);
        }
        
        return logoPath;
    }
    
    @Override
    @Transactional
    public void resetAllSettings() {
        List<SystemSetting> settings = systemSettingRepository.findAll();
        
        for (SystemSetting setting : settings) {
            // 如果是系统设置且有默认值，则重置为默认值
            if (setting.getIsSystem() != null && setting.getIsSystem() && setting.getDefaultValue() != null) {
                // 获取旧值
                String oldValue = setting.getValue();
                
                // 重置为默认值
                setting.setValue(setting.getDefaultValue());
                systemSettingRepository.save(setting);
                
                // 更新缓存
                settingsCache.put(setting.getKey(), setting.getValue());
                
                // 发布设置变更事件
                eventPublisher.publishEvent(new SettingChangeEvent(
                        setting.getKey(), 
                        oldValue, 
                        setting.getValue(),
                        setting.getGroup()
                ));
                
                logger.info("重置设置: {}={}", setting.getKey(), setting.getValue());
            }
        }
    }
    
    @Override
    @Transactional
    public void resetSettingsByGroup(String group) {
        List<SystemSetting> settings = systemSettingRepository.findByGroup(group);
        
        for (SystemSetting setting : settings) {
            // 如果是系统设置且有默认值，则重置为默认值
            if (setting.getIsSystem() != null && setting.getIsSystem() && setting.getDefaultValue() != null) {
                // 获取旧值
                String oldValue = setting.getValue();
                
                // 重置为默认值
                setting.setValue(setting.getDefaultValue());
                systemSettingRepository.save(setting);
                
                // 更新缓存
                settingsCache.put(setting.getKey(), setting.getValue());
                
                // 发布设置变更事件
                eventPublisher.publishEvent(new SettingChangeEvent(
                        setting.getKey(), 
                        oldValue, 
                        setting.getValue(),
                        setting.getGroup()
                ));
                
                logger.info("重置设置: {}={}", setting.getKey(), setting.getValue());
            }
        }
    }
    
    /**
     * 初始化缓存
     */
    @EventListener(org.springframework.context.event.ContextRefreshedEvent.class)
    public void initializeCache() {
        logger.info("初始化系统设置缓存");
        refreshSettingsCache();
    }
    
    /**
     * 将SystemSetting转换为SettingDTO
     * @param setting 系统设置
     * @return 设置DTO
     */
    private SettingDTO convertToDTO(SystemSetting setting) {
        return new SettingDTO(
                setting.getKey(),
                setting.getValue(),
                setting.getType(),
                setting.getGroup(),
                setting.getDisplayName(),
                setting.getDescription(),
                setting.getIsReadonly()
        );
    }
    
    /**
     * 根据类型转换设置值
     * @param value 设置值
     * @param type 类型
     * @return 转换后的值
     */
    private Object convertValueByType(String value, String type) {
        if (value == null) {
            return null;
        }
        
        switch (type) {
            case "NUMBER":
                try {
                    // 尝试解析为整数
                    return Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    try {
                        // 尝试解析为浮点数
                        return Double.parseDouble(value);
                    } catch (NumberFormatException ex) {
                        logger.warn("无法将值转换为数字: {}", value);
                        return value;
                    }
                }
            case "BOOLEAN":
                return Boolean.parseBoolean(value);
            case "JSON":
                // 注意：这里未实现JSON解析，返回原始字符串
                return value;
            case "STRING":
            default:
                return value;
        }
    }
} 