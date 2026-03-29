package com.example.medicalsystem.dto;

import lombok.Data;

@Data
public class DoctorHomeStatsDTO {
    private Integer pendingImages;  // 待分析影像数量
    private Integer todayPatients;  // 今日患者数量
    private Integer totalReports;   // 总诊断报告数
} 