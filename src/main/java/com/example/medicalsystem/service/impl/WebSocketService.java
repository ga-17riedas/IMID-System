package com.example.medicalsystem.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket服务类
 * 负责通过WebSocket向客户端推送消息
 */
@Service
public class WebSocketService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * 处理系统设置变更事件并通过WebSocket通知客户端
     * @param event 设置变更事件
     */
    @EventListener
    public void handleSettingChangeEvent(SystemSettingsServiceImpl.SettingChangeEvent event) {
        logger.info("发送设置变更WebSocket通知: {}", event.getKey());
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "SETTING_CHANGED");
        payload.put("key", event.getKey());
        payload.put("value", event.getNewValue());
        payload.put("group", event.getGroup());
        
        // 发送到系统设置主题
        messagingTemplate.convertAndSend("/topic/settings", payload);
        
        // 对特定设置发送到特定主题
        messagingTemplate.convertAndSend("/topic/settings/" + event.getKey(), payload);
        
        // 对特定组发送到特定主题
        if (event.getGroup() != null) {
            messagingTemplate.convertAndSend("/topic/settings/group/" + event.getGroup(), payload);
        }
    }
    
    /**
     * 发送系统通知
     * @param message 通知消息
     */
    public void sendSystemNotification(String message) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "SYSTEM_NOTIFICATION");
        payload.put("message", message);
        
        messagingTemplate.convertAndSend("/topic/notifications", payload);
    }
} 