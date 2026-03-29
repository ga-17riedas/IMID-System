package com.example.medicalsystem.repository;

import com.example.medicalsystem.model.MedicalImage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface MedicalImageRepository extends JpaRepository<MedicalImage, Long> {
    List<MedicalImage> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    int countByPatientId(Long patientId);

    @Query("SELECT COUNT(m) FROM MedicalImage m " +
           "WHERE m.doctor.id = :doctorId")
    Integer countImagesByDoctor(@Param("doctorId") Long doctorId);

    @Query("SELECT COUNT(m) FROM MedicalImage m " +
           "WHERE m.doctor.id = :doctorId AND m.createdAt >= :startDate")
    Integer countImagesByDoctorSince(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDateTime startDate);

    @Query("SELECT AVG(CASE WHEN m.aiDiagnosis = m.doctorDiagnosis THEN 1.0 ELSE 0.0 END) " +
           "FROM MedicalImage m " +
           "WHERE m.doctor.id = :doctorId AND m.aiDiagnosis IS NOT NULL AND m.doctorDiagnosis IS NOT NULL")
    Double calculateAiAccuracy(@Param("doctorId") Long doctorId);

    @Query("SELECT COUNT(m) FROM MedicalImage m " +
           "WHERE m.doctor.id = :doctorId " +
           "AND m.createdAt BETWEEN :startDate AND :endDate")
    Integer countImagesByDoctorInRange(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT AVG(CASE WHEN m.aiDiagnosis = m.doctorDiagnosis THEN 1.0 ELSE 0.0 END) " +
           "FROM MedicalImage m " +
           "WHERE m.doctor.id = :doctorId " +
           "AND m.createdAt BETWEEN :startDate AND :endDate " +
           "AND m.aiDiagnosis IS NOT NULL AND m.doctorDiagnosis IS NOT NULL")
    Double calculateAiAccuracyInRange(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(m) FROM MedicalImage m " +
           "WHERE m.doctor.id = :doctorId " +
           "AND m.createdAt BETWEEN :startDate AND :endDate " +
           "AND m.aiDiagnosis IS NOT NULL AND m.doctorDiagnosis IS NOT NULL")
    Integer countAiDiagnosisInRange(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(m) FROM MedicalImage m WHERE m.doctor.id = :doctorId AND m.doctorDiagnosis IS NULL")
    Integer countByDoctorIdAndDoctorDiagnosisIsNull(@Param("doctorId") Long doctorId);

    @Query("SELECT AVG(CASE WHEN m.aiDiagnosis = m.doctorDiagnosis THEN 1.0 ELSE 0.0 END) " +
           "FROM MedicalImage m WHERE m.createdAt BETWEEN :startDate AND :endDate " +
           "AND m.aiDiagnosis IS NOT NULL AND m.doctorDiagnosis IS NOT NULL")
    Double calculateAiAccuracy(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT m FROM MedicalImage m WHERE m.patient.fullName LIKE %:search% AND m.imageType = :imageType")
    Page<MedicalImage> findByPatientNameContainingAndImageType(
        @Param("search") String search, 
        @Param("imageType") String imageType, 
        Pageable pageable
    );

    @Query("SELECT m FROM MedicalImage m WHERE m.patient.fullName LIKE %:search%")
    Page<MedicalImage> findByPatientNameContaining(
        @Param("search") String search, 
        Pageable pageable
    );

    Page<MedicalImage> findByImageType(String imageType, Pageable pageable);

    /**
     * 获取指定时间范围内的AI诊断准确率分布
     * 返回格式: [准确率, 图像数量]
     */
    @Query("SELECT " +
           "CASE " +
           "  WHEN m.aiDiagnosis = m.doctorDiagnosis THEN 1.0 " +
           "  ELSE 0.0 " +
           "END as accuracy, " +
           "COUNT(m) as count " +
           "FROM MedicalImage m " +
           "WHERE m.createdAt BETWEEN :startDate AND :endDate " +
           "AND m.aiDiagnosis IS NOT NULL AND m.doctorDiagnosis IS NOT NULL " +
           "GROUP BY accuracy")
    List<Object[]> getAiAccuracyDistribution(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    /**
     * 计算指定医生在指定时间范围内的AI诊断准确率
     */
    @Query("SELECT AVG(CASE WHEN m.aiDiagnosis = m.doctorDiagnosis THEN 1.0 ELSE 0.0 END) " +
           "FROM MedicalImage m " +
           "WHERE m.doctor.id = :doctorId " +
           "AND m.createdAt BETWEEN :startDate AND :endDate " +
           "AND m.aiDiagnosis IS NOT NULL AND m.doctorDiagnosis IS NOT NULL")
    Double calculateDoctorAiAccuracy(
        @Param("doctorId") Long doctorId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
} 