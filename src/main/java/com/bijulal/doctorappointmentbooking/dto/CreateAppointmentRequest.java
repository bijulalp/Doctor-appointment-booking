package com.bijulal.doctorappointmentbooking.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateAppointmentRequest {
    @NotNull(message = "Doctor ID is required")
    private Long doctorId;

    @NotBlank(message = "Patient name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String patientName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String patientEmail;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String patientPhone;

    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 150, message = "Age must be less than 150")
    private Integer age;

    @NotNull(message = "Appointment time is required")
    @Future(message = "Appointment time must be in the future")
    private LocalDateTime appointmentTime;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;
}
