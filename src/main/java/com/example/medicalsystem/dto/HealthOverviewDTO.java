package com.example.medicalsystem.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HealthOverviewDTO {
    private BloodPressureData lastBloodPressure;
    private HealthData lastHeartRate;
    private HealthData lastBloodSugar;
    private HealthData lastTemperature;
    private HealthData lastWeight;

    @Data
    public static class BloodPressureData {
        private Integer systolic;
        private Integer diastolic;
        private LocalDateTime date;
    }

    @Data
    public static class HealthData {
        private Double value;
        private LocalDateTime date;
    }
} 