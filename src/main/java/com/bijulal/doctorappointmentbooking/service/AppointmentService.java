
package com.bijulal.doctorappointmentbooking.service;

import com.bijulal.doctorappointmentbooking.dto.*;
import com.bijulal.doctorappointmentbooking.exception.AppointmentConflictException;
import com.bijulal.doctorappointmentbooking.exception.InvalidAppointmentException;
import com.bijulal.doctorappointmentbooking.exception.ResourceNotFoundException;
import com.bijulal.doctorappointmentbooking.model.*;
import com.bijulal.doctorappointmentbooking.repository.AppointmentRepository;
import com.bijulal.doctorappointmentbooking.repository.DoctorRepository;
import com.bijulal.doctorappointmentbooking.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    private static final LocalTime CLINIC_OPEN_TIME = LocalTime.of(9, 0);
    private static final LocalTime CLINIC_CLOSE_TIME = LocalTime.of(17, 0);
    private static final int MIN_ADVANCE_BOOKING_HOURS = 2;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AppointmentDto bookAppointment(CreateAppointmentRequest request) {
        // Get the currently authenticated user
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            throw new InvalidAppointmentException("User must be logged in to book an appointment");
        }

        // Verify doctor exists
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + request.getDoctorId()));

        // Validate appointment time
        validateAppointmentTime(request.getAppointmentTime());

        // Check doctor availability
        checkDoctorAvailability(doctor.getId(), request.getAppointmentTime(), null);

        // Create appointment with the authenticated user
        Appointment appointment = Appointment.builder()
                .user(currentUser)  // Set the authenticated user
                .doctor(doctor)
                .patientName(request.getPatientName())
                .patientEmail(request.getPatientEmail())
                .patientPhone(request.getPatientPhone())
                .age(request.getAge())
                .appointmentTime(request.getAppointmentTime())
                .status(AppointmentStatus.PENDING)
                .notes(request.getNotes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return convertToDto(savedAppointment);
    }

    @Transactional(readOnly = true)
    public AppointmentDto getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        validateAppointmentAccess(appointment);
        return convertToDto(appointment);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDto> getAllAppointments(Pageable pageable) {
        User currentUser = getCurrentUser();

        if (isAdmin(currentUser)) {
            return appointmentRepository.findAll(pageable).map(this::convertToDto);
        } else if (isDoctor(currentUser)) {
            Doctor doctor = doctorRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor profile not found"));
            return appointmentRepository.findByDoctorId(doctor.getId(), pageable)
                    .map(this::convertToDto);
        } else {
            return appointmentRepository.findByUserId(currentUser.getId(), pageable)
                    .map(this::convertToDto);
        }
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> getMyUpcomingAppointments() {
        User currentUser = getCurrentUser();
        List<Appointment> appointments = appointmentRepository
                .findUpcomingAppointmentsByUser(currentUser.getId(), LocalDateTime.now());
        return appointments.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentDto> getDoctorUpcomingAppointments(Long doctorId) {
        User currentUser = getCurrentUser();

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        if (!isAdmin(currentUser) && !currentUser.getId().equals(doctor.getUser().getId())) {
            throw new InvalidAppointmentException("Access denied");
        }

        List<Appointment> appointments = appointmentRepository
                .findUpcomingAppointmentsByDoctor(doctorId, LocalDateTime.now());
        return appointments.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public AppointmentDto updateAppointment(Long id, UpdateAppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        User currentUser = getCurrentUser();

        // Only the user who booked the appointment can update it
        if (!appointment.getUser().getId().equals(currentUser.getId())) {
            throw new InvalidAppointmentException("You can only update your own appointments");
        }

        // Cannot update cancelled or completed appointments
        if (appointment.getStatus() == AppointmentStatus.CANCELLED ||
                appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidAppointmentException("Cannot update a " + appointment.getStatus() + " appointment");
        }

        // Update appointment time if provided
        if (request.getAppointmentTime() != null) {
            validateAppointmentTime(request.getAppointmentTime());
            checkDoctorAvailability(appointment.getDoctor().getId(), request.getAppointmentTime(), id);
            appointment.setAppointmentTime(request.getAppointmentTime());
            appointment.setStatus(AppointmentStatus.RESCHEDULED);
        }

        // Update notes if provided
        if (request.getNotes() != null) {
            appointment.setNotes(request.getNotes());
        }

        appointment.setUpdatedAt(LocalDateTime.now());
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return convertToDto(updatedAppointment);
    }

    @Transactional
    public AppointmentDto confirmAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        User currentUser = getCurrentUser();

        // Only the assigned doctor can confirm appointments
        if (!isDoctor(currentUser) || !appointment.getDoctor().getUser().getId().equals(currentUser.getId())) {
            throw new InvalidAppointmentException("Only the assigned doctor can confirm appointments");
        }

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new InvalidAppointmentException("Only pending appointments can be confirmed");
        }

        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setUpdatedAt(LocalDateTime.now());
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return convertToDto(updatedAppointment);
    }

    @Transactional
    public AppointmentDto cancelAppointment(Long id, CancelAppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        User currentUser = getCurrentUser();

        // User who booked, assigned doctor, or admin can cancel
        boolean canCancel = appointment.getUser().getId().equals(currentUser.getId()) ||
                (isDoctor(currentUser) && appointment.getDoctor().getUser().getId().equals(currentUser.getId())) ||
                isAdmin(currentUser);

        if (!canCancel) {
            throw new InvalidAppointmentException("You don't have permission to cancel this appointment");
        }

        // Cannot cancel already cancelled or completed appointments
        if (appointment.getStatus() == AppointmentStatus.CANCELLED ||
                appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidAppointmentException("Appointment is already " + appointment.getStatus());
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancellationReason(request.getCancellationReason());
        appointment.setUpdatedAt(LocalDateTime.now());
        Appointment cancelledAppointment = appointmentRepository.save(appointment);
        return convertToDto(cancelledAppointment);
    }

    @Transactional
    public AppointmentDto completeAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        User currentUser = getCurrentUser();

        // Only the assigned doctor can complete appointments
        if (!isDoctor(currentUser) || !appointment.getDoctor().getUser().getId().equals(currentUser.getId())) {
            throw new InvalidAppointmentException("Only the assigned doctor can complete appointments");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new InvalidAppointmentException("Cannot complete a cancelled appointment");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setUpdatedAt(LocalDateTime.now());
        Appointment completedAppointment = appointmentRepository.save(appointment);
        return convertToDto(completedAppointment);
    }

    // ==================== PRIVATE HELPER METHODS ====================

    private void validateAppointmentTime(LocalDateTime appointmentTime) {
        // Must be at least MIN_ADVANCE_BOOKING_HOURS in the future
        if (appointmentTime.isBefore(LocalDateTime.now().plusHours(MIN_ADVANCE_BOOKING_HOURS))) {
            throw new InvalidAppointmentException(
                    "Appointments must be booked at least " + MIN_ADVANCE_BOOKING_HOURS + " hours in advance");
        }

        // No weekend appointments
        DayOfWeek dayOfWeek = appointmentTime.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            throw new InvalidAppointmentException("Appointments are not available on weekends");
        }

        // Must be within clinic hours
        LocalTime time = appointmentTime.toLocalTime();
        if (time.isBefore(CLINIC_OPEN_TIME) || time.isAfter(CLINIC_CLOSE_TIME)) {
            throw new InvalidAppointmentException(
                    "Appointments are only available between " + CLINIC_OPEN_TIME + " and " + CLINIC_CLOSE_TIME);
        }

        // Must be on :00 or :30 minute marks
        if (appointmentTime.getMinute() != 0 && appointmentTime.getMinute() != 30) {
            throw new InvalidAppointmentException("Appointments can only be booked at :00 or :30 minute marks");
        }
    }

    private void checkDoctorAvailability(Long doctorId, LocalDateTime appointmentTime, Long excludeAppointmentId) {
        boolean hasConflict = appointmentRepository.existsConflictingAppointment(
                doctorId, appointmentTime, excludeAppointmentId);

        if (hasConflict) {
            throw new AppointmentConflictException(
                    "Doctor is not available at the requested time. Please choose a different time slot.");
        }
    }

    private void validateAppointmentAccess(Appointment appointment) {
        User currentUser = getCurrentUser();

        boolean hasAccess = appointment.getUser().getId().equals(currentUser.getId()) ||
                (isDoctor(currentUser) && appointment.getDoctor().getUser().getId().equals(currentUser.getId())) ||
                isAdmin(currentUser);

        if (!hasAccess) {
            throw new InvalidAppointmentException("Access denied to this appointment");
        }
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new InvalidAppointmentException("User not authenticated");
        }

        String username = authentication.getName();
        User user = userRepository.findByEmail(username);

        if (user == null) {
            throw new ResourceNotFoundException("User not found with email: " + username);
        }

        return user;
    }

    private boolean isAdmin(User user) {
        return user != null && user.getRole() == Role.ADMIN;
    }

    private boolean isDoctor(User user) {
        return user != null && user.getRole() == Role.DOCTOR;
    }

    private AppointmentDto convertToDto(Appointment appointment) {
        return AppointmentDto.builder()
                .id(appointment.getId())
                .userId(appointment.getUser() != null ? appointment.getUser().getId() : null)
                .doctorId(appointment.getDoctor() != null ? appointment.getDoctor().getId() : null)
                .doctorName(appointment.getDoctor() != null && appointment.getDoctor().getUser() != null
                        ? appointment.getDoctor().getUser().getName() : null)
                .specialization(appointment.getDoctor() != null ? appointment.getDoctor().getSpecialization() : null)
                .patientName(appointment.getPatientName())
                .patientEmail(appointment.getPatientEmail())
                .patientPhone(appointment.getPatientPhone())
                .age(appointment.getAge())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .cancellationReason(appointment.getCancellationReason())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}

