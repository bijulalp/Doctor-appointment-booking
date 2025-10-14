package com.bijulal.doctorappointmentbooking.exception;

public class AppointmentConflictException extends RuntimeException {
    public AppointmentConflictException(String message) {
        super(message);
    }
}
