package com.example.medicalsystem.service;

import com.example.medicalsystem.dto.ReportRequest;
import com.example.medicalsystem.model.DiagnosisReport;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;

public interface MedicalReportService {
    DiagnosisReport saveReport(ReportRequest request, String doctorUsername);
    Resource generatePdfReport(String username, ReportRequest request);
    Page<DiagnosisReport> getReportsByDoctor(String username, int page, int size);
    Resource generatePdfReportById(Long reportId, String username);
    DiagnosisReport getReportById(Long id);
} 