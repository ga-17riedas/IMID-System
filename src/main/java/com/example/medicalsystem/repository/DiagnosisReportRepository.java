package com.example.medicalsystem.repository;

import com.example.medicalsystem.model.DiagnosisReport;
import com.example.medicalsystem.model.Patient;
import com.example.medicalsystem.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface DiagnosisReportRepository extends JpaRepository<DiagnosisReport, Long> {
    List<DiagnosisReport> findByPatientIdOrderByCreatedAtDesc(Long patientId);

    @Query("SELECT DISTINCT d.patient FROM DiagnosisReport d " +
           "WHERE d.doctor.id = :doctorId " +
           "ORDER BY d.createdAt DESC")
    List<Patient> findRecentPatientsByDoctorId(@Param("doctorId") Long doctorId);

    Optional<DiagnosisReport> findFirstByPatientIdOrderByCreatedAtDesc(Long patientId);

    int countByPatientId(Long patientId);

    @Query("SELECT COUNT(DISTINCT d.patient.id) FROM DiagnosisReport d " +
           "WHERE d.doctor.id = :doctorId")
    Integer countTotalPatientsByDoctor(@Param("doctorId") Long doctorId);

    @Query("SELECT COUNT(d) FROM DiagnosisReport d " +
           "WHERE d.doctor.id = :doctorId AND d.createdAt >= :startDate")
    Integer countReportsByDoctorSince(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(DISTINCT d.patient.id) FROM DiagnosisReport d " +
           "WHERE d.doctor.id = :doctorId AND d.createdAt >= :startDate")
    Integer countNewPatientsByDoctorSince(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDateTime startDate);

    @Query("SELECT DATE(d.createdAt) as date, COUNT(d) as count " +
           "FROM DiagnosisReport d " +
           "WHERE d.doctor.id = :doctorId AND d.createdAt >= :startDate " +
           "GROUP BY DATE(d.createdAt) " +
           "ORDER BY date")
    List<Object[]> getDailyReportCounts(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(DISTINCT d.patient.id) FROM DiagnosisReport d " +
           "WHERE d.doctor.id = :doctorId " +
           "AND d.createdAt BETWEEN :startDate AND :endDate")
    Integer countPatientsByDoctorInRange(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(d) FROM DiagnosisReport d " +
           "WHERE d.doctor.id = :doctorId " +
           "AND d.createdAt BETWEEN :startDate AND :endDate")
    Integer countReportsByDoctorInRange(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT d.diagnosis as type, COUNT(d) as count " +
           "FROM DiagnosisReport d " +
           "WHERE d.doctor.id = :doctorId " +
           "AND d.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY d.diagnosis")
    List<Object[]> getDiagnosisTypeDistribution(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT DATE(d.createdAt) as date, COUNT(d) as count " +
           "FROM DiagnosisReport d " +
           "WHERE d.doctor.id = :doctorId " +
           "AND d.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(d.createdAt) " +
           "ORDER BY date")
    List<Object[]> getDailyReportCountsInRange(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(d) FROM DiagnosisReport d " +
           "WHERE d.patient.id = :patientId AND d.readStatus = false")
    Integer countUnreadReports(@Param("patientId") Long patientId);

    Page<DiagnosisReport> findByDoctor(Doctor doctor, Pageable pageable);

    Page<DiagnosisReport> findByPatient(Patient patient, Pageable pageable);

    Page<DiagnosisReport> findByPatientAndDiagnosisContaining(Patient patient, String diagnosis, Pageable pageable);

    Optional<DiagnosisReport> findByIdAndPatient(Long id, Patient patient);

    List<DiagnosisReport> findByExpireDateBeforeAndExpiredFalse(LocalDateTime dateTime);

    Page<DiagnosisReport> findByPatientOrderByCreatedAtDesc(Patient patient, Pageable pageable);

    Page<DiagnosisReport> findByPatientAndDiagnosisContainingOrderByCreatedAtDesc(
        Patient patient, 
        String diagnosis, 
        Pageable pageable
    );

    Page<DiagnosisReport> findByDoctorId(Long doctorId, Pageable pageable);

    @Query("SELECT COUNT(d) FROM DiagnosisReport d WHERE d.doctor.id = :doctorId")
    Integer countByDoctorId(@Param("doctorId") Long doctorId);

    @Query("SELECT COUNT(d) FROM DiagnosisReport d WHERE d.createdAt BETWEEN :startDate AND :endDate")
    Integer countByCreatedAtBetween(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(d) FROM DiagnosisReport d WHERE d.doctor.id = :doctorId " +
           "AND d.createdAt BETWEEN :startDate AND :endDate")
    Integer countByDoctorAndDateRange(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    Optional<DiagnosisReport> findFirstByMedicalImageIdOrderByCreatedAtDesc(Long medicalImageId);

    @Query("SELECT COUNT(d) FROM DiagnosisReport d WHERE d.doctor.id = :doctorId AND d.createdAt BETWEEN :startDate AND :endDate")
    Integer countByDoctorIdAndCreatedAtBetween(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * 查找指定时间范围内的所有诊断报告
     */
    List<DiagnosisReport> findByCreatedAtBetween(
        LocalDateTime startDate, 
        LocalDateTime endDate
    );

    /**
     * 获取指定时间范围内的疾病类型分布
     */
    @Query("SELECT d.diagnosis as disease, COUNT(d) as count " +
           "FROM DiagnosisReport d " +
           "WHERE d.createdAt BETWEEN :startDate AND :endDate " +
           "AND d.diagnosis IS NOT NULL " +
           "GROUP BY d.diagnosis " +
           "ORDER BY COUNT(d) DESC")
    List<Object[]> getDiseaseDistribution(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
} 