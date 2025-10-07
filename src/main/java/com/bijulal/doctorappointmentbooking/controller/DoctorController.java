package com.bijulal.doctorappointmentbooking.controller;

import com.bijulal.doctorappointmentbooking.dto.DoctorDto;
import com.bijulal.doctorappointmentbooking.dto.DoctorRegister;
import com.bijulal.doctorappointmentbooking.model.Doctor;
import com.bijulal.doctorappointmentbooking.service.DoctorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping
    public List<DoctorDto> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    @GetMapping("/{id}")
    public Doctor getDoctorById(@PathVariable Long id) {
        return doctorService.getDoctorById(id);

    }

    @PostMapping
    public Doctor addDoctor(@RequestBody DoctorRegister doctor) {
        return doctorService.addDoctor(doctor);
    }

    @PutMapping("/{id}")
    public void updateDoctor(@PathVariable Long id, @RequestBody DoctorDto doctor) {
        doctorService.updateDoctor(id, doctor);
    }

    @DeleteMapping("/{id}")
    public void deleteDoctor(@PathVariable Long id) {
//        doctorService.deleteDoctor(id);
    }
}
