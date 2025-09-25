package com.bijulal.doctorappointmentbooking.repository;

import com.bijulal.doctorappointmentbooking.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
}
