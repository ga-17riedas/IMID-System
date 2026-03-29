package com.example.medicalsystem.dto;

import lombok.Data;

@Data
public class ScheduleDTO {
    private Long id;
    private String date;
    private String timeSlot;
    private Integer maxPatients;
    private Integer bookedPatients;
    private String notes;
    private String createdAt;
} 