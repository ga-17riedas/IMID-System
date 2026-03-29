package com.example.medicalsystem.service;

import com.example.medicalsystem.dto.DoctorProfileDTO;
import com.example.medicalsystem.dto.PatientProfileDTO;

public interface ProfileService {
    DoctorProfileDTO getDoctorProfile(String username);
    PatientProfileDTO getPatientProfile(String username);
    void updateDoctorProfile(String username, DoctorProfileDTO profileDTO);
    void updatePatientProfile(String username, PatientProfileDTO profileDTO);
}