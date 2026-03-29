package com.example.medicalsystem.service.impl;

import com.example.medicalsystem.model.DiagnosisReport;
import com.example.medicalsystem.model.Patient;
import com.example.medicalsystem.repository.DiagnosisReportRepository;
import com.example.medicalsystem.repository.PatientRepository;
import com.example.medicalsystem.service.DiagnosisReportService;
import com.example.medicalsystem.dto.DiagnosisReportDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import com.example.medicalsystem.exception.ResourceNotFoundException;

@Service
@Slf4j
public class DiagnosisReportServiceImpl implements DiagnosisReportService {
    
    @Autowired
    private DiagnosisReportRepository diagnosisReportRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Override
    public Page<DiagnosisReport> getDoctorReports(Long doctorId, Pageable pageable) {
        log.debug("Getting reports for doctor: {}", doctorId);
        Page<DiagnosisReport> reports = diagnosisReportRepository.findByDoctorId(doctorId, pageable);
        log.debug("Found {} reports", reports.getTotalElements());
        return reports;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DiagnosisReportDTO> getPatientReports(String username, Pageable pageable) {
        Patient patient = patientRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        Page<DiagnosisReport> reports = diagnosisReportRepository
            .findByPatientOrderByCreatedAtDesc(patient, pageable);
        
        return reports.map(this::convertToDTO);
    }

    @Override
    public List<DiagnosisReportDTO> getPatientDiagnosis(Long patientId) {
        List<DiagnosisReport> reports = diagnosisReportRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
        return reports.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private DiagnosisReportDTO convertToDTO(DiagnosisReport report) {
        DiagnosisReportDTO dto = new DiagnosisReportDTO();
        dto.setId(report.getId());
        dto.setDiagnosis(report.getDiagnosis());
        dto.setTreatmentPlan(report.getTreatmentPlan());
        dto.setRecommendations(report.getRecommendations());
        dto.setCreatedAt(report.getCreatedAt());
        dto.setReadStatus(report.getReadStatus());
        
        if (report.getMedicalImage() != null) {
            dto.setImageUrl(report.getMedicalImage().getImageUrl());
            dto.setMedicalImageId(report.getMedicalImage().getId());
        }

        if (report.getDoctor() != null) {
            DiagnosisReportDTO.DoctorDTO doctorDTO = new DiagnosisReportDTO.DoctorDTO(
                report.getDoctor().getId(),
                report.getDoctor().getFullName(),
                report.getDoctor().getDepartment(),
                report.getDoctor().getProfessionalTitle()
            );
            dto.setDoctor(doctorDTO);
        }

        if (report.getPatient() != null) {
            dto.setPatientId(report.getPatient().getId());
            dto.setPatientName(report.getPatient().getFullName());
        }

        return dto;
    }

    @Override
    public Resource generatePdfReport(Long reportId) {
        DiagnosisReport report = diagnosisReportRepository.findById(reportId)
            .orElseThrow(() -> new ResourceNotFoundException("诊断报告不存在"));
            
        try {
            // 使用 iText 或其他 PDF 库生成报告
            Document document = new Document();
            File tempFile = File.createTempFile("report_", ".pdf");
            PdfWriter.getInstance(document, new FileOutputStream(tempFile));
            
            document.open();
            
            // 添加报告内容
            document.add(new Paragraph("诊断报告"));
            document.add(new Paragraph("诊断日期: " + report.getCreatedAt()));
            document.add(new Paragraph("诊断结果: " + report.getDiagnosis()));
            document.add(new Paragraph("治疗方案: " + report.getTreatmentPlan()));
            document.add(new Paragraph("医嘱建议: " + report.getRecommendations()));
            
            document.close();
            
            return new FileSystemResource(tempFile);
        } catch (Exception e) {
            throw new RuntimeException("生成PDF报告失败", e);
        }
    }

    @Override
    public DiagnosisReport getDiagnosisReport(Long id) {
        return diagnosisReportRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("诊断报告", "id", id));
    }
} 