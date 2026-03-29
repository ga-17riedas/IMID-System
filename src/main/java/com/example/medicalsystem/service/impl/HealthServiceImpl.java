package com.example.medicalsystem.service.impl;

import com.example.medicalsystem.dto.HealthRecordDTO;
import com.example.medicalsystem.dto.HealthStatsDTO;
import com.example.medicalsystem.model.HealthRecord;
import com.example.medicalsystem.model.Patient;
import com.example.medicalsystem.repository.HealthRecordRepository;
import com.example.medicalsystem.repository.PatientRepository;
import com.example.medicalsystem.service.HealthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HealthServiceImpl implements HealthService {

    @Autowired
    private HealthRecordRepository healthRecordRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Override
    @Transactional
    public HealthRecordDTO createHealthRecord(String username, HealthRecordDTO recordDTO) {
        Patient patient = patientRepository.findByUser_Username(username)
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        HealthRecord record = new HealthRecord();
        record.setPatient(patient);
        record.setRecordTime(LocalDateTime.now());  // 使用当前时间
        
        // 根据填写的数据自动判断记录类型
        if (recordDTO.getSystolicPressure() != null && recordDTO.getDiastolicPressure() != null) {
            record.setRecordType("BLOOD_PRESSURE");
            record.setValue1(recordDTO.getSystolicPressure());
            record.setValue2(recordDTO.getDiastolicPressure());
            record.setUnit("mmHg");
        }
        if (recordDTO.getHeartRate() != null) {
            record.setRecordType("HEART_RATE");
            record.setValue1(recordDTO.getHeartRate());
            record.setUnit("bpm");
        }
        if (recordDTO.getBloodSugar() != null) {
            record.setRecordType("BLOOD_SUGAR");
            record.setValue1(recordDTO.getBloodSugar());
            record.setUnit("mmol/L");
        }
        if (recordDTO.getTemperature() != null) {
            record.setRecordType("TEMPERATURE");
            record.setValue1(recordDTO.getTemperature());
            record.setUnit("°C");
        }
        if (recordDTO.getWeight() != null) {
            record.setRecordType("WEIGHT");
            record.setValue1(recordDTO.getWeight());
            record.setUnit("kg");
        }

        // 设置其他字段
        record.setSystolicPressure(recordDTO.getSystolicPressure());
        record.setDiastolicPressure(recordDTO.getDiastolicPressure());
        record.setHeartRate(recordDTO.getHeartRate());
        record.setBloodSugar(recordDTO.getBloodSugar());
        record.setTemperature(recordDTO.getTemperature());
        record.setWeight(recordDTO.getWeight());
        record.setSymptoms(recordDTO.getSymptoms());
        record.setMedications(recordDTO.getMedications());
        record.setNotes(recordDTO.getNotes());

        record = healthRecordRepository.save(record);
        return convertToDTO(record);
    }

    private HealthRecordDTO convertToDTO(HealthRecord record) {
        HealthRecordDTO dto = new HealthRecordDTO();
        dto.setId(record.getId());
        dto.setRecordTime(record.getRecordTime());
        dto.setRecordType(record.getRecordType());
        dto.setValue1(record.getValue1());
        dto.setValue2(record.getValue2());
        dto.setUnit(record.getUnit());
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

    @Override
    public List<HealthRecordDTO> getHealthRecords(String username) {
        Patient patient = patientRepository.findByUser_Username(username)
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        return healthRecordRepository.findByPatientIdOrderByRecordTimeDesc(patient.getId())
            .stream()
            .map(record -> {
                HealthRecordDTO dto = new HealthRecordDTO();
                BeanUtils.copyProperties(record, dto);
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Override
    public HealthStatsDTO getHealthStats(String username) {
        Patient patient = patientRepository.findByUser_Username(username)
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        List<HealthRecord> records = healthRecordRepository.findByPatientIdOrderByRecordTimeDesc(patient.getId());
        HealthStatsDTO stats = new HealthStatsDTO();

        // 血压统计
        stats.setBloodPressureStats(records.stream()
            .filter(r -> r.getSystolicPressure() != null && r.getDiastolicPressure() != null)
            .map(r -> {
                HealthStatsDTO.DailyStatDTO stat = new HealthStatsDTO.DailyStatDTO();
                stat.setDate(r.getRecordTime().toLocalDate().toString());
                stat.setValue(r.getSystolicPressure());
                stat.setSecondaryValue(r.getDiastolicPressure());
                return stat;
            })
            .collect(Collectors.groupingBy(
                HealthStatsDTO.DailyStatDTO::getDate,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> {
                        HealthStatsDTO.DailyStatDTO result = new HealthStatsDTO.DailyStatDTO();
                        result.setDate(list.get(0).getDate());
                        result.setValue(list.get(0).getValue());
                        result.setSecondaryValue(list.get(0).getSecondaryValue());
                        return result;
                    }
                )
            ))
            .values()
            .stream()
            .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
            .collect(Collectors.toList()));

        // 心率统计
        stats.setHeartRateStats(calculateDailyStats(records, r -> {
            HealthStatsDTO.DailyStatDTO stat = new HealthStatsDTO.DailyStatDTO();
            stat.setDate(r.getRecordTime().toLocalDate().toString());
            stat.setValue(r.getHeartRate());
            return stat;
        }));

        // 血糖统计
        stats.setBloodSugarStats(calculateDailyStats(records, r -> {
            HealthStatsDTO.DailyStatDTO stat = new HealthStatsDTO.DailyStatDTO();
            stat.setDate(r.getRecordTime().toLocalDate().toString());
            stat.setValue(r.getBloodSugar());
            return stat;
        }));

        // 体温统计
        stats.setTemperatureStats(calculateDailyStats(records, r -> {
            HealthStatsDTO.DailyStatDTO stat = new HealthStatsDTO.DailyStatDTO();
            stat.setDate(r.getRecordTime().toLocalDate().toString());
            stat.setValue(r.getTemperature());
            return stat;
        }));

        // 体重统计
        stats.setWeightStats(calculateDailyStats(records, r -> {
            HealthStatsDTO.DailyStatDTO stat = new HealthStatsDTO.DailyStatDTO();
            stat.setDate(r.getRecordTime().toLocalDate().toString());
            stat.setValue(r.getWeight());
            return stat;
        }));

        return stats;
    }

    private List<HealthStatsDTO.DailyStatDTO> calculateDailyStats(
            List<HealthRecord> records,
            java.util.function.Function<HealthRecord, HealthStatsDTO.DailyStatDTO> mapper) {
        
        return records.stream()
            .map(mapper)
            .collect(Collectors.groupingBy(
                HealthStatsDTO.DailyStatDTO::getDate,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    list -> {
                        HealthStatsDTO.DailyStatDTO result = new HealthStatsDTO.DailyStatDTO();
                        result.setDate(list.get(0).getDate());
                        
                        double sum = list.stream()
                            .mapToDouble(HealthStatsDTO.DailyStatDTO::getValue)
                            .sum();
                        double avg = sum / list.size();
                        
                        result.setValue(avg);
                        result.setMinValue(list.stream()
                            .mapToDouble(HealthStatsDTO.DailyStatDTO::getValue)
                            .min()
                            .orElse(0.0));
                        result.setMaxValue(list.stream()
                            .mapToDouble(HealthStatsDTO.DailyStatDTO::getValue)
                            .max()
                            .orElse(0.0));
                        
                        if (list.get(0).getSecondaryValue() != null) {
                            double secondarySum = list.stream()
                                .mapToDouble(HealthStatsDTO.DailyStatDTO::getSecondaryValue)
                                .sum();
                            result.setSecondaryValue(secondarySum / list.size());
                        }
                        
                        return result;
                    }
                )))
            .values()
            .stream()
            .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HealthRecordDTO updateHealthRecord(String username, Long recordId, HealthRecordDTO recordDTO) {
        Patient patient = patientRepository.findByUser_Username(username)
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        HealthRecord record = healthRecordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("Health record not found"));

        if (!record.getPatient().getId().equals(patient.getId())) {
            throw new RuntimeException("Not authorized to update this record");
        }

        BeanUtils.copyProperties(recordDTO, record, "id", "patient", "createdAt", "recordTime");
        record = healthRecordRepository.save(record);

        HealthRecordDTO resultDTO = new HealthRecordDTO();
        BeanUtils.copyProperties(record, resultDTO);
        return resultDTO;
    }

    @Override
    @Transactional
    public void deleteHealthRecord(String username, Long recordId) {
        Patient patient = patientRepository.findByUser_Username(username)
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        HealthRecord record = healthRecordRepository.findById(recordId)
            .orElseThrow(() -> new RuntimeException("Health record not found"));

        if (!record.getPatient().getId().equals(patient.getId())) {
            throw new RuntimeException("Not authorized to delete this record");
        }

        healthRecordRepository.delete(record);
    }
} 