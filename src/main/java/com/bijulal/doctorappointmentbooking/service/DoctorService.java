package com.bijulal.doctorappointmentbooking.service;

import com.bijulal.doctorappointmentbooking.dto.DoctorDto;
import com.bijulal.doctorappointmentbooking.dto.DoctorRegister;
import com.bijulal.doctorappointmentbooking.dto.RegistrationDTO;
import com.bijulal.doctorappointmentbooking.model.Doctor;
import com.bijulal.doctorappointmentbooking.model.Role;
import com.bijulal.doctorappointmentbooking.model.User;
import com.bijulal.doctorappointmentbooking.repository.DoctorRepository;
import com.bijulal.doctorappointmentbooking.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;

    public DoctorService(DoctorRepository doctorRepository, UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    public List<DoctorDto> getAllDoctors() {

        System.out.println("doctor service entered =========== ");
        List<DoctorDto> doctorDtos = doctorRepository.findAll().stream()
                .map(doctor -> DoctorDto.builder()
                        .id(doctor.getId())
                        .name(doctor.getUser().getName())
                        .specialization(doctor.getSpecialization())
                        .email(doctor.getUser().getEmail())
                        .phone(doctor.getUser().getPhone())
                        .build()
                ).toList();

        System.out.println("doctor service exiting ========== " +  doctorDtos.size());
        return doctorDtos;
    }

    public Doctor addDoctor(@RequestBody DoctorRegister doctor) {

        Doctor doctorToAdd = new Doctor();

        User user = userRepository.findById(doctor.getUserId())
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (user.getRole() != Role.DOCTOR) {
            user.setRole(Role.DOCTOR);
        }

        userRepository.save(user);

        doctorToAdd.setUser(user);
        doctorToAdd.setSpecialization(doctor.getSpecialization());
        return doctorRepository.save(doctorToAdd);
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Doctor not found with id: " + id));
    }

    public void updateDoctor(Long id, DoctorDto doctor) {
        Doctor doc = doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("doctor not found with id : " + id));

        Doctor updatedDoctor = new Doctor();
//        updatedDoctor.setName();
    }


    public Doctor createDoctor(RegistrationDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setPhone(dto.getPhone());
        userRepository.save(user);


        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setSpecialization( dto.getSpecialization() );
        doctorRepository.save(doctor);

        return doctor;
    }
}
