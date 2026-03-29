package com.example.medicalsystem.service;

import com.example.medicalsystem.model.DiagnosisReport;
import com.example.medicalsystem.dto.DiagnosisReportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.core.io.Resource;

public interface DiagnosisReportService {
    Page<DiagnosisReport> getDoctorReports(Long doctorId, Pageable pageable);
    
    Page<DiagnosisReportDTO> getPatientReports(String username, Pageable pageable);
    
    List<DiagnosisReportDTO> getPatientDiagnosis(Long patientId);
    
    Resource generatePdfReport(Long reportId);

    DiagnosisReport getDiagnosisReport(Long id);
} 