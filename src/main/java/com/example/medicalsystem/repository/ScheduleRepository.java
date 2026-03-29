package com.example.medicalsystem.repository;

import com.example.medicalsystem.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("SELECT s FROM Schedule s WHERE s.doctor.id = :doctorId " +
           "AND YEAR(s.date) = :year AND MONTH(s.date) = :month")
    List<Schedule> findByDoctorAndYearMonth(
        @Param("doctorId") Long doctorId,
        @Param("year") int year,
        @Param("month") int month
    );

    @Query("SELECT s FROM Schedule s WHERE s.doctor.id = :doctorId " +
           "AND s.date = :date AND s.timeSlot = :timeSlot")
    List<Schedule> findConflictingSchedules(
        @Param("doctorId") Long doctorId,
        @Param("date") LocalDate date,
        @Param("timeSlot") String timeSlot
    );

    List<Schedule> findByDoctorIdAndDateBetween(Long doctorId, LocalDate startDate, LocalDate endDate);
} 