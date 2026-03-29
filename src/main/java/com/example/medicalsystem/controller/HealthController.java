package com.example.medicalsystem.controller;

import com.example.medicalsystem.dto.HealthRecordDTO;
import com.example.medicalsystem.dto.HealthStatsDTO;
import com.example.medicalsystem.service.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient/health")
public class HealthController {

    @Autowired
    private HealthService healthService;

    @PostMapping("/records")
    public ResponseEntity<HealthRecordDTO> createHealthRecord(
            Authentication authentication,
            @RequestBody HealthRecordDTO recordDTO) {
        return ResponseEntity.ok(healthService.createHealthRecord(authentication.getName(), recordDTO));
    }

    @GetMapping("/records")
    public ResponseEntity<List<HealthRecordDTO>> getHealthRecords(Authentication authentication) {
        return ResponseEntity.ok(healthService.getHealthRecords(authentication.getName()));
    }

    @GetMapping("/stats")
    public ResponseEntity<HealthStatsDTO> getHealthStats(Authentication authentication) {
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(healthService.getHealthStats(authentication.getName()));
    }

    @PutMapping("/records/{recordId}")
    public ResponseEntity<HealthRecordDTO> updateHealthRecord(
            Authentication authentication,
            @PathVariable Long recordId,
            @RequestBody HealthRecordDTO recordDTO) {
        return ResponseEntity.ok(healthService.updateHealthRecord(authentication.getName(), recordId, recordDTO));
    }

    @DeleteMapping("/records/{recordId}")
    public ResponseEntity<Void> deleteHealthRecord(
            Authentication authentication,
            @PathVariable Long recordId) {
        healthService.deleteHealthRecord(authentication.getName(), recordId);
        return ResponseEntity.ok().build();
    }
} 