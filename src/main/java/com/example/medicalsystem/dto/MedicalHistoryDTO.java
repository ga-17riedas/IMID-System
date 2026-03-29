package com.example.medicalsystem.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MedicalHistoryDTO {
    private Long id;
    private LocalDate date;
    private String description;
    private String diagnosis;
    private String treatment;
    private LocalDateTime visitDate;
    private String doctorName;
    private String department;
} 