package com.example.medicalsystem.service;

import com.example.medicalsystem.dto.HealthOverviewDTO;
import com.example.medicalsystem.dto.HealthRecordDTO;
import java.util.List;

public interface PatientHomeService {
    /**
     * 获取健康数据概览
     */
    HealthOverviewDTO getHealthOverview(String username);

    /**
     * 获取最近的健康记录
     */
    List<HealthRecordDTO> getRecentRecords(String username);
} 