package com.example.medicalsystem.service;

import com.example.medicalsystem.dto.AnalysisStatisticsDTO;
import com.example.medicalsystem.dto.AnalysisChartDataDTO;
import java.time.LocalDateTime;

public interface AnalysisService {
    AnalysisStatisticsDTO getStatistics(LocalDateTime startDate, LocalDateTime endDate);
    AnalysisChartDataDTO getChartData(LocalDateTime startDate, LocalDateTime endDate);
} 