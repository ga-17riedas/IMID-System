package com.example.medicalsystem.repository;

import com.example.medicalsystem.model.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    
    List<HealthRecord> findByPatientIdOrderByRecordTimeDesc(Long patientId);

    @Query("SELECT h FROM HealthRecord h WHERE h.patient.id = :patientId " +
           "AND h.recordTime BETWEEN :startTime AND :endTime " +
           "ORDER BY h.recordTime")
    List<HealthRecord> findByPatientIdAndTimeRange(
        @Param("patientId") Long patientId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );

    @Query("SELECT COUNT(h) FROM HealthRecord h WHERE h.patient.id = :patientId " +
           "AND h.recordTime >= :since")
    Long countRecordsSince(
        @Param("patientId") Long patientId,
        @Param("since") LocalDateTime since
    );

    @Query("SELECT AVG(h.systolicPressure) FROM HealthRecord h WHERE h.patient.id = :patientId " +
           "AND h.recordTime >= :since")
    Double getAverageSystolicPressureSince(
        @Param("patientId") Long patientId,
        @Param("since") LocalDateTime since
    );

    @Query("SELECT AVG(h.diastolicPressure) FROM HealthRecord h WHERE h.patient.id = :patientId " +
           "AND h.recordTime >= :since")
    Double getAverageDiastolicPressureSince(
        @Param("patientId") Long patientId,
        @Param("since") LocalDateTime since
    );

    @Query("SELECT AVG(h.heartRate) FROM HealthRecord h WHERE h.patient.id = :patientId " +
           "AND h.recordTime >= :since")
    Double getAverageHeartRateSince(
        @Param("patientId") Long patientId,
        @Param("since") LocalDateTime since
    );

    @Query("SELECT AVG(h.bloodSugar) FROM HealthRecord h WHERE h.patient.id = :patientId " +
           "AND h.recordTime >= :since")
    Double getAverageBloodSugarSince(
        @Param("patientId") Long patientId,
        @Param("since") LocalDateTime since
    );

    List<HealthRecord> findTop5ByPatientIdOrderByRecordTimeDesc(Long patientId);
} 