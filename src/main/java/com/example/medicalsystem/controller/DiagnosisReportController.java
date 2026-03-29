package com.example.medicalsystem.controller;

import com.example.medicalsystem.model.DiagnosisReport;
import com.example.medicalsystem.service.DiagnosisReportService;
import com.example.medicalsystem.dto.DiagnosisReportDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.medicalsystem.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/diagnosis")
public class DiagnosisReportController {

    private static final Logger logger = LoggerFactory.getLogger(DiagnosisReportController.class);

    @Autowired
    private DiagnosisReportService diagnosisReportService;

    @GetMapping("/reports")
    public ResponseEntity<Page<DiagnosisReport>> getDoctorReports(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long doctorId = userPrincipal.getId();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DiagnosisReport> reports = diagnosisReportService.getDoctorReports(
            doctorId, 
            pageable
        );
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<DiagnosisReport> getReport(
            Authentication authentication,
            @PathVariable Long id) {
        try {
            DiagnosisReport report = diagnosisReportService.getDiagnosisReport(id);
            // 检查权限
            if (report.getPatient().getUser().getUsername().equals(authentication.getName())) {
                return ResponseEntity.ok(report);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            logger.error("获取报告详情失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

 