package com.bijulal.doctorappointmentbooking.dto;

import com.bijulal.doctorappointmentbooking.model.Role;
import com.bijulal.doctorappointmentbooking.model.User;
import lombok.Data;

@Data
public class RegistrationDTO {
    private Long id;
    private String name;
    private String password;
    private String email;
    private String phone;
    private Role role;
    private String specialization;



//    ----------me-----
    private User.Gender gender;
    private String address;

}
