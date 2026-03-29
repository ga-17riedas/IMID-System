package com.example.medicalsystem.service.impl;

import com.example.medicalsystem.dto.DoctorHomeStatsDTO;
import com.example.medicalsystem.model.Doctor;
import com.example.medicalsystem.repository.*;
import com.example.medicalsystem.service.DoctorHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Service
public class DoctorHomeServiceImpl implements DoctorHomeService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private MedicalImageRepository medicalImageRepository;

    @Autowired
    private DiagnosisReportRepository diagnosisReportRepository;

    @Autowired
    private DoctorPatientRepository doctorPatientRepository;

    @Override
    public DoctorHomeStatsDTO getHomeStats(String username) {
        Doctor doctor = doctorRepository.findByUser_Username(username)
            .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DoctorHomeStatsDTO stats = new DoctorHomeStatsDTO();

        // 获取待分析影像数量（未诊断的影像）
        Integer pendingImages = medicalImageRepository.countByDoctorIdAndDoctorDiagnosisIsNull(doctor.getId());
        stats.setPendingImages(pendingImages != null ? pendingImages : 0);

        // 获取今日患者数量
        Integer todayPatients = doctorPatientRepository.countTodayPatients(doctor.getId());
        stats.setTodayPatients(todayPatients != null ? todayPatients : 0);

        // 获取总诊断报告数
        Integer totalReports = diagnosisReportRepository.countByDoctorId(doctor.getId());
        stats.setTotalReports(totalReports != null ? totalReports : 0);

        return stats;
    }
} 