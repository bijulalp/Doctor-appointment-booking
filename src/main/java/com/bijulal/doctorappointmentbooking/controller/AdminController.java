package com.bijulal.doctorappointmentbooking.controller;

import com.bijulal.doctorappointmentbooking.dto.DoctorDto;
import com.bijulal.doctorappointmentbooking.dto.RegistrationDTO;
import com.bijulal.doctorappointmentbooking.model.Doctor;
import com.bijulal.doctorappointmentbooking.model.User;
import com.bijulal.doctorappointmentbooking.service.DoctorService;
import com.bijulal.doctorappointmentbooking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    DoctorService doctorService;

    @Autowired
    UserService userService;

    @PostMapping("/doctors")
    public ResponseEntity<?> createDoctor(@RequestBody RegistrationDTO doctorCreateDto) {
       Doctor doc = doctorService.createDoctor(doctorCreateDto);
        return new ResponseEntity<>(doc, HttpStatus.CREATED);
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return userService.getAllUsers();
    }


}
