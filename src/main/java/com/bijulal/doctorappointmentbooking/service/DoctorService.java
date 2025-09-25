package com.bijulal.doctorappointmentbooking.service;

import com.bijulal.doctorappointmentbooking.model.Doctor;
import com.bijulal.doctorappointmentbooking.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public List<Doctor>getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor addDoctor(@RequestBody Doctor doctor) {
        return doctorRepository.save(doctor);
    }
}
