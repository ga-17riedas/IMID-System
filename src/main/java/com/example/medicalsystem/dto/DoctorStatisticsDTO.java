package com.example.medicalsystem.dto;

import lombok.Data;
import java.util.Map;
import java.util.List;

@Data
public class DoctorStatisticsDTO {
    // 基本统计
    private Integer totalPatients;
    private Integer totalDiagnoses;
    private Integer totalImages;
    
    // 最近30天统计
    private Integer newPatientsLast30Days;
    private Integer diagnosesLast30Days;
    private Integer imagesLast30Days;
    
    // 诊断类型分布
    private Map<String, Integer> diagnosisTypeDistribution;
    
    // 每日诊断数量趋势（最近30天）
    private List<DailyStatDTO> dailyDiagnosisTrend;
    
    // AI诊断准确率统计
    private Double aiDiagnosisAccuracy;
    private Integer aiDiagnosisTotal;
    
    private List<DailyStatDTO> dailyStats;
    
    @Data
    public static class DailyStatDTO {
        private String date;
        private Integer count;
    }
} 