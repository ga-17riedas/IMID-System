package com.example.medicalsystem.service;

import com.example.medicalsystem.dto.PatientHomeDTO;
import com.example.medicalsystem.dto.PatientProfileDTO;
import com.example.medicalsystem.dto.MedicalImageDTO;
import com.example.medicalsystem.dto.DiagnosisReportDTO;
import com.example.medicalsystem.dto.PatientUpdateRequest;
import com.example.medicalsystem.model.DiagnosisReport;
import com.example.medicalsystem.model.MedicalRecord;
import org.springframework.data.domain.Page;
import java.util.List;

public interface PatientService {
    // 首页相关
    PatientHomeDTO getPatientHomeData(String username);
    
    // 个人信息相关
    PatientProfileDTO getPatientProfile(String username);
    void updatePatientProfile(String username, PatientProfileDTO profileDTO);
    
    // 医疗记录相关
    List<MedicalImageDTO> getPatientImages(String username);
    List<DiagnosisReportDTO> getPatientReports(String username);
    
    // 诊断报告相关
    Page<DiagnosisReport> getReports(String username, int page, int size, String search);
    DiagnosisReport getReport(String username, Long reportId);
    void markReportAsRead(String username, Long reportId);
    
    // 废弃的方法，将被移除
    @Deprecated
    DiagnosisReportDTO getReport(Long reportId);

    // 添加获取病历记录的方法
    List<MedicalRecord> getMedicalRecords(String username);

    void updatePatient(Long patientId, PatientUpdateRequest request);
} 