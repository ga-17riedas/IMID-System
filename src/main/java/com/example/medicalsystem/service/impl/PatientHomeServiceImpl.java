package com.example.medicalsystem.service.impl;

import com.example.medicalsystem.dto.HealthOverviewDTO;
import com.example.medicalsystem.dto.HealthRecordDTO;
import com.example.medicalsystem.model.HealthRecord;
import com.example.medicalsystem.model.Patient;
import com.example.medicalsystem.repository.HealthRecordRepository;
import com.example.medicalsystem.repository.PatientRepository;
import com.example.medicalsystem.service.PatientHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientHomeServiceImpl implements PatientHomeService {

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Override
    @Transactional(readOnly = true)
    public HealthOverviewDTO getHealthOverview(String username) {
        Patient patient = patientRepository.findByUser_Username(username)
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        List<HealthRecord> records = healthRecordRepository.findTop5ByPatientIdOrderByRecordTimeDesc(patient.getId());
        
        HealthOverviewDTO overview = new HealthOverviewDTO();
        
        // 处理血压数据
        records.stream()
            .filter(r -> r.getSystolicPressure() != null && r.getDiastolicPressure() != null)
            .findFirst()
            .ifPresent(r -> {
                HealthOverviewDTO.BloodPressureData bp = new HealthOverviewDTO.BloodPressureData();
                bp.setSystolic(r.getSystolicPressure().intValue());
                bp.setDiastolic(r.getDiastolicPressure().intValue());
                bp.setDate(r.getRecordTime());
                overview.setLastBloodPressure(bp);
            });

        // 处理心率数据
        records.stream()
            .filter(r -> r.getHeartRate() != null)
            .findFirst()
            .ifPresent(r -> {
                HealthOverviewDTO.HealthData hr = new HealthOverviewDTO.HealthData();
                hr.setValue(r.getHeartRate());
                hr.setDate(r.getRecordTime());
                overview.setLastHeartRate(hr);
            });

        // 处理血糖数据
        records.stream()
            .filter(r -> r.getBloodSugar() != null)
            .findFirst()
            .ifPresent(r -> {
                HealthOverviewDTO.HealthData bs = new HealthOverviewDTO.HealthData();
                bs.setValue(r.getBloodSugar());
                bs.setDate(r.getRecordTime());
                overview.setLastBloodSugar(bs);
            });

        return overview;
    }

    @Override
    @Transactional(readOnly = true)
    public List<HealthRecordDTO> getRecentRecords(String username) {
        Patient patient = patientRepository.findByUser_Username(username)
            .orElseThrow(() -> new RuntimeException("Patient not found"));
            
        return healthRecordRepository.findTop5ByPatientIdOrderByRecordTimeDesc(patient.getId())
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    private HealthRecordDTO convertToDTO(HealthRecord record) {
        HealthRecordDTO dto = new HealthRecordDTO();
        dto.setId(record.getId());
        dto.setRecordTime(record.getRecordTime());
        dto.setSystolicPressure(record.getSystolicPressure());
        dto.setDiastolicPressure(record.getDiastolicPressure());
        dto.setHeartRate(record.getHeartRate());
        dto.setBloodSugar(record.getBloodSugar());
        dto.setTemperature(record.getTemperature());
        dto.setWeight(record.getWeight());
        dto.setSymptoms(record.getSymptoms());
        dto.setMedications(record.getMedications());
        dto.setNotes(record.getNotes());
        return dto;
    }
} 