package com.example.medicalsystem.controller;

import com.example.medicalsystem.dto.ReportRequest;
import com.example.medicalsystem.model.DiagnosisReport;
import com.example.medicalsystem.service.MedicalReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medical/diagnosis-reports")
public class MedicalReportController {

    @Autowired
    private MedicalReportService medicalReportService;

    @PostMapping
    public ResponseEntity<DiagnosisReport> createReport(
            @ModelAttribute ReportRequest request,
            Authentication authentication) {
        try {
            DiagnosisReport report = medicalReportService.saveReport(request, authentication.getName());
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            throw new RuntimeException("保存报告失败: " + e.getMessage());
        }
    }

    @PostMapping("/export")
    public ResponseEntity<Resource> exportReport(
            Authentication authentication,
            @RequestBody ReportRequest request) {
        Resource resource = medicalReportService.generatePdfReport(
            authentication.getName(), 
            request
        );
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"medical_report.pdf\"")
            .body(resource);
    }

    @GetMapping
    public ResponseEntity<Page<DiagnosisReport>> getReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        Page<DiagnosisReport> reports = medicalReportService.getReportsByDoctor(
            authentication.getName(), page, size);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiagnosisReport> getReport(@PathVariable Long id) {
        DiagnosisReport report = medicalReportService.getReportById(id);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }

    @PostMapping("/{id}/export")
    public ResponseEntity<Resource> exportReport(
            @PathVariable Long id,
            Authentication authentication) {
        Resource resource = medicalReportService.generatePdfReportById(id, authentication.getName());
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"medical_report_" + id + ".pdf\"")
            .body(resource);
    }
} 