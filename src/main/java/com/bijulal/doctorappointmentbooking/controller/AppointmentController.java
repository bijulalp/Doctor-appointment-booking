package com.bijulal.doctorappointmentbooking.controller;

import com.bijulal.doctorappointmentbooking.dto.*;
import com.bijulal.doctorappointmentbooking.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointments", description = "Appointment management endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @Operation(summary = "Book a new appointment")
    public ResponseEntity<AppointmentDto> bookAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        AppointmentDto appointment = appointmentService.bookAppointment(request);
        return new ResponseEntity<>(appointment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID")
    public ResponseEntity<AppointmentDto> getAppointment(@PathVariable Long id) {
        AppointmentDto appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping
    @Operation(summary = "Get all appointments (paginated, filtered by user role)")
    public ResponseEntity<Page<AppointmentDto>> getAllAppointments(
            @PageableDefault(size = 10, sort = "appointmentTime", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<AppointmentDto> appointments = appointmentService.getAllAppointments(pageable);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/my-upcoming")
    @Operation(summary = "Get current user's upcoming appointments")
    public ResponseEntity<List<AppointmentDto>> getMyUpcomingAppointments() {
        List<AppointmentDto> appointments = appointmentService.getMyUpcomingAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/doctor/{doctorId}/upcoming")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "Get upcoming appointments for a specific doctor")
    public ResponseEntity<List<AppointmentDto>> getDoctorUpcomingAppointments(@PathVariable Long doctorId) {
        List<AppointmentDto> appointments = appointmentService.getDoctorUpcomingAppointments(doctorId);
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update appointment (reschedule or update notes)")
    public ResponseEntity<AppointmentDto> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAppointmentRequest request) {
        AppointmentDto appointment = appointmentService.updateAppointment(id, request);
        return ResponseEntity.ok(appointment);
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Confirm appointment (Doctor only)")
    public ResponseEntity<AppointmentDto> confirmAppointment(@PathVariable Long id) {
        AppointmentDto appointment = appointmentService.confirmAppointment(id);
        return ResponseEntity.ok(appointment);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel appointment")
    public ResponseEntity<AppointmentDto> cancelAppointment(
            @PathVariable Long id,
            @Valid @RequestBody CancelAppointmentRequest request) {
        AppointmentDto appointment = appointmentService.cancelAppointment(id, request);
        return ResponseEntity.ok(appointment);
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Mark appointment as completed (Doctor only)")
    public ResponseEntity<AppointmentDto> completeAppointment(@PathVariable Long id) {
        AppointmentDto appointment = appointmentService.completeAppointment(id);
        return ResponseEntity.ok(appointment);
    }
}
