package com.bijulal.doctorappointmentbooking.dto;

import lombok.Data;

@Data
public class DoctorDto {

    private Long id;
    private String name;
    private String specialization;
    private String email;
    private String phone;
}
