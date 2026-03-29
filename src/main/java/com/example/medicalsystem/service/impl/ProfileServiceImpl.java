package com.example.medicalsystem.service.impl;

import com.example.medicalsystem.dto.DoctorProfileDTO;
import com.example.medicalsystem.dto.PatientProfileDTO;
import com.example.medicalsystem.model.Doctor;
import com.example.medicalsystem.model.Patient;
import com.example.medicalsystem.model.User;
import com.example.medicalsystem.repository.DoctorRepository;
import com.example.medicalsystem.repository.PatientRepository;
import com.example.medicalsystem.repository.UserRepository;
import com.example.medicalsystem.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public DoctorProfileDTO getDoctorProfile(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        Doctor doctor = doctorRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        DoctorProfileDTO dto = new DoctorProfileDTO();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(doctor.getFullName());
        dto.setAge(doctor.getAge());
        dto.setGender(doctor.getGender());
        dto.setPhone(doctor.getPhone());
        dto.setProfessionalTitle(doctor.getProfessionalTitle());
        dto.setHospital(doctor.getHospital());
        dto.setDepartment(doctor.getDepartment());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        dto.setSpecialty(doctor.getSpecialty());
        dto.setPracticeYears(doctor.getPracticeYears());
        
        return dto;
    }

    @Override
    public PatientProfileDTO getPatientProfile(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        Patient patient = patientRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        PatientProfileDTO dto = new PatientProfileDTO();
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(patient.getFullName());
        dto.setAge(patient.getAge());
        dto.setGender(patient.getGender());
        dto.setPhone(patient.getPhone());
        dto.setEmergencyContact(patient.getEmergencyContact());
        dto.setEmergencyPhone(patient.getEmergencyPhone());
        
        return dto;
    }

    @Override
    @Transactional
    public void updateDoctorProfile(String username, DoctorProfileDTO profileDTO) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        Doctor doctor = doctorRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        user.setEmail(profileDTO.getEmail());
        userRepository.save(user);

        doctor.setFullName(profileDTO.getFullName());
        doctor.setAge(profileDTO.getAge());
        doctor.setGender(profileDTO.getGender());
        doctor.setPhone(profileDTO.getPhone());
        doctor.setProfessionalTitle(profileDTO.getProfessionalTitle());
        doctor.setHospital(profileDTO.getHospital());
        doctor.setDepartment(profileDTO.getDepartment());
        doctor.setLicenseNumber(profileDTO.getLicenseNumber());
        doctor.setSpecialty(profileDTO.getSpecialty());
        doctor.setPracticeYears(profileDTO.getPracticeYears());
        
        doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public void updatePatientProfile(String username, PatientProfileDTO profileDTO) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        Patient patient = patientRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        user.setEmail(profileDTO.getEmail());
        userRepository.save(user);

        patient.setFullName(profileDTO.getFullName());
        patient.setAge(profileDTO.getAge());
        patient.setGender(profileDTO.getGender());
        patient.setPhone(profileDTO.getPhone());
        patient.setEmergencyContact(profileDTO.getEmergencyContact());
        patient.setEmergencyPhone(profileDTO.getEmergencyPhone());
        
        patientRepository.save(patient);
    }
} 