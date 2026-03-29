package com.example.medicalsystem.controller.admin;

import com.example.medicalsystem.dto.SettingDTO;
import com.example.medicalsystem.dto.SettingGroupDTO;
import com.example.medicalsystem.model.SystemSetting;
import com.example.medicalsystem.service.SystemLogService;
import com.example.medicalsystem.service.SystemSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 系统设置控制器类
 * 负责处理系统设置相关的REST请求
 */
@RestController
@RequestMapping("/api/admin/settings")
@PreAuthorize("hasRole('ADMIN')")
public class SystemSettingsController {

    private static final Logger logger = LoggerFactory.getLogger(SystemSettingsController.class);

    @Autowired
    private SystemSettingsService systemSettingsService;
    
    @Autowired
    private SystemLogService systemLogService;

    /**
     * 获取所有系统设置
     * @return 系统设置列表
     */
    @GetMapping
    public ResponseEntity<List<SystemSetting>> getAllSettings() {
        logger.info("获取所有系统设置");
        return ResponseEntity.ok(systemSettingsService.getAllSettings());
    }

    /**
     * 按组获取系统设置
     * @return 按组分类的系统设置
     */
    @GetMapping("/groups")
    public ResponseEntity<List<SettingGroupDTO>> getSettingsByGroups() {
        logger.info("按组获取系统设置");
        return ResponseEntity.ok(systemSettingsService.getSettingsByGroups());
    }

    /**
     * 按键获取系统设置
     * @param key 设置键
     * @return 系统设置
     */
    @GetMapping("/{key}")
    public ResponseEntity<SystemSetting> getSettingByKey(@PathVariable String key) {
        logger.info("获取系统设置: {}", key);
        return ResponseEntity.ok(systemSettingsService.getSettingByKey(key));
    }

    /**
     * 获取系统基本设置
     * @return 基本设置
     */
    @GetMapping("/basic")
    public ResponseEntity<Map<String, Object>> getBasicSettings() {
        logger.info("获取系统基本设置");
        return ResponseEntity.ok(systemSettingsService.getSettingsByGroup("BASIC"));
    }

    /**
     * 获取系统安全设置
     * @return 安全设置
     */
    @GetMapping("/security")
    public ResponseEntity<Map<String, Object>> getSecuritySettings() {
        logger.info("获取系统安全设置");
        return ResponseEntity.ok(systemSettingsService.getSettingsByGroup("SECURITY"));
    }

    /**
     * 获取系统日志设置
     * @return 日志设置
     */
    @GetMapping("/log")
    public ResponseEntity<Map<String, Object>> getLogSettings() {
        logger.info("获取系统日志设置");
        return ResponseEntity.ok(systemSettingsService.getSettingsByGroup("LOG"));
    }

    /**
     * 获取系统存储设置
     * @return 存储设置
     */
    @GetMapping("/storage")
    public ResponseEntity<Map<String, Object>> getStorageSettings() {
        logger.info("获取系统存储设置");
        return ResponseEntity.ok(systemSettingsService.getSettingsByGroup("STORAGE"));
    }

    /**
     * 获取系统备份设置
     * @return 备份设置
     */
    @GetMapping("/backup")
    public ResponseEntity<Map<String, Object>> getBackupSettings() {
        logger.info("获取系统备份设置");
        return ResponseEntity.ok(systemSettingsService.getSettingsByGroup("BACKUP"));
    }

    /**
     * 获取AI模型设置
     * @return AI设置
     */
    @GetMapping("/ai")
    public ResponseEntity<Map<String, Object>> getAISettings() {
        logger.info("获取AI模型设置");
        return ResponseEntity.ok(systemSettingsService.getSettingsByGroup("AI"));
    }

    /**
     * 更新系统设置
     * @param settings 设置列表
     * @return 更新结果
     */
    @PutMapping
    public ResponseEntity<Void> updateSettings(@RequestBody List<SettingDTO> settings) {
        logger.info("更新系统设置: {}", settings.size());
        systemSettingsService.updateSettings(settings);
        systemLogService.logUserAction("更新系统设置", "更新了" + settings.size() + "个系统设置项");
        return ResponseEntity.ok().build();
    }

    /**
     * 更新单个系统设置
     * @param key 设置键
     * @param setting 设置值
     * @return 更新结果
     */
    @PutMapping("/{key}")
    public ResponseEntity<Void> updateSetting(@PathVariable String key, @RequestBody SettingDTO setting) {
        logger.info("更新系统设置: {}", key);
        systemSettingsService.updateSetting(key, setting);
        systemLogService.logUserAction("更新系统设置", "更新了系统设置: " + key);
        return ResponseEntity.ok().build();
    }

    /**
     * 上传系统Logo
     * @param file Logo文件
     * @return 上传结果
     */
    @PostMapping("/logo")
    public ResponseEntity<String> uploadLogo(@RequestParam("file") MultipartFile file) {
        try {
            logger.info("上传系统Logo: {}", file.getOriginalFilename());
            String logoPath = systemSettingsService.saveLogo(file);
            systemLogService.logUserAction("上传系统Logo", "上传了新的系统Logo");
            return ResponseEntity.ok(logoPath);
        } catch (IOException e) {
            logger.error("上传Logo失败", e);
            return ResponseEntity.badRequest().body("上传Logo失败: " + e.getMessage());
        }
    }

    /**
     * 重置系统设置
     * @param group 设置组
     * @return 重置结果
     */
    @PostMapping("/reset")
    public ResponseEntity<Void> resetSettings(@RequestParam(required = false) String group) {
        if (group != null) {
            logger.info("重置系统设置组: {}", group);
            systemSettingsService.resetSettingsByGroup(group);
            systemLogService.logUserAction("重置系统设置", "重置了系统设置组: " + group);
        } else {
            logger.info("重置所有系统设置");
            systemSettingsService.resetAllSettings();
            systemLogService.logUserAction("重置系统设置", "重置了所有系统设置");
        }
        return ResponseEntity.ok().build();
    }
} 