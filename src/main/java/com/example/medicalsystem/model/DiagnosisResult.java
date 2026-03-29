package com.example.medicalsystem.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "diagnosis_results")
public class DiagnosisResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_id", length = 255, nullable = false)
    private String imageId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "analysis_type")
    private String analysisType;

    @Column(name = "confidence")
    private Double confidence;

    @Column(name = "diagnosis")
    private String diagnosis;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
} 