package com.bijulal.doctorappointmentbooking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDto {
    private Long id;
    private String patientName;
    private String patientEmail;
    private LocalDateTime appointmentTime;
    private Long doctorId;
}
