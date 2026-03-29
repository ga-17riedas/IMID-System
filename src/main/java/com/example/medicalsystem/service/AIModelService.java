package com.example.medicalsystem.service;

import com.example.medicalsystem.model.AIModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface AIModelService {
    /**
     * 获取所有模型
     */
    List<AIModel> getAllModels();
    
    /**
     * 分页获取所有模型
     */
    Page<AIModel> getModels(Pageable pageable);
    
    /**
     * 根据类型分页获取模型
     */
    Page<AIModel> getModelsByType(String type, Pageable pageable);
    
    /**
     * 根据ID获取模型
     */
    Optional<AIModel> getModelById(Long id);
    
    /**
     * 保存新模型
     */
    AIModel saveModel(MultipartFile file, String name, String type, String version, String description,
                     Double accuracy, Double precision, Double recall, Double f1Score);
    
    /**
     * 更新模型信息
     */
    AIModel updateModel(Long id, String name, String type, String version, String description,
                       Double accuracy, Double precision, Double recall, Double f1Score);
    
    /**
     * 删除模型
     */
    void deleteModel(Long id);
    
    /**
     * 设置模型激活状态
     */
    AIModel setModelActive(Long id, boolean active);
    
    /**
     * 获取指定类型的激活模型
     */
    Optional<AIModel> getActiveModelByType(String type);
    
    /**
     * 分析模型文件
     */
    Object analyzeModel(MultipartFile file);
} 