package com.bijulal.doctorappointmentbooking.service;

import com.bijulal.doctorappointmentbooking.model.Appointment;
import com.bijulal.doctorappointmentbooking.model.Doctor;
import com.bijulal.doctorappointmentbooking.repository.AppointmentRepository;
import com.bijulal.doctorappointmentbooking.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
    }

    public Appointment bookAppointment(Appointment appointment,Long doctorId) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        appointment.setDoctor(doctor);
        return appointmentRepository.save(appointment);

    }

    public List<Appointment>getAllAppointments(){
        return appointmentRepository.findAll();
    }


}
