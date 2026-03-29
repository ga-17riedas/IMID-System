package com.example.medicalsystem.controller;

import com.example.medicalsystem.dto.HealthOverviewDTO;
import com.example.medicalsystem.dto.HealthRecordDTO;
import com.example.medicalsystem.service.PatientHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/patient/home")
public class PatientHomeController {

    @Autowired
    private PatientHomeService patientHomeService;

    @GetMapping("/overview")
    public HealthOverviewDTO getHealthOverview(Authentication authentication) {
        return patientHomeService.getHealthOverview(authentication.getName());
    }

    @GetMapping("/recent-records")
    public List<HealthRecordDTO> getRecentRecords(Authentication authentication) {
        return patientHomeService.getRecentRecords(authentication.getName());
    }
} 