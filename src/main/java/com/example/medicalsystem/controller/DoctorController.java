package com.example.medicalsystem.controller;

import com.example.medicalsystem.dto.DoctorHomeDTO;
import com.example.medicalsystem.dto.DoctorProfileDTO;
import com.example.medicalsystem.dto.MedicalImageDTO;
import com.example.medicalsystem.dto.DiagnosisReportDTO;
import com.example.medicalsystem.dto.PatientDTO;
import com.example.medicalsystem.dto.MedicalHistoryDTO;
import com.example.medicalsystem.service.DoctorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.medicalsystem.dto.ScheduleDTO;
import org.springframework.http.HttpStatus;
import com.example.medicalsystem.dto.ErrorResponse;
import java.util.List;
import com.example.medicalsystem.dto.DoctorDTO;
import com.example.medicalsystem.repository.DoctorRepository;
import com.example.medicalsystem.repository.DoctorPatientRepository;
import com.example.medicalsystem.service.MedicalHistoryService;
import org.springframework.web.server.ResponseStatusException;
import com.example.medicalsystem.model.Doctor;
import com.example.medicalsystem.exception.ResourceNotFoundException;
import com.example.medicalsystem.service.ProfileService;

@RestController
@RequestMapping("/api/doctor")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorPatientRepository doctorPatientRepository;

    @Autowired
    private MedicalHistoryService medicalHistoryService;

    @Autowired
    private ProfileService profileService;

    @GetMapping("/home")
    public ResponseEntity<?> getDoctorHome() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return ResponseEntity.ok(doctorService.getDoctorHomeData(username));
    }

    @GetMapping("/patients")
    public ResponseEntity<?> getPatients(Authentication authentication) {
        return ResponseEntity.ok(doctorService.getPatients(authentication.getName()));
    }

    @GetMapping("/schedules")
    public ResponseEntity<?> getSchedules(
            Authentication authentication,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        return ResponseEntity.ok(doctorService.getSchedules(authentication.getName(), year, month));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        return ResponseEntity.ok(profileService.getDoctorProfile(authentication.getName()));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @RequestBody DoctorDTO profileDTO) {
        try {
            doctorService.updateDoctorProfile(authentication.getName(), profileDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 影像诊断相关端点
    @PostMapping("/images/upload")
    public ResponseEntity<?> uploadImage(
            @RequestParam("patientId") Long patientId,
            @RequestParam("image") MultipartFile image,
            @RequestParam("imageType") String imageType,
            @RequestParam("bodyPart") String bodyPart) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(doctorService.uploadMedicalImage(auth.getName(), patientId, image, imageType, bodyPart));
    }

    @GetMapping("/patients/{patientId}/images")
    public ResponseEntity<?> getPatientImages(@PathVariable Long patientId) {
        return ResponseEntity.ok(doctorService.getPatientImages(patientId));
    }

    @PostMapping("/images/{imageId}/analyze")
    public ResponseEntity<?> analyzeImage(@PathVariable Long imageId) {
        return ResponseEntity.ok(doctorService.analyzeMedicalImage(imageId));
    }

    @PutMapping("/images/{imageId}/diagnosis")
    public ResponseEntity<?> updateDiagnosis(
            @PathVariable Long imageId,
            @RequestParam String diagnosis) {
        doctorService.updateDiagnosis(imageId, diagnosis);
        return ResponseEntity.ok().build();
    }

    // 诊断报告相关端点
    @PostMapping("/reports")
    public ResponseEntity<?> createReport(@RequestBody DiagnosisReportDTO reportDTO) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            DiagnosisReportDTO report = doctorService.createDiagnosisReport(auth.getName(), reportDTO);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/patients/{patientId}/reports")
    public ResponseEntity<?> getPatientReports(@PathVariable Long patientId) {
        try {
            List<DiagnosisReportDTO> reports = doctorService.getPatientReports(patientId);
            return ResponseEntity.ok(reports);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/reports/{reportId}")
    public ResponseEntity<?> getReport(@PathVariable Long reportId) {
        try {
            DiagnosisReportDTO report = doctorService.getReport(reportId);
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    // 患者管理相关端点
    @GetMapping("/patients/search")
    public ResponseEntity<?> searchPatients(@RequestParam String query) {
        try {
            List<PatientDTO> patients = doctorService.searchPatients(query);
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/patients/{patientId}")
    public ResponseEntity<?> getPatientDetail(@PathVariable Long patientId) {
        return ResponseEntity.ok(doctorService.getPatientDetail(patientId));
    }

    @GetMapping("/patients/recent")
    public ResponseEntity<?> getRecentPatients() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(doctorService.getRecentPatients(auth.getName()));
    }

    @GetMapping("/patients/{patientId}/history")
    public ResponseEntity<?> getPatientHistory(@PathVariable Long patientId) {
        try {
            List<MedicalHistoryDTO> history = medicalHistoryService.getPatientHistory(patientId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/patients/{patientId}")
    public ResponseEntity<?> updatePatient(
            @PathVariable Long patientId,
            @RequestBody PatientDTO patientDTO) {
        try {
            PatientDTO updated = doctorService.updatePatient(patientId, patientDTO);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    // 统计分析相关端点
    @GetMapping("/statistics")
    public ResponseEntity<?> getDoctorStatistics() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(doctorService.getDoctorStatistics(auth.getName()));
    }

    @GetMapping("/statistics/range")
    public ResponseEntity<?> getDoctorStatisticsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(
            doctorService.getDoctorStatisticsByDateRange(auth.getName(), startDate, endDate));
    }

    // 排班管理相关端点
    @PostMapping("/schedules")
    public ResponseEntity<?> createSchedule(@RequestBody ScheduleDTO scheduleDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(doctorService.createSchedule(auth.getName(), scheduleDTO));
    }

    @PutMapping("/schedules/{id}")
    public ResponseEntity<?> updateSchedule(
            @PathVariable Long id,
            @RequestBody ScheduleDTO scheduleDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(doctorService.updateSchedule(auth.getName(), id, scheduleDTO));
    }

    @DeleteMapping("/schedules/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        doctorService.deleteSchedule(auth.getName(), id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/patients/{patientId}/add")
    public ResponseEntity<?> addPatient(
            @PathVariable Long patientId,
            @RequestParam String relationshipType,
            Authentication auth) {
        try {
            doctorService.addPatient(auth.getName(), patientId, relationshipType);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/patients/{patientId}/remove")
    public ResponseEntity<?> removePatient(
            @PathVariable Long patientId,
            Authentication auth) {
        try {
            doctorService.removePatient(auth.getName(), patientId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/my-patients")
    public ResponseEntity<?> getMyPatients(Authentication auth) {
        try {
            List<PatientDTO> patients = doctorService.getMyPatients(auth.getName());
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(e.getMessage()));
        }
    }
} 