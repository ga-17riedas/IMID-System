package com.example.medicalsystem.repository;

import com.example.medicalsystem.model.MedicalHistory;
import com.example.medicalsystem.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    List<MedicalHistory> findByPatient_IdOrderByVisitDateDesc(Long patientId);
    List<MedicalHistory> findByDoctor_Id(Long doctorId);
    List<MedicalHistory> findByPatient_IdAndDoctor_Id(Long patientId, Long doctorId);
    void deleteByPatientIdAndId(Long patientId, Long id);
    Page<MedicalHistory> findByPatient(Patient patient, Pageable pageable);
} 