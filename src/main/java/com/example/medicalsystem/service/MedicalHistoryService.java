package com.example.medicalsystem.service;

import com.example.medicalsystem.dto.MedicalHistoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface MedicalHistoryService {
    List<MedicalHistoryDTO> getPatientHistory(Long patientId);
    
    MedicalHistoryDTO createMedicalHistory(Long patientId, Long doctorId, MedicalHistoryDTO historyDTO);
    
    MedicalHistoryDTO updateMedicalHistory(Long id, MedicalHistoryDTO historyDTO);
    
    void deleteHistory(Long patientId, Long historyId);
    
    Page<MedicalHistoryDTO> getPatientHistoryPaged(Long patientId, Pageable pageable);
    
    List<MedicalHistoryDTO> getRecentHistory(Long patientId, int limit);
} 