
package com.bijulal.doctorappointmentbooking.repository;

import com.bijulal.doctorappointmentbooking.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    @Query("SELECT d FROM Doctor d WHERE d.user.id = :userId")
    Optional<Doctor> findByUserId(@Param("userId") Long userId);
}
