package com.example.medicalsystem.repository;

import com.example.medicalsystem.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    @Query("SELECT p FROM Patient p JOIN p.user u WHERE u.username = :username")
    Optional<Patient> findByUsername(@Param("username") String username);

    @Query("SELECT DISTINCT p FROM Patient p " +
           "LEFT JOIN DoctorPatient dp ON p.id = dp.patient.id " +
           "WHERE p.fullName LIKE %:query% OR p.emergencyContact LIKE %:query%")
    List<Patient> searchPatients(@Param("query") String query);

    List<Patient> findAll();
    Optional<Patient> findById(Long id);

    @Query("SELECT p FROM Patient p WHERE p.id NOT IN " +
           "(SELECT dp.patient.id FROM DoctorPatient dp WHERE dp.doctor.id = :doctorId AND dp.endDate IS NULL)")
    List<Patient> findPatientsNotAssociatedWithDoctor(@Param("doctorId") Long doctorId);

    Optional<Patient> findByUser_Username(String username);

    @Query("SELECT p FROM Patient p WHERE p.fullName LIKE %:fullName% AND UPPER(p.gender) = UPPER(:gender)")
    Page<Patient> findByFullNameContainingAndGender(@Param("fullName") String fullName, @Param("gender") String gender, Pageable pageable);
    
    Page<Patient> findByFullNameContaining(String fullName, Pageable pageable);
    
    @Query("SELECT p FROM Patient p WHERE UPPER(p.gender) = UPPER(:gender)")
    Page<Patient> findByGender(@Param("gender") String gender, Pageable pageable);
} 