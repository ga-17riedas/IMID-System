package com.example.medicalsystem.service.impl;

import com.example.medicalsystem.dto.PatientHomeDTO;
import com.example.medicalsystem.dto.PatientProfileDTO;
import com.example.medicalsystem.dto.MedicalImageDTO;
import com.example.medicalsystem.dto.DiagnosisReportDTO;
import com.example.medicalsystem.dto.PatientUpdateRequest;
import com.example.medicalsystem.model.Patient;
import com.example.medicalsystem.model.MedicalImage;
import com.example.medicalsystem.model.DiagnosisReport;
import com.example.medicalsystem.model.MedicalRecord;
import com.example.medicalsystem.repository.PatientRepository;
import com.example.medicalsystem.repository.MedicalImageRepository;
import com.example.medicalsystem.repository.DiagnosisReportRepository;
import com.example.medicalsystem.repository.MedicalRecordRepository;
import com.example.medicalsystem.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
import com.example.medicalsystem.exception.ResourceNotFoundException;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private MedicalImageRepository medicalImageRepository;

    @Autowired
    private DiagnosisReportRepository diagnosisReportRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Override
    public PatientHomeDTO getPatientHomeData(String username) {
        Patient patient = getPatientByUsername(username);
        PatientHomeDTO homeDTO = new PatientHomeDTO();
        homeDTO.setFullName(patient.getFullName());
        homeDTO.setAge(patient.getAge());
        homeDTO.setGender(patient.getGender());
        homeDTO.setWelcomeMessage("欢迎回来，" + patient.getFullName());
        
        // 获取待处理和总报告数
        homeDTO.setPendingReports(diagnosisReportRepository.countUnreadReports(patient.getId()));
        homeDTO.setTotalReports(diagnosisReportRepository.countByPatientId(patient.getId()));
        
        return homeDTO;
    }

    @Override
    public PatientProfileDTO getPatientProfile(String username) {
        Patient patient = getPatientByUsername(username);
        PatientProfileDTO profileDTO = new PatientProfileDTO();
        profileDTO.setFullName(patient.getFullName());
        profileDTO.setAge(patient.getAge());
        profileDTO.setGender(patient.getGender());
        profileDTO.setPhone(patient.getPhone());
        profileDTO.setEmail(patient.getUser().getEmail());
        profileDTO.setEmergencyContact(patient.getEmergencyContact());
        profileDTO.setEmergencyPhone(patient.getEmergencyPhone());
        
        return profileDTO;
    }

    @Override
    @Transactional
    public void updatePatientProfile(String username, PatientProfileDTO profileDTO) {
        Patient patient = getPatientByUsername(username);
        patient.setFullName(profileDTO.getFullName());
        patient.setAge(profileDTO.getAge());
        patient.setGender(profileDTO.getGender());
        patient.setPhone(profileDTO.getPhone());
        patient.getUser().setEmail(profileDTO.getEmail());
        patient.setEmergencyContact(profileDTO.getEmergencyContact());
        patient.setEmergencyPhone(profileDTO.getEmergencyPhone());

        patientRepository.save(patient);
    }

    @Override
    public List<MedicalImageDTO> getPatientImages(String username) {
        Patient patient = getPatientByUsername(username);
        return medicalImageRepository.findByPatientIdOrderByCreatedAtDesc(patient.getId())
            .stream()
            .map(this::convertToMedicalImageDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<DiagnosisReportDTO> getPatientReports(String username) {
        Patient patient = getPatientByUsername(username);
        return diagnosisReportRepository.findByPatientIdOrderByCreatedAtDesc(patient.getId())
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public Page<DiagnosisReport> getReports(String username, int page, int size, String search) {
        // 获取当前患者
        Patient patient = patientRepository.findByUser_Username(username)
            .orElseThrow(() -> new RuntimeException("Patient not found"));
            
        // 创建分页请求
        Pageable pageable = PageRequest.of(page, size);
        
        // 如果有搜索条件，则按诊断内容搜索
        if (StringUtils.hasText(search)) {
            return diagnosisReportRepository
                .findByPatientAndDiagnosisContainingOrderByCreatedAtDesc(patient, search, pageable);
        }
        
        // 否则返回所有报告
        return diagnosisReportRepository.findByPatientOrderByCreatedAtDesc(patient, pageable);
    }

    @Override
    public DiagnosisReport getReport(String username, Long reportId) {
        Patient patient = getPatientByUsername(username);
        return diagnosisReportRepository.findByIdAndPatient(reportId, patient)
            .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    @Override
    @Transactional
    public void markReportAsRead(String username, Long reportId) {
        Patient patient = getPatientByUsername(username);
        DiagnosisReport report = diagnosisReportRepository.findByIdAndPatient(reportId, patient)
            .orElseThrow(() -> new RuntimeException("Report not found"));
        report.setReadStatus(true);
        diagnosisReportRepository.save(report);
    }

    @Override
    @Deprecated
    public DiagnosisReportDTO getReport(Long reportId) {
        DiagnosisReport report = diagnosisReportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));
        return convertToDTO(report);
    }

    @Override
    public List<MedicalRecord> getMedicalRecords(String username) {
        Patient patient = getPatientByUsername(username);
        return medicalRecordRepository.findByPatientOrderByVisitDateDesc(patient);
    }

    @Override
    @Transactional
    public void updatePatient(Long patientId, PatientUpdateRequest request) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));
            
        patient.setFullName(request.getFullName());
        patient.setGender(request.getGender());
        patient.setPhone(request.getPhone());
        patient.setEmail(request.getEmail());
        patient.setEmergencyContact(request.getEmergencyContact());
        patient.setEmergencyPhone(request.getEmergencyPhone());

        
        patientRepository.save(patient);
    }

    private MedicalImageDTO convertToMedicalImageDTO(MedicalImage image) {
        MedicalImageDTO dto = new MedicalImageDTO();
        dto.setId(image.getId());
        dto.setPatientId(image.getPatient() != null ? image.getPatient().getId() : null);
        dto.setPatientName(image.getPatient() != null ? image.getPatient().getFullName() : null);
        
        // 使用imageUrl字段而不是imagePath
        dto.setImageUrl(image.getImageUrl());
        
        dto.setImageType(image.getImageType());
        dto.setBodyPart(image.getBodyPart());
        dto.setAiDiagnosis(image.getAiDiagnosis());
        dto.setConfidenceScore(image.getConfidenceScore());
        dto.setDoctorDiagnosis(image.getDoctorDiagnosis());
        dto.setCreatedAt(image.getCreatedAt() != null ? 
            image.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null);
        return dto;
    }

    private DiagnosisReportDTO convertToDTO(DiagnosisReport report) {
        DiagnosisReportDTO dto = new DiagnosisReportDTO();
        dto.setId(report.getId());
        dto.setPatientId(report.getPatient().getId());
        dto.setDiagnosis(report.getDiagnosis());
        dto.setTreatmentPlan(report.getTreatmentPlan());
        dto.setRecommendations(report.getRecommendations());
        dto.setReadStatus(report.getReadStatus());
        dto.setCreatedAt(report.getCreatedAt());

        // Set medical image data if available
        if (report.getMedicalImage() != null) {
            dto.setImageUrl(report.getMedicalImage().getImageUrl());
            dto.setMedicalImageId(report.getMedicalImage().getId());
        }

        // Set doctor data if available
        if (report.getDoctor() != null) {
            DiagnosisReportDTO.DoctorDTO doctorDTO = new DiagnosisReportDTO.DoctorDTO(
                report.getDoctor().getId(),
                report.getDoctor().getFullName(),
                report.getDoctor().getDepartment(),
                report.getDoctor().getProfessionalTitle()
            );
            dto.setDoctor(doctorDTO);
        }

        // Set patient name if available
        if (report.getPatient() != null) {
            dto.setPatientName(report.getPatient().getFullName());
        }

        return dto;
    }

    private Patient getPatientByUsername(String username) {
        return patientRepository.findByUser_Username(username)
            .orElseThrow(() -> new RuntimeException("Patient not found"));
    }
} 