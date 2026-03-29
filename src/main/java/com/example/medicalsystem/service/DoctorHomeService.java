package com.example.medicalsystem.service;

import com.example.medicalsystem.dto.DoctorHomeStatsDTO;

public interface DoctorHomeService {
    DoctorHomeStatsDTO getHomeStats(String username);
} 