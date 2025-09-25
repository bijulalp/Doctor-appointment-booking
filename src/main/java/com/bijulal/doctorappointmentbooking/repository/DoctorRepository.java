package com.bijulal.doctorappointmentbooking.repository;

import com.bijulal.doctorappointmentbooking.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
