package com.example.medicalsystem.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

import com.example.medicalsystem.model.Patient;
import com.example.medicalsystem.model.Doctor;
import com.example.medicalsystem.model.MedicalImage;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;

@Data
@Entity
@Table(name = "diagnosis_reports")
public class DiagnosisReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patient_id")
    private Long patientId;

    @Column(name = "doctor_id")
    private Long doctorId;

    @Column(name = "medical_image_id")
    private Long medicalImageId;

    private String diagnosis;
    private String treatmentPlan;
    private String recommendations;

    @Column(columnDefinition = "TEXT")
    private String treatment;

    @ToString.Exclude
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "patient", "doctor"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", insertable = false, updatable = false)
    private Doctor doctor;

    @ToString.Exclude
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "medicalHistories", "healthRecords"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", insertable = false, updatable = false)
    private Patient patient;

    @ToString.Exclude
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "patient", "doctor"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_image_id", insertable = false, updatable = false)
    private MedicalImage medicalImage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "expire_date")
    private LocalDateTime expireDate;

    private boolean expired;

    @Column(name = "read_status")
    private Boolean readStatus = false;  // 默认未读

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 