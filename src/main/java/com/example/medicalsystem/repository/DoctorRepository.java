package com.example.medicalsystem.repository;

import com.example.medicalsystem.model.Doctor;
import com.example.medicalsystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUserId(Long userId);
    
    Optional<Doctor> findByUser(User user);
    
    boolean existsByLicenseNumber(String licenseNumber);

    Optional<Doctor> findByLicenseNumber(String licenseNumber);

    Optional<Doctor> findByUser_Username(String username);

    Page<Doctor> findByFullNameContainingAndDepartment(String fullName, String department, Pageable pageable);
    Page<Doctor> findByFullNameContaining(String fullName, Pageable pageable);
    Page<Doctor> findByDepartment(String department, Pageable pageable);
} 