package com.example.medicalsystem.model;

import lombok.Data;
import javax.persistence.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;

@Data
@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    private String gender;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "phone")
    private String phone;

    @Column(name = "professional_title")
    private String professionalTitle;

    @Column(name = "hospital")
    private String hospital;

    @Column(name = "department")
    private String department;

    @Column(name = "practice_years")
    private Integer practiceYears;

    @Column(name = "specialty")
    private String specialty;

    @Column(name = "license_number", unique = true)
    private String licenseNumber;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "doctor")
    private List<MedicalImage> medicalImages;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "doctor")
    private List<DiagnosisReport> diagnosisReports;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
} 