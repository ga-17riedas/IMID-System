package com.example.medicalsystem.repository;

import com.example.medicalsystem.model.DiagnosisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagnosisResultRepository extends JpaRepository<DiagnosisResult, Long> {
} 