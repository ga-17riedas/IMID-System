package com.example.medicalsystem.service.impl;

import com.example.medicalsystem.dto.MedicalHistoryDTO;
import com.example.medicalsystem.model.MedicalHistory;
import com.example.medicalsystem.model.Doctor;
import com.example.medicalsystem.model.Patient;
import com.example.medicalsystem.repository.MedicalHistoryRepository;
import com.example.medicalsystem.repository.DoctorRepository;
import com.example.medicalsystem.repository.PatientRepository;
import com.example.medicalsystem.service.MedicalHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.medicalsystem.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public List<MedicalHistoryDTO> getPatientHistory(Long patientId) {
        List<MedicalHistory> histories = medicalHistoryRepository.findByPatient_IdOrderByVisitDateDesc(patientId);
        return histories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<MedicalHistoryDTO> getPatientHistoryPaged(Long patientId, Pageable pageable) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Page<MedicalHistory> histories = medicalHistoryRepository.findByPatient(patient, pageable);
        return histories.map(this::convertToDTO);
    }

    @Override
    public List<MedicalHistoryDTO> getRecentHistory(Long patientId, int limit) {
        List<MedicalHistory> histories = medicalHistoryRepository.findByPatient_IdOrderByVisitDateDesc(patientId);
        return histories.stream()
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MedicalHistoryDTO createMedicalHistory(Long patientId, Long doctorId, MedicalHistoryDTO historyDTO) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", doctorId));
        
        MedicalHistory history = new MedicalHistory();
        history.setPatient(patient);
        history.setDoctor(doctor);
        history.setDate(historyDTO.getDate());
        history.setDescription(historyDTO.getDescription());
        history.setDiagnosis(historyDTO.getDiagnosis());
        history.setTreatment(historyDTO.getTreatment());
        history.setVisitDate(historyDTO.getVisitDate());
        
        return convertToDTO(medicalHistoryRepository.save(history));
    }

    @Override
    @Transactional
    public MedicalHistoryDTO updateMedicalHistory(Long id, MedicalHistoryDTO historyDTO) {
        MedicalHistory history = medicalHistoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalHistory", "id", id));
        
        history.setDate(historyDTO.getDate());
        history.setDescription(historyDTO.getDescription());
        history.setDiagnosis(historyDTO.getDiagnosis());
        history.setTreatment(historyDTO.getTreatment());
        history.setVisitDate(historyDTO.getVisitDate());
        
        return convertToDTO(medicalHistoryRepository.save(history));
    }

    @Override
    @Transactional
    public void deleteHistory(Long patientId, Long historyId) {
        MedicalHistory history = medicalHistoryRepository.findById(historyId)
            .orElseThrow(() -> new RuntimeException("History not found"));
            
        if (!history.getPatient().getId().equals(patientId)) {
            throw new RuntimeException("History record does not belong to this patient");
        }
            
        medicalHistoryRepository.delete(history);
    }

    private MedicalHistoryDTO convertToDTO(MedicalHistory history) {
        MedicalHistoryDTO dto = new MedicalHistoryDTO();
        dto.setId(history.getId());
        dto.setVisitDate(history.getVisitDate());
        dto.setDescription(history.getDescription());
        dto.setDiagnosis(history.getDiagnosis());
        dto.setTreatment(history.getTreatment());
        if (history.getDoctor() != null) {
            dto.setDoctorName(history.getDoctor().getFullName());
            dto.setDepartment(history.getDoctor().getDepartment());
        }
        return dto;
    }
} 