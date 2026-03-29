package com.example.medicalsystem.controller;

import com.example.medicalsystem.dto.PatientHomeDTO;
import com.example.medicalsystem.dto.PatientProfileDTO;
import com.example.medicalsystem.dto.DiagnosisReportDTO;
import com.example.medicalsystem.model.DiagnosisReport;
import com.example.medicalsystem.model.MedicalRecord;
import com.example.medicalsystem.service.PatientService;
import com.example.medicalsystem.service.MedicalReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@PreAuthorize("hasRole('PATIENT')")
public class PatientController {

    @Autowired
    private PatientService patientService;
    
    @Autowired
    private MedicalReportService medicalReportService;

    @GetMapping("/home")
    public ResponseEntity<PatientHomeDTO> getPatientHomeData(Authentication authentication) {
        return ResponseEntity.ok(patientService.getPatientHomeData(authentication.getName()));
    }

    @GetMapping("/profile")
    public ResponseEntity<PatientProfileDTO> getPatientProfile(Authentication authentication) {
        return ResponseEntity.ok(patientService.getPatientProfile(authentication.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> updatePatientProfile(
        @RequestBody PatientProfileDTO profileDTO,
        Authentication authentication
    ) {
        patientService.updatePatientProfile(authentication.getName(), profileDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/images")
    public ResponseEntity<?> getPatientImages() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(patientService.getPatientImages(auth.getName()));
    }

    @GetMapping("/reports")
    public ResponseEntity<Page<DiagnosisReport>> getReports(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search,
        Authentication authentication
    ) {
        String username = authentication.getName();
        return ResponseEntity.ok(patientService.getReports(username, page, size, search));
    }

    @GetMapping("/reports/{reportId}")
    public ResponseEntity<DiagnosisReportDTO> getReport(
        @PathVariable Long reportId,
        Authentication authentication
    ) {
        DiagnosisReport report = patientService.getReport(authentication.getName(), reportId);
        DiagnosisReportDTO reportDTO = convertToDTO(report);
        return ResponseEntity.ok(reportDTO);
    }

    private DiagnosisReportDTO convertToDTO(DiagnosisReport report) {
        DiagnosisReportDTO dto = new DiagnosisReportDTO();
        dto.setId(report.getId());
        dto.setPatientId(report.getPatient().getId());
        dto.setPatientName(report.getPatient().getFullName());
        dto.setDiagnosis(report.getDiagnosis());
        dto.setTreatmentPlan(report.getTreatmentPlan());
        dto.setRecommendations(report.getRecommendations());
        dto.setReadStatus(report.getReadStatus());
        dto.setCreatedAt(report.getCreatedAt());

        if (report.getMedicalImage() != null) {
            dto.setImageUrl(report.getMedicalImage().getImageUrl());
            dto.setMedicalImageId(report.getMedicalImage().getId());
            dto.setImageType(report.getMedicalImage().getImageType());
            dto.setConfidenceScore(report.getMedicalImage().getConfidenceScore());
            dto.setAiDiagnosis(report.getMedicalImage().getAiDiagnosis());
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

        return dto;
    }

    @PostMapping("/reports/{reportId}/read")
    public ResponseEntity<Void> markReportAsRead(
        @PathVariable Long reportId,
        Authentication authentication
    ) {
        patientService.markReportAsRead(authentication.getName(), reportId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/medical-records")
    public ResponseEntity<List<MedicalRecord>> getMedicalRecords(Authentication authentication) {
        return ResponseEntity.ok(patientService.getMedicalRecords(authentication.getName()));
    }
    
    @GetMapping("/medical/reports/{reportId}/pdf")
    public ResponseEntity<Resource> downloadReportPdf(
        @PathVariable Long reportId,
        Authentication authentication
    ) {
        patientService.getReport(authentication.getName(), reportId);
        
        Resource resource = medicalReportService.generatePdfReportById(reportId, null);
        
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"medical_report_" + reportId + ".pdf\"")
            .body(resource);
    }
} 