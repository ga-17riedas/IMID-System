package com.example.medicalsystem.service.impl;

import com.example.medicalsystem.dto.DoctorHomeDTO;
import com.example.medicalsystem.dto.DoctorProfileDTO;
import com.example.medicalsystem.dto.MedicalImageDTO;
import com.example.medicalsystem.dto.DiagnosisReportDTO;
import com.example.medicalsystem.dto.PatientDTO;
import com.example.medicalsystem.dto.DoctorStatisticsDTO;
import com.example.medicalsystem.dto.ScheduleDTO;
import com.example.medicalsystem.dto.MedicalHistoryDTO;
import com.example.medicalsystem.dto.DoctorDTO;
import com.example.medicalsystem.model.Doctor;
import com.example.medicalsystem.model.MedicalImage;
import com.example.medicalsystem.model.Patient;
import com.example.medicalsystem.model.DiagnosisReport;
import com.example.medicalsystem.model.Schedule;
import com.example.medicalsystem.model.User;
import com.example.medicalsystem.model.DoctorPatient;
import com.example.medicalsystem.repository.DoctorRepository;
import com.example.medicalsystem.repository.UserRepository;
import com.example.medicalsystem.repository.MedicalImageRepository;
import com.example.medicalsystem.repository.PatientRepository;
import com.example.medicalsystem.repository.DiagnosisReportRepository;
import com.example.medicalsystem.repository.ScheduleRepository;
import com.example.medicalsystem.repository.DoctorPatientRepository;
import com.example.medicalsystem.service.DoctorService;
import com.example.medicalsystem.service.ImageStorageService;
import com.example.medicalsystem.service.MedicalAnalysisService;
import com.example.medicalsystem.dto.AnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.time.Period;
import lombok.extern.slf4j.Slf4j;
import com.example.medicalsystem.exception.ResourceNotFoundException;
import com.example.medicalsystem.service.MedicalImageService;

@Slf4j
@Service
@Transactional
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicalImageRepository medicalImageRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ImageStorageService imageStorageService;

    @Autowired
    private MedicalImageService medicalImageService;

    @Autowired
    private DiagnosisReportRepository diagnosisReportRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private DoctorPatientRepository doctorPatientRepository;

    @Autowired
    private MedicalAnalysisService medicalAnalysisService;

    @Override
    public Doctor getDoctorByUsername(String username) {
        return doctorRepository.findByUser_Username(username)
            .orElseThrow(() -> new RuntimeException("医生不存在"));
    }

    @Override
    public DoctorHomeDTO getDoctorHomeData(String username) {
        Doctor doctor = getDoctorByUsername(username);

        DoctorHomeDTO homeDTO = new DoctorHomeDTO();
        homeDTO.setFullName(doctor.getFullName());
        homeDTO.setProfessionalTitle(doctor.getProfessionalTitle());
        homeDTO.setHospital(doctor.getHospital());
        homeDTO.setDepartment(doctor.getDepartment());
        homeDTO.setWelcomeMessage("欢迎回来，" + doctor.getFullName() + "医生");
        
        return homeDTO;
    }

    @Override
    public DoctorDTO getDoctorProfile(String username) {
        log.debug("Getting profile for username: {}", username);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Doctor doctor = doctorRepository.findByUserId(user.getId())
            .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return convertToDTO(doctor, user);
    }

    @Override
    public void updateDoctorProfile(String username, DoctorDTO profileDTO) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Doctor doctor = doctorRepository.findByUserId(user.getId())
            .orElseThrow(() -> new RuntimeException("Doctor not found"));

        updateDoctorFromDTO(doctor, profileDTO);
        doctorRepository.save(doctor);
    }

    private DoctorDTO convertToDTO(Doctor doctor, User user) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doctor.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(doctor.getFullName());
        dto.setProfessionalTitle(doctor.getProfessionalTitle());
        dto.setDepartment(doctor.getDepartment());
        dto.setSpecialty(doctor.getSpecialty());
        dto.setPhone(doctor.getPhone());
        dto.setHospital(doctor.getHospital());
        dto.setLicenseNumber(doctor.getLicenseNumber());
        return dto;
    }

    private void updateDoctorFromDTO(Doctor doctor, DoctorDTO dto) {
        doctor.setFullName(dto.getFullName());
        doctor.setProfessionalTitle(dto.getProfessionalTitle());
        doctor.setDepartment(dto.getDepartment());
        doctor.setSpecialty(dto.getSpecialty());
        doctor.setPhone(dto.getPhone());
        doctor.setHospital(dto.getHospital());
        doctor.setLicenseNumber(dto.getLicenseNumber());
    }

    @Override
    public MedicalImageDTO uploadMedicalImage(String username, Long patientId, MultipartFile image, 
            String imageType, String bodyPart) {
        Doctor doctor = getDoctorByUsername(username);
        
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        try {
            // 使用MedicalImageService保存图片，确保URL正确设置
            MedicalImage medicalImage = medicalImageService.saveImage(image, patientId, doctor.getId());
            
            // 更新其他字段
            medicalImage.setImageType(imageType);
            medicalImage.setBodyPart(bodyPart);
            
            medicalImage = medicalImageRepository.save(medicalImage);
            return convertToMedicalImageDTO(medicalImage);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store image", e);
        }
    }

    @Override
    public List<MedicalImageDTO> getPatientImages(Long patientId) {
        return medicalImageRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
            .stream()
            .map(this::convertToMedicalImageDTO)
            .collect(Collectors.toList());
    }

    @Override
    public MedicalImageDTO analyzeMedicalImage(Long imageId) {
        MedicalImage image = medicalImageRepository.findById(imageId)
            .orElseThrow(() -> new RuntimeException("Image not found"));
            
        // TODO: 调用AI服务进行分析
        image.setAiDiagnosis("AI分析结果示例");
        image.setConfidenceScore(0.95);
        
        return convertToMedicalImageDTO(medicalImageRepository.save(image));
    }

    @Override
    public void updateDiagnosis(Long imageId, String doctorDiagnosis) {
        MedicalImage image = medicalImageRepository.findById(imageId)
            .orElseThrow(() -> new RuntimeException("Image not found"));
        
        image.setDoctorDiagnosis(doctorDiagnosis);
        medicalImageRepository.save(image);
    }

    @Override
    @Transactional
    public DiagnosisReportDTO createDiagnosisReport(String username, DiagnosisReportDTO reportDTO) {
        Doctor doctor = getDoctorByUsername(username);
        
        Patient patient = patientRepository.findById(reportDTO.getPatientId())
            .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        MedicalImage medicalImage = null;
        if (reportDTO.getMedicalImageId() != null) {
            medicalImage = medicalImageRepository.findById(reportDTO.getMedicalImageId())
                .orElseThrow(() -> new RuntimeException("Medical image not found"));
        }
        
        DiagnosisReport report = new DiagnosisReport();
        report.setPatient(patient);
        report.setDoctor(doctor);
        report.setMedicalImage(medicalImage);
        report.setDiagnosis(reportDTO.getDiagnosis());
        report.setTreatmentPlan(reportDTO.getTreatmentPlan());
        report.setRecommendations(reportDTO.getRecommendations());
        
        report = diagnosisReportRepository.save(report);
        return convertToDTO(report);
    }

    @Override
    public List<DiagnosisReportDTO> getPatientReports(Long patientId) {
        return diagnosisReportRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public DiagnosisReportDTO getReport(Long reportId) {
        DiagnosisReport report = diagnosisReportRepository.findById(reportId)
            .orElseThrow(() -> new RuntimeException("Report not found"));
        return convertToDTO(report);
    }

    @Override
    public List<PatientDTO> searchPatients(String query) {
        return patientRepository.searchPatients(query)
            .stream()
            .map(this::convertToPatientDTO)
            .collect(Collectors.toList());
    }

    @Override
    public PatientDTO getPatientDetail(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new RuntimeException("Patient not found"));
        return convertToPatientDTO(patient);
    }

    @Override
    public List<PatientDTO> getRecentPatients(String username) {
        Doctor doctor = getDoctorByUsername(username);
        
        // 获取最近有诊断记录的患者
        return diagnosisReportRepository.findRecentPatientsByDoctorId(doctor.getId())
            .stream()
            .map(this::convertToPatientDTO)
            .collect(Collectors.toList());
    }

    @Override
    public DoctorStatisticsDTO getDoctorStatistics(String username) {
        Doctor doctor = getDoctorByUsername(username);

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);

        List<Object[]> dailyReports = diagnosisReportRepository.getDailyReportCountsInRange(
            doctor.getId(),
            startDate,
            endDate
        );

        List<DoctorStatisticsDTO.DailyStatDTO> dailyStats = dailyReports.stream()
            .map(row -> {
                DoctorStatisticsDTO.DailyStatDTO stat = new DoctorStatisticsDTO.DailyStatDTO();
                if (row[0] instanceof java.sql.Date) {
                    java.sql.Date sqlDate = (java.sql.Date) row[0];
                    stat.setDate(sqlDate.toLocalDate().toString());
                } else if (row[0] instanceof java.sql.Timestamp) {
                    java.sql.Timestamp timestamp = (java.sql.Timestamp) row[0];
                    stat.setDate(timestamp.toLocalDateTime().toLocalDate().toString());
                } else {
                    stat.setDate(row[0].toString());
                }
                stat.setCount(((Number) row[1]).intValue());
                return stat;
            })
            .collect(Collectors.toList());

        DoctorStatisticsDTO statistics = new DoctorStatisticsDTO();
        statistics.setDailyStats(dailyStats);
        statistics.setTotalPatients(diagnosisReportRepository.countTotalPatientsByDoctor(doctor.getId()));
        statistics.setTotalDiagnoses(diagnosisReportRepository.countReportsByDoctorSince(doctor.getId(), LocalDateTime.MIN));
        statistics.setTotalImages(medicalImageRepository.countImagesByDoctor(doctor.getId()));
        statistics.setNewPatientsLast30Days(diagnosisReportRepository.countNewPatientsByDoctorSince(doctor.getId(), startDate));
        statistics.setDiagnosesLast30Days(diagnosisReportRepository.countReportsByDoctorSince(doctor.getId(), startDate));
        statistics.setImagesLast30Days(medicalImageRepository.countImagesByDoctorSince(doctor.getId(), startDate));
        
        Double accuracy = medicalImageRepository.calculateAiAccuracy(doctor.getId());
        statistics.setAiDiagnosisAccuracy(accuracy != null ? accuracy : 0.0);
        statistics.setAiDiagnosisTotal(medicalImageRepository.countAiDiagnosisInRange(doctor.getId(), startDate, endDate));

        return statistics;
    }

    @Override
    public DoctorStatisticsDTO getDoctorStatisticsByDateRange(String username, String startDate, String endDate) {
        Doctor doctor = getDoctorByUsername(username);
        
        LocalDateTime start = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate).atTime(23, 59, 59);
        
        DoctorStatisticsDTO stats = new DoctorStatisticsDTO();
        
        // 指定时间范围内的统计
        stats.setTotalPatients(diagnosisReportRepository.countPatientsByDoctorInRange(doctor.getId(), start, end));
        stats.setTotalDiagnoses(diagnosisReportRepository.countReportsByDoctorInRange(doctor.getId(), start, end));
        stats.setTotalImages(medicalImageRepository.countImagesByDoctorInRange(doctor.getId(), start, end));
        
        // 诊断类型分布
        List<Object[]> diagnosisTypesList = diagnosisReportRepository.getDiagnosisTypeDistribution(
            doctor.getId(), start, end);
        Map<String, Integer> diagnosisTypes = diagnosisTypesList.stream()
            .collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).intValue()
            ));
        stats.setDiagnosisTypeDistribution(diagnosisTypes);
        
        // 每日诊断趋势
        List<Object[]> dailyCounts = diagnosisReportRepository.getDailyReportCountsInRange(
            doctor.getId(), start, end);
        stats.setDailyDiagnosisTrend(dailyCounts.stream()
            .map(row -> {
                DoctorStatisticsDTO.DailyStatDTO dailyStat = new DoctorStatisticsDTO.DailyStatDTO();
                dailyStat.setDate(((LocalDate) row[0]).format(DateTimeFormatter.ISO_DATE));
                dailyStat.setCount(((Number) row[1]).intValue());
                return dailyStat;
            })
            .collect(Collectors.toList()));
        
        // AI诊断准确率
        Double accuracy = medicalImageRepository.calculateAiAccuracyInRange(doctor.getId(), start, end);
        stats.setAiDiagnosisAccuracy(accuracy != null ? accuracy : 0.0);
        stats.setAiDiagnosisTotal(medicalImageRepository.countAiDiagnosisInRange(doctor.getId(), start, end));
        
        return stats;
    }

    @Override
    public List<ScheduleDTO> getSchedules(String username, Integer year, Integer month) {
        Doctor doctor = doctorRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            
        LocalDate startDate = year != null && month != null 
            ? LocalDate.of(year, month, 1)
            : LocalDate.now().withDayOfMonth(1);
            
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            
        return scheduleRepository.findByDoctorIdAndDateBetween(
                doctor.getId(), startDate, endDate)
            .stream()
            .map(this::convertToScheduleDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ScheduleDTO createSchedule(String username, ScheduleDTO scheduleDTO) {
        Doctor doctor = getDoctorByUsername(username);
        
        LocalDate date = LocalDate.parse(scheduleDTO.getDate());
        
        // 检查是否存在时间冲突
        List<Schedule> conflicts = scheduleRepository.findConflictingSchedules(
            doctor.getId(), date, scheduleDTO.getTimeSlot());
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("该时间段已有排班");
        }
        
        Schedule schedule = new Schedule();
        schedule.setDoctor(doctor);
        schedule.setDate(date);
        schedule.setTimeSlot(scheduleDTO.getTimeSlot());
        schedule.setMaxPatients(scheduleDTO.getMaxPatients());
        schedule.setNotes(scheduleDTO.getNotes());
        
        schedule = scheduleRepository.save(schedule);
        return convertToScheduleDTO(schedule);
    }

    @Override
    @Transactional
    public ScheduleDTO updateSchedule(String username, Long scheduleId, ScheduleDTO scheduleDTO) {
        Doctor doctor = getDoctorByUsername(username);
        
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new RuntimeException("Schedule not found"));
        
        if (!schedule.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("无权修改此排班");
        }
        
        LocalDate date = LocalDate.parse(scheduleDTO.getDate());
        
        // 检查是否存在时间冲突（排除当前排班）
        List<Schedule> conflicts = scheduleRepository.findConflictingSchedules(
            doctor.getId(), date, scheduleDTO.getTimeSlot());
        conflicts.removeIf(s -> s.getId().equals(scheduleId));
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("该时间段已有排班");
        }
        
        schedule.setDate(date);
        schedule.setTimeSlot(scheduleDTO.getTimeSlot());
        schedule.setMaxPatients(scheduleDTO.getMaxPatients());
        schedule.setNotes(scheduleDTO.getNotes());
        
        schedule = scheduleRepository.save(schedule);
        return convertToScheduleDTO(schedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(String username, Long scheduleId) {
        Doctor doctor = getDoctorByUsername(username);
        
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new RuntimeException("Schedule not found"));
        
        if (!schedule.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("无权删除此排班");
        }
        
        if (schedule.getBookedPatients() > 0) {
            // TODO: 处理已有预约的情况
            throw new RuntimeException("该排班已有预约，无法删除");
        }
        
        scheduleRepository.delete(schedule);
    }

    @Override
    public List<PatientDTO> getAllPatients() {
        try {
            // 获取所有未关联的患者
            List<Patient> patients = patientRepository.findPatientsNotAssociatedWithDoctor(
                getDoctorByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).getId()
            );
            return patients.stream()
                .map(this::convertToPatientDTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting all patients", e);
            throw new RuntimeException("获取患者列表失败");
        }
    }

    @Override
    public List<MedicalHistoryDTO> getPatientHistory(Long patientId) {
        try {
            Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
            
            List<DiagnosisReport> reports = diagnosisReportRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
            
            return reports.stream()
                .map(this::convertToMedicalHistoryDTO)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting patient history", e);
            throw new RuntimeException("获取病史记录失败");
        }
    }

    @Override
    @Transactional
    public PatientDTO updatePatient(Long patientId, PatientDTO patientDTO) {
        try {
            Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

            patient.setFullName(patientDTO.getFullName());
            patient.setGender(patientDTO.getGender());
            patient.setPhone(patientDTO.getPhone());
            patient.getUser().setEmail(patientDTO.getEmail());
            patient.setEmergencyContact(patientDTO.getEmergencyContact());
            patient.setEmergencyPhone(patientDTO.getEmergencyPhone());
            // 可以根据需要更新其他字段

            patient = patientRepository.save(patient);
            return convertToPatientDTO(patient);
        } catch (Exception e) {
            log.error("Error updating patient", e);
            throw new RuntimeException("更新患者信息失败");
        }
    }

    @Override
    @Transactional
    public void addPatient(String username, Long patientId, String relationshipType) {
        try {
            Doctor doctor = getDoctorByUsername(username);
            Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

            // 检查是否已经存在未结束的关系
            if (doctorPatientRepository.existsByDoctorIdAndPatientIdAndEndDateIsNull(
                doctor.getId(), patientId)) {
                throw new RuntimeException("该患者已经是您的患者");
            }

            DoctorPatient doctorPatient = new DoctorPatient();
            doctorPatient.setDoctor(doctor);
            doctorPatient.setPatient(patient);
            doctorPatient.setRelationshipType(relationshipType);
            doctorPatient.setStartDate(LocalDate.now());
            doctorPatient.setCreatedAt(LocalDateTime.now());
            doctorPatient.setUpdatedAt(LocalDateTime.now());

            doctorPatientRepository.save(doctorPatient);
        } catch (Exception e) {
            log.error("Error adding patient", e);
            throw new RuntimeException("添加患者失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removePatient(String username, Long patientId) {
        try {
            Doctor doctor = getDoctorByUsername(username);
            
            // 查找当前有效的医生-患者关系
            List<DoctorPatient> relationships = doctorPatientRepository
                .findCurrentPatientsByDoctorId(doctor.getId())
                .stream()
                .filter(dp -> dp.getPatient().getId().equals(patientId))
                .collect(Collectors.toList());

            if (relationships.isEmpty()) {
                throw new RuntimeException("未找到与该患者的有效关系");
            }

            // 设置结束日期
            relationships.forEach(dp -> {
                dp.setEndDate(LocalDate.now());
                dp.setUpdatedAt(LocalDateTime.now());
                doctorPatientRepository.save(dp);
            });
        } catch (Exception e) {
            log.error("Error removing patient", e);
            throw new RuntimeException("移除患者失败: " + e.getMessage());
        }
    }

    @Override
    public List<PatientDTO> getMyPatients(String username) {
        try {
            Doctor doctor = getDoctorByUsername(username);
            
            // 获取当前的患者列表
            return doctorPatientRepository.findCurrentPatientsByDoctorId(doctor.getId())
                .stream()
                .map(dp -> convertToPatientDTO(dp.getPatient()))
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting doctor's patients", e);
            throw new RuntimeException("获取患者列表失败: " + e.getMessage());
        }
    }

    @Override
    public List<PatientDTO> getPatients(String username) {
        Doctor doctor = doctorRepository.findByUser_Username(username)
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            
        return doctorPatientRepository.findCurrentPatientsByDoctorId(doctor.getId())
            .stream()
            .map(dp -> convertToPatientDTO(dp.getPatient()))
            .collect(Collectors.toList());
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
        dto.setPatientId(report.getPatientId());
        dto.setDiagnosis(report.getDiagnosis());
        dto.setTreatmentPlan(report.getTreatmentPlan());
        dto.setRecommendations(report.getRecommendations());
        dto.setReadStatus(report.getReadStatus());
        dto.setCreatedAt(report.getCreatedAt()); // Now passing LocalDateTime directly

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

    private PatientDTO convertToPatientDTO(Patient patient) {
        PatientDTO dto = new PatientDTO();
        dto.setId(patient.getId());
        dto.setUsername(patient.getUser().getUsername());
        dto.setFullName(patient.getFullName());
        dto.setGender(patient.getGender());
        dto.setAge(patient.getBirthDate() != null ? 
            Period.between(patient.getBirthDate(), LocalDate.now()).getYears() : null);
        dto.setPhone(patient.getPhone());
        if (patient.getUser() != null) {
            dto.setEmail(patient.getUser().getEmail());
        }
        dto.setEmergencyContact(patient.getEmergencyContact());
        dto.setEmergencyPhone(patient.getEmergencyPhone());
        // 获取患者的影像和报告数量
        dto.setImageCount(medicalImageRepository.countByPatientId(patient.getId()));
        dto.setReportCount(diagnosisReportRepository.countByPatientId(patient.getId()));
        // 获取最近一次就诊时间
        diagnosisReportRepository.findFirstByPatientIdOrderByCreatedAtDesc(patient.getId())
            .ifPresent(report -> dto.setLastVisit(
                report.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE)));

        return dto;
    }

    private ScheduleDTO convertToScheduleDTO(Schedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setDate(schedule.getDate().toString());
        dto.setTimeSlot(schedule.getTimeSlot());
        dto.setMaxPatients(schedule.getMaxPatients());
        dto.setBookedPatients(schedule.getBookedPatients());
        dto.setNotes(schedule.getNotes());
        return dto;
    }

    private void handleAIAnalysis(MedicalImage image) {
        try {
            AnalysisResult result = medicalAnalysisService.analyzeImage(
                image.getImageUrl(),
                image.getImageType()
            );
            
            image.setAiDiagnosis(result.getSuggestion());
            image.setConfidenceScore(result.getConfidence());
            medicalImageRepository.save(image);
            
        } catch (Exception e) {
            log.error("AI analysis failed for image {}: {}", image.getId(), e.getMessage());
            throw new RuntimeException("AI analysis failed: " + e.getMessage());
        }
    }

    private void validatePatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new RuntimeException("Patient not found");
        }
    }

    private void validatePatientAccess(Long patientId, Long doctorId) {
        if (!doctorPatientRepository.existsByDoctorIdAndPatientIdAndEndDateIsNull(doctorId, patientId)) {
            throw new RuntimeException("No active association between doctor and patient");
        }
    }

    private void processScheduleConflict(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new RuntimeException("Schedule not found"));
        
        List<Schedule> conflicts = scheduleRepository.findConflictingSchedules(
            schedule.getDoctor().getId(),
            schedule.getDate(),
            schedule.getTimeSlot()
        );
        
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Schedule conflict detected");
        }
    }

    private void processAIAnalysis(MedicalImage image) {
        AnalysisResult result = medicalAnalysisService.analyzeImage(
            image.getImageUrl(),
            image.getImageType()
        );
        
        image.setAiDiagnosis(result.getSuggestion());
        image.setConfidenceScore(result.getConfidence());
        medicalImageRepository.save(image);
    }

    @Override
    public Doctor findByUsername(String username) {
        return getDoctorByUsername(username);
    }

    private MedicalHistoryDTO convertToMedicalHistoryDTO(DiagnosisReport report) {
        MedicalHistoryDTO dto = new MedicalHistoryDTO();
        dto.setId(report.getId());
        dto.setDate(report.getCreatedAt().toLocalDate());
        dto.setDescription(report.getDiagnosis());
        dto.setDiagnosis(report.getDiagnosis());
        dto.setTreatment(report.getTreatment() != null ? report.getTreatment() : "");
        dto.setVisitDate(report.getCreatedAt());
        dto.setDoctorName(report.getDoctor().getFullName());
        dto.setDepartment(report.getDoctor().getDepartment());
        return dto;
    }
} 