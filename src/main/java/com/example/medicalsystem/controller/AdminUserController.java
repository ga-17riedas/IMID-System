package com.example.medicalsystem.controller;

import com.example.medicalsystem.dto.*;
import com.example.medicalsystem.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    // 医生管理接口
    @GetMapping("/doctors")
    public ResponseEntity<Page<DoctorDTO>> getDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department) {
        return ResponseEntity.ok(adminUserService.getDoctors(PageRequest.of(page, size), search, department));
    }

    @PostMapping("/doctors")
    public ResponseEntity<DoctorDTO> createDoctor(@RequestBody DoctorCreateRequest request) {
        return ResponseEntity.ok(adminUserService.createDoctor(request));
    }

    @PutMapping("/doctors/{id}")
    public ResponseEntity<DoctorDTO> updateDoctor(
            @PathVariable Long id,
            @RequestBody DoctorUpdateRequest request) {
        return ResponseEntity.ok(adminUserService.updateDoctor(id, request));
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        adminUserService.deleteDoctor(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/doctors/{id}/reset-password")
    public ResponseEntity<PasswordResetResponse> resetDoctorPassword(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.resetDoctorPassword(id));
    }

    // 患者管理接口
    @GetMapping("/patients")
    public ResponseEntity<Page<PatientDTO>> getPatients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String gender) {
        return ResponseEntity.ok(adminUserService.getPatients(PageRequest.of(page, size), search, gender));
    }

    @GetMapping("/patients/{id}/history")
    public ResponseEntity<Page<MedicalHistoryDTO>> getPatientHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminUserService.getPatientHistory(id, PageRequest.of(page, size)));
    }

    @PostMapping("/patients/{id}/reset-password")
    public ResponseEntity<PasswordResetResponse> resetPatientPassword(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.resetPatientPassword(id));
    }

    // 管理员管理接口
    @GetMapping("/admins")
    public ResponseEntity<Page<AdminDTO>> getAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(adminUserService.getAdmins(PageRequest.of(page, size), search));
    }

    @PostMapping("/admins")
    public ResponseEntity<?> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        try {
            AdminDTO admin = adminUserService.createAdmin(request);
            return ResponseEntity.ok(admin);
        } catch (Exception e) {
            return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/admins/{id}/status")
    public ResponseEntity<AdminDTO> updateAdminStatus(
            @PathVariable Long id,
            @RequestBody AdminStatusUpdateRequest request) {
        return ResponseEntity.ok(adminUserService.updateAdminStatus(id, request.getStatus()));
    }

    @PostMapping("/admins/{id}/reset-password")
    public ResponseEntity<PasswordResetResponse> resetAdminPassword(@PathVariable Long id) {
        return ResponseEntity.ok(adminUserService.resetAdminPassword(id));
    }
} 