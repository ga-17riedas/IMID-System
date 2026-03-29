package com.example.medicalsystem.controller;

import com.example.medicalsystem.dto.DoctorHomeStatsDTO;
import com.example.medicalsystem.dto.DoctorProfileDTO;
import com.example.medicalsystem.service.DoctorHomeService;
import com.example.medicalsystem.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctor/home")
public class DoctorHomeController {

    @Autowired
    private DoctorHomeService doctorHomeService;

    @Autowired
    private ProfileService profileService;

    @GetMapping("/stats")
    public ResponseEntity<DoctorHomeStatsDTO> getHomeStats(Authentication authentication) {
        String username = authentication.getName();
        DoctorHomeStatsDTO stats = doctorHomeService.getHomeStats(username);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/profile")
    public ResponseEntity<DoctorProfileDTO> getDoctorProfile(Authentication authentication) {
        DoctorProfileDTO profile = profileService.getDoctorProfile(authentication.getName());
        return ResponseEntity.ok(profile);
    }
} 