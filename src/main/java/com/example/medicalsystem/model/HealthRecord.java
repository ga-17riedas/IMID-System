package com.example.medicalsystem.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "health_records")
public class HealthRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "record_type", nullable = false)
    private String recordType = "REGULAR";

    @Column(name = "value1", nullable = false)
    private Double value1;

    @Column(name = "value2")
    private Double value2;

    @Column(name = "unit")
    private String unit;

    @Column(name = "measure_time")
    private LocalDateTime measureTime;

    @Column(name = "notes")
    private String notes;

    @Column(name = "systolic_pressure")
    private Double systolicPressure;

    @Column(name = "diastolic_pressure")
    private Double diastolicPressure;

    @Column(name = "heart_rate")
    private Double heartRate;

    @Column(name = "blood_sugar")
    private Double bloodSugar;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "symptoms")
    private String symptoms;

    @Column(name = "medications")
    private String medications;

    @Column(name = "record_time")
    private LocalDateTime recordTime;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (recordTime == null) {
            recordTime = LocalDateTime.now();
        }
        if (measureTime == null) {
            measureTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 