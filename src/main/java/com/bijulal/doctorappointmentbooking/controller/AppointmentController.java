package com.bijulal.doctorappointmentbooking.controller;

import com.bijulal.doctorappointmentbooking.model.Appointment;
import com.bijulal.doctorappointmentbooking.service.AppointmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final  AppointmentService appointmentService;
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
@PostMapping("/{doctorId}")
    public Appointment bookAppointment(@RequestBody Appointment appointment, @PathVariable("doctorId") Long doctorId) {
        return appointmentService.bookAppointment(appointment,doctorId);

}

@GetMapping
    public List<Appointment> getAppointments() {
       return appointmentService.getAllAppointments();
}

}
