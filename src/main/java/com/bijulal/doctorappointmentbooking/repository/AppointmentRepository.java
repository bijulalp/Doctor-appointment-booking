package com.bijulal.doctorappointmentbooking.repository;

import com.bijulal.doctorappointmentbooking.model.Appointment;
import com.bijulal.doctorappointmentbooking.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {


    // Find appointments by doctor
    Page<Appointment> findByDoctorId(Long doctorId, Pageable pageable);

    // Find appointments by user
    Page<Appointment> findByUserId(Long userId, Pageable pageable);

    // Find appointments by status
    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);

    // Find appointments by doctor and date range
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
            "AND a.appointmentTime BETWEEN :startDate AND :endDate")
    List<Appointment> findByDoctorAndDateRange(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // Check for appointment conflicts
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctor.id = :doctorId " +
            "AND a.appointmentTime = :appointmentTime " +
            "AND a.status NOT IN ('CANCELLED', 'COMPLETED') " +
            "AND (:excludeId IS NULL OR a.id != :excludeId)")
    boolean existsConflictingAppointment(
            @Param("doctorId") Long doctorId,
            @Param("appointmentTime") LocalDateTime appointmentTime,
            @Param("excludeId") Long excludeId
    );

    // Find upcoming appointments for a user
    @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId " +
            "AND a.appointmentTime > :now " +
            "AND a.status IN ('PENDING', 'CONFIRMED') " +
            "ORDER BY a.appointmentTime ASC")
    List<Appointment> findUpcomingAppointmentsByUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    // Find upcoming appointments for a doctor
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
            "AND a.appointmentTime > :now " +
            "AND a.status IN ('PENDING', 'CONFIRMED') " +
            "ORDER BY a.appointmentTime ASC")
    List<Appointment> findUpcomingAppointmentsByDoctor(@Param("doctorId") Long doctorId, @Param("now") LocalDateTime now);

}
