package com.bijulal.doctorappointmentbooking.dto;

import com.bijulal.doctorappointmentbooking.model.AppointmentStatus;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
 @Builder
public class AppointmentDto {
    private Long id;
    private Long userId;
    private Long doctorId;
    private String doctorName;
    private String specialization;
    private String patientName;
    private String patientEmail;
    private String patientPhone;
    private Integer age;
    private LocalDateTime appointmentTime;
    private AppointmentStatus status;
    private String notes;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
