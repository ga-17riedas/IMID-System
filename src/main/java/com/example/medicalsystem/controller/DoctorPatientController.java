package com.example.medicalsystem.controller;

import com.example.medicalsystem.dto.MedicalHistoryDTO;
import com.example.medicalsystem.dto.PatientUpdateRequest;
import com.example.medicalsystem.dto.DiagnosisReportDTO;
import com.example.medicalsystem.service.MedicalHistoryService;
import com.example.medicalsystem.service.PatientService;
import com.example.medicalsystem.service.DiagnosisReportService;
import com.example.medicalsystem.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors/patients")
public class DoctorPatientController {

    @Autowired
    private MedicalHistoryService medicalHistoryService;
    
    @Autowired
    private PatientService patientService;

    @Autowired
    private DiagnosisReportService diagnosisReportService;

    @GetMapping("/{patientId}/history")
    public ResponseEntity<List<MedicalHistoryDTO>> getPatientHistory(
            @PathVariable Long patientId,
            Authentication authentication) {
        List<MedicalHistoryDTO> history = medicalHistoryService.getPatientHistory(patientId);
        return ResponseEntity.ok(history);
    }

    @PutMapping("/{patientId}")
    public ResponseEntity<?> updatePatient(
            @PathVariable Long patientId,
            @RequestBody PatientUpdateRequest request) {
        patientService.updatePatient(patientId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{patientId}/history/{historyId}")
    public ResponseEntity<?> updateMedicalHistory(
            @PathVariable Long patientId,
            @PathVariable Long historyId,
            @RequestBody MedicalHistoryDTO request) {
        medicalHistoryService.updateMedicalHistory(historyId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{patientId}/history")
    public ResponseEntity<?> createMedicalHistory(
            @PathVariable Long patientId,
            Authentication authentication,
            @RequestBody MedicalHistoryDTO request) {
        Long doctorId = getDoctorId(authentication);
        MedicalHistoryDTO history = medicalHistoryService.createMedicalHistory(patientId, doctorId, request);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{patientId}/diagnosis")
    public ResponseEntity<List<DiagnosisReportDTO>> getPatientDiagnosis(
            @PathVariable Long patientId,
            Authentication authentication) {
        List<DiagnosisReportDTO> reports = diagnosisReportService.getPatientDiagnosis(patientId);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/reports/{reportId}/export")
    public ResponseEntity<Resource> exportReport(@PathVariable Long reportId) {
        try {
            Resource report = diagnosisReportService.generatePdfReport(reportId);
            
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.pdf\"")
                .body(report);
        } catch (Exception e) {
            throw new RuntimeException("导出报告失败: " + e.getMessage());
        }
    }

    private Long getDoctorId(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getId();
    }
} 