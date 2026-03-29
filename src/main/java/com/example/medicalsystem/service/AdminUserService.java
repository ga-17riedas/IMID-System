package com.example.medicalsystem.service;

import com.example.medicalsystem.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {
    // 医生管理
    Page<DoctorDTO> getDoctors(Pageable pageable, String search, String department);
    DoctorDTO createDoctor(DoctorCreateRequest request);
    DoctorDTO updateDoctor(Long id, DoctorUpdateRequest request);
    void deleteDoctor(Long id);
    PasswordResetResponse resetDoctorPassword(Long id);

    // 患者管理
    Page<PatientDTO> getPatients(Pageable pageable, String search, String gender);
    Page<MedicalHistoryDTO> getPatientHistory(Long patientId, Pageable pageable);
    PatientDTO updatePatient(Long id, PatientUpdateRequest request);
    PasswordResetResponse resetPatientPassword(Long id);

    // 管理员管理
    Page<AdminDTO> getAdmins(Pageable pageable, String search);
    AdminDTO createAdmin(AdminCreateRequest request);
    AdminDTO updateAdminStatus(Long id, String status);
    PasswordResetResponse resetAdminPassword(Long id);
} 