package com.example.medicalsystem.repository;

import com.example.medicalsystem.model.MedicalRecord;
import com.example.medicalsystem.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientOrderByVisitDateDesc(Patient patient);
} 