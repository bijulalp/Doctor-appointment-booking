package com.bijulal.doctorappointmentbooking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorDto {
    private Long id;
    private String name;
    private String specialization;
    private String email;
    private String phone;
}
