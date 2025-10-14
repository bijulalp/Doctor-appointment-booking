package com.bijulal.doctorappointmentbooking.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateAppointmentRequest {
    private LocalDateTime appointmentTime;
    private String notes;
}
