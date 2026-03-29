package com.example.medicalsystem.dto;

import lombok.Data;
import java.util.List;

@Data
public class HealthStatsDTO {
    private List<DailyStatDTO> bloodPressureStats;  // 血压统计
    private List<DailyStatDTO> heartRateStats;      // 心率统计
    private List<DailyStatDTO> bloodSugarStats;     // 血糖统计
    private List<DailyStatDTO> temperatureStats;    // 体温统计
    private List<DailyStatDTO> weightStats;         // 体重统计
    
    @Data
    public static class DailyStatDTO {
        private String date;
        private Double value;           // 主要值（收缩压/心率/血糖等）
        private Double secondaryValue;  // 次要值（舒张压）
        private Double minValue;        // 最小值
        private Double maxValue;        // 最大值
    }
} 