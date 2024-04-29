package com.blubank.doctorappointment.common.exception;

public class AppointmentAlreadyTakenException extends RuntimeException {
    public AppointmentAlreadyTakenException(String message) {
        super(message);
    }
}

