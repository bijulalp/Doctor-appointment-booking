package com.bijulal.doctorappointmentbooking.repository;

import com.bijulal.doctorappointmentbooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
