package com.example.medicalsystem.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 系统设置变更事件监听器
 * 负责监听和处理系统设置变更事件
 */
@Component
public class SystemSettingsListener {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemSettingsListener.class);
    
    /**
     * 处理系统设置变更事件
     * @param event 设置变更事件
     */
    @EventListener
    public void handleSettingChangeEvent(SystemSettingsServiceImpl.SettingChangeEvent event) {
        logger.info("系统设置变更: key={}, oldValue={}, newValue={}, group={}", 
                   event.getKey(), event.getOldValue(), event.getNewValue(), event.getGroup());
        
        // 根据不同的设置键或组执行不同的操作
        String key = event.getKey();
        String group = event.getGroup();
        
        // 示例：处理一些特定设置的变更
        if ("SECURITY".equals(group)) {
            // 安全设置变更后的处理逻辑
            logger.info("安全设置已变更，可能需要更新安全配置");
        } else if ("BASIC".equals(group)) {
            // 基本设置变更后的处理逻辑
            logger.info("基本设置已变更，可能需要更新系统基本信息");
        } else if ("LOG".equals(group)) {
            // 日志设置变更后的处理逻辑
            logger.info("日志设置已变更，可能需要更新日志配置");
        } else if ("system.logo".equals(key)) {
            // Logo变更后的处理逻辑
            logger.info("系统Logo已变更，前端需要刷新Logo");
        } else if ("system.name".equals(key)) {
            // 系统名称变更后的处理逻辑
            logger.info("系统名称已变更，前端需要刷新系统名称");
        }
        
        // 这里可以添加更多的特定设置处理逻辑
    }
} 