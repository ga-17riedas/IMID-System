package com.example.medicalsystem.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ai_models")
public class AIModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String version;

    private String description;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "model_size")
    private Long modelSize;

    @Column(name = "model_size_formatted")
    private String modelSizeFormatted;

    @Column(name = "parameter_count")
    private Long parameterCount;

    @Column(name = "accuracy")
    private Double accuracyValue;

    @Column(name = "precision_val")
    private Double precisionValue;

    @Column(name = "recall")
    private Double recallValue;

    @Column(name = "f1_score")
    private Double f1ScoreValue;

    @Column(name = "accuracy_formatted")
    private String accuracy;

    @Column(name = "precision_formatted")
    private String precision;

    @Column(name = "recall_formatted")
    private String recall;

    @Column(name = "f1_score_formatted")
    private String f1Score;

    @Column(name = "active")
    private boolean active = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        
        if (accuracyValue != null && (accuracy == null || accuracy.isEmpty())) {
            setAccuracy(accuracyValue);
        }
        
        if (precisionValue != null && (precision == null || precision.isEmpty())) {
            setPrecision(precisionValue);
        }
        
        if (recallValue != null && (recall == null || recall.isEmpty())) {
            setRecall(recallValue);
        }
        
        if (f1ScoreValue != null && (f1Score == null || f1Score.isEmpty())) {
            setF1Score(f1ScoreValue);
        }
    }

    public Double getAccuracyValue() {
        return accuracyValue;
    }

    public void setAccuracyValue(Double accuracyValue) {
        this.accuracyValue = accuracyValue;
        if (accuracyValue != null) {
            this.accuracy = String.format("%.2f%%", accuracyValue * 100);
        } else {
            this.accuracy = "-";
        }
    }

    public Double getPrecisionValue() {
        return precisionValue;
    }

    public void setPrecisionValue(Double precisionValue) {
        this.precisionValue = precisionValue;
        if (precisionValue != null) {
            this.precision = String.format("%.2f%%", precisionValue * 100);
        } else {
            this.precision = "-";
        }
    }

    public Double getRecallValue() {
        return recallValue;
    }

    public void setRecallValue(Double recallValue) {
        this.recallValue = recallValue;
        if (recallValue != null) {
            this.recall = String.format("%.2f%%", recallValue * 100);
        } else {
            this.recall = "-";
        }
    }

    public Double getF1ScoreValue() {
        return f1ScoreValue;
    }

    public void setF1ScoreValue(Double f1ScoreValue) {
        this.f1ScoreValue = f1ScoreValue;
        if (f1ScoreValue != null) {
            this.f1Score = String.format("%.2f%%", f1ScoreValue * 100);
        } else {
            this.f1Score = "-";
        }
    }

    public void setAccuracy(Double accuracy) {
        this.accuracyValue = accuracy;
        if (accuracy != null) {
            this.accuracy = String.format("%.2f%%", accuracy * 100);
        } else {
            this.accuracy = "-";
        }
    }

    public void setPrecision(Double precision) {
        this.precisionValue = precision;
        if (precision != null) {
            this.precision = String.format("%.2f%%", precision * 100);
        } else {
            this.precision = "-";
        }
    }

    public void setRecall(Double recall) {
        this.recallValue = recall;
        if (recall != null) {
            this.recall = String.format("%.2f%%", recall * 100);
        } else {
            this.recall = "-";
        }
    }

    public void setF1Score(Double f1Score) {
        this.f1ScoreValue = f1Score;
        if (f1Score != null) {
            this.f1Score = String.format("%.2f%%", f1Score * 100);
        } else {
            this.f1Score = "-";
        }
    }

    public String getAccuracy() {
        return accuracy;
    }

    public String getPrecision() {
        return precision;
    }

    public String getRecall() {
        return recall;
    }

    public String getF1Score() {
        return f1Score;
    }
} 