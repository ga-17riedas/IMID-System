package com.example.medicalsystem.service;

import com.example.medicalsystem.dto.HealthRecordDTO;
import com.example.medicalsystem.dto.HealthStatsDTO;
import java.time.LocalDateTime;
import java.util.List;

public interface HealthService {
    HealthRecordDTO createHealthRecord(String username, HealthRecordDTO recordDTO);
    List<HealthRecordDTO> getHealthRecords(String username);
    HealthStatsDTO getHealthStats(String username);
    HealthRecordDTO updateHealthRecord(String username, Long recordId, HealthRecordDTO recordDTO);
    void deleteHealthRecord(String username, Long recordId);
} 