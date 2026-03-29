package com.example.medicalsystem.repository;

import com.example.medicalsystem.model.DoctorPatient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DoctorPatientRepository extends JpaRepository<DoctorPatient, Long> {
    
    @Query("SELECT dp FROM DoctorPatient dp " +
           "JOIN FETCH dp.patient p " +
           "LEFT JOIN FETCH p.user u " +
           "WHERE dp.doctor.id = :doctorId AND dp.endDate IS NULL")
    List<DoctorPatient> findCurrentPatientsByDoctorId(@Param("doctorId") Long doctorId);
    
    @Query("SELECT dp FROM DoctorPatient dp WHERE dp.patient.id = :patientId AND dp.endDate IS NULL")
    List<DoctorPatient> findCurrentDoctorsByPatientId(@Param("patientId") Long patientId);
    
    boolean existsByDoctorIdAndPatientIdAndEndDateIsNull(Long doctorId, Long patientId);

    @Query("SELECT COUNT(DISTINCT dp.patient.id) FROM DoctorPatient dp " +
           "WHERE dp.doctor.id = :doctorId AND CAST(dp.createdAt AS date) = CURRENT_DATE")
    Integer countTodayPatients(@Param("doctorId") Long doctorId);
} 