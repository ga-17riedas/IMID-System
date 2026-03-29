package com.example.medicalsystem.service.impl;

import com.example.medicalsystem.model.MedicalImage;
import com.example.medicalsystem.dto.MedicalImageDTO;
import com.example.medicalsystem.repository.MedicalImageRepository;
import com.example.medicalsystem.service.MedicalImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.time.format.DateTimeFormatter;
import com.example.medicalsystem.repository.DiagnosisReportRepository;
import com.example.medicalsystem.dto.MedicalImageDetailDTO;
import com.example.medicalsystem.exception.ResourceNotFoundException;
import com.example.medicalsystem.repository.DoctorRepository;
import com.example.medicalsystem.model.DiagnosisReport;

@Service
public class MedicalImageServiceImpl implements MedicalImageService {
    
    @Autowired
    private MedicalImageRepository medicalImageRepository;
    
    @Autowired
    private DiagnosisReportRepository diagnosisReportRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;
    
    @Value("${app.base.url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public MedicalImage saveImage(MultipartFile file, Long patientId, Long doctorId) throws IOException {
        // 创建上传目录
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString() + "_" + StringUtils.cleanPath(file.getOriginalFilename());
        String filePath = uploadDir + File.separator + fileName;
        
        // 保存文件
        Path path = Paths.get(filePath);
        Files.write(path, file.getBytes());

        // 生成访问URL（使用相对路径，而不是绝对路径）
        String imageUrl = "/uploads/" + fileName;

        // 保存到数据库
        MedicalImage image = new MedicalImage();
        image.setPatientId(patientId);
        image.setDoctorId(doctorId);
        image.setImagePath(filePath);
        image.setImageUrl(imageUrl);
        image.setImageType(file.getContentType());
        image.setBodyPart("未指定"); // 默认值，可以在后续更新
        
        return medicalImageRepository.save(image);
    }

    @Override
    public Page<MedicalImageDTO> getAdminImages(Pageable pageable, String search, String imageType) {
        Page<MedicalImage> images;
        
        if (StringUtils.hasText(search) && StringUtils.hasText(imageType)) {
            images = medicalImageRepository.findByPatientNameContainingAndImageType(
                search, imageType, pageable);
        } else if (StringUtils.hasText(search)) {
            images = medicalImageRepository.findByPatientNameContaining(search, pageable);
        } else if (StringUtils.hasText(imageType)) {
            images = medicalImageRepository.findByImageType(imageType, pageable);
        } else {
            images = medicalImageRepository.findAll(pageable);
        }
        
        return images.map(this::convertToDTO);
    }

    @Override
    public void deleteImage(Long id) {
        MedicalImage image = medicalImageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Image not found with id: " + id));
            
        // 删除物理文件
        try {
            Path path = Paths.get(image.getImagePath());
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image file", e);
        }
        
        // 从数据库中删除记录
        medicalImageRepository.delete(image);
    }

    @Override
    public MedicalImageDetailDTO getImageDetail(Long id) {
        MedicalImage image = medicalImageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("医学影像", "id", id));
            
        // 获取最新的诊断报告
        DiagnosisReport report = diagnosisReportRepository
            .findFirstByMedicalImageIdOrderByCreatedAtDesc(id)
            .orElse(null);
            
        MedicalImageDetailDTO dto = new MedicalImageDetailDTO();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setImageType(image.getImageType());
        dto.setConfidenceScore(image.getConfidenceScore());
        dto.setAiDiagnosis(image.getAiDiagnosis());
        dto.setPatientName(image.getPatient().getFullName());
        dto.setCreatedAt(image.getCreatedAt());
        
        // 从医生表获取医生姓名
        if (image.getDoctor() != null) {
            dto.setDoctorName(image.getDoctor().getFullName());
        }
        
        // 从诊断报告获取医生诊断信息
        if (report != null) {
            dto.setDoctorDiagnosis(report.getDiagnosis());
            dto.setTreatmentPlan(report.getTreatmentPlan());
            dto.setRecommendations(report.getRecommendations());
        }
        
        return dto;
    }

    private MedicalImageDTO convertToDTO(MedicalImage image) {
        MedicalImageDTO dto = new MedicalImageDTO();
        dto.setId(image.getId());
        dto.setPatientId(image.getPatient() != null ? image.getPatient().getId() : null);
        dto.setPatientName(image.getPatient() != null ? image.getPatient().getFullName() : null);
        
        // 使用imageUrl字段而不是imagePath
        dto.setImageUrl(image.getImageUrl());
        
        dto.setImageType(image.getImageType());
        dto.setBodyPart(image.getBodyPart());
        dto.setAiDiagnosis(image.getAiDiagnosis());
        dto.setConfidenceScore(image.getConfidenceScore());
        dto.setDoctorDiagnosis(image.getDoctorDiagnosis());
        dto.setCreatedAt(image.getCreatedAt() != null ? 
            image.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        return dto;
    }

    private String getFileExtension(String filename) {
        if (filename == null) return "";
        int lastDotIndex = filename.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : filename.substring(lastDotIndex);
    }

    private MedicalImageDetailDTO convertToDetailDTO(MedicalImage image) {
        MedicalImageDetailDTO dto = new MedicalImageDetailDTO();
        dto.setId(image.getId());
        dto.setImageUrl(image.getImageUrl());
        dto.setImageType(image.getImageType());
        dto.setConfidenceScore(image.getConfidenceScore());
        dto.setAiDiagnosis(image.getAiDiagnosis());
        dto.setDoctorDiagnosis(image.getDoctorDiagnosis());
        
        // 添加医生相关信息
        if (image.getDoctor() != null) {
            dto.setDoctorName(image.getDoctor().getFullName());
        }
        
        // 添加患者信息
        if (image.getPatient() != null) {
            dto.setPatientName(image.getPatient().getFullName());
        }
        
        // 添加诊断报告相关信息
        DiagnosisReport report = diagnosisReportRepository.findFirstByMedicalImageIdOrderByCreatedAtDesc(image.getId())
            .orElse(null);
        if (report != null) {
            dto.setTreatmentPlan(report.getTreatmentPlan());
            dto.setRecommendations(report.getRecommendations());
        }
        
        dto.setCreatedAt(image.getCreatedAt());
        return dto;
    }
} 