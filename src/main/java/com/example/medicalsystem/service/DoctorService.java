package com.example.medicalsystem.service;

import com.example.medicalsystem.dto.DoctorHomeDTO;
import com.example.medicalsystem.dto.DoctorProfileDTO;
import com.example.medicalsystem.dto.MedicalImageDTO;
import com.example.medicalsystem.dto.DiagnosisReportDTO;
import com.example.medicalsystem.dto.PatientDTO;
import com.example.medicalsystem.dto.DoctorStatisticsDTO;
import com.example.medicalsystem.dto.ScheduleDTO;
import com.example.medicalsystem.dto.MedicalHistoryDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import com.example.medicalsystem.dto.DoctorDTO;
import com.example.medicalsystem.model.Doctor;

public interface DoctorService {
    DoctorHomeDTO getDoctorHomeData(String username);
    DoctorDTO getDoctorProfile(String username);
    void updateDoctorProfile(String username, DoctorDTO profileDTO);
    
    // 影像诊断相关
    MedicalImageDTO uploadMedicalImage(String username, Long patientId, MultipartFile image, String imageType, String bodyPart);
    List<MedicalImageDTO> getPatientImages(Long patientId);
    MedicalImageDTO analyzeMedicalImage(Long imageId);
    void updateDiagnosis(Long imageId, String doctorDiagnosis);
    
    // 诊断报告相关
    DiagnosisReportDTO createDiagnosisReport(String username, DiagnosisReportDTO reportDTO);
    List<DiagnosisReportDTO> getPatientReports(Long patientId);
    DiagnosisReportDTO getReport(Long reportId);

    // 患者管理相关
    List<PatientDTO> searchPatients(String query);
    PatientDTO getPatientDetail(Long patientId);
    List<PatientDTO> getRecentPatients(String username);

    // 统计分析相关
    DoctorStatisticsDTO getDoctorStatistics(String username);
    DoctorStatisticsDTO getDoctorStatisticsByDateRange(String username, String startDate, String endDate);

    // 排班管理相关
    List<ScheduleDTO> getSchedules(String username, Integer year, Integer month);
    ScheduleDTO createSchedule(String username, ScheduleDTO scheduleDTO);
    ScheduleDTO updateSchedule(String username, Long scheduleId, ScheduleDTO scheduleDTO);
    void deleteSchedule(String username, Long scheduleId);

    List<PatientDTO> getAllPatients();
    List<MedicalHistoryDTO> getPatientHistory(Long patientId);
    PatientDTO updatePatient(Long patientId, PatientDTO patientDTO);

    void addPatient(String username, Long patientId, String relationshipType);
    void removePatient(String username, Long patientId);
    List<PatientDTO> getMyPatients(String username);

    List<PatientDTO> getPatients(String username);

    Doctor getDoctorByUsername(String username);

    Doctor findByUsername(String username);
} 