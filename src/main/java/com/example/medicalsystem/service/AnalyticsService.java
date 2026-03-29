package com.example.medicalsystem.service;

import com.example.medicalsystem.dto.AnalyticsDataDTO;
import java.time.LocalDate;

public interface AnalyticsService {
    AnalyticsDataDTO getAnalyticsData(LocalDate startDate, LocalDate endDate);
} 