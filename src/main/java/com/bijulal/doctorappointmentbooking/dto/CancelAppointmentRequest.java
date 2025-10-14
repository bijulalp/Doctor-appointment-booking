package com.bijulal.doctorappointmentbooking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CancelAppointmentRequest {
    @NotBlank(message = "Cancellation reason is required")
    private String cancellationReason;
}
