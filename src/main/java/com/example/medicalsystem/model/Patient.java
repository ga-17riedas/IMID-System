package com.example.medicalsystem.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;

@Data
@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    private String gender;

    @Column(name = "phone")
    private String phone;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "emergency_phone")
    private String emergencyPhone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "patient_id", unique = true)
    private String patientId;

    @Column(name = "address")
    private String address;

    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "patient")
    private List<MedicalImage> medicalImages;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "patient")
    private List<DiagnosisReport> diagnosisReports;

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getEmail() {
        return user != null ? user.getEmail() : null;
    }

    public void setEmail(String email) {
        if (user != null) {
            user.setEmail(email);
        }
    }
} 