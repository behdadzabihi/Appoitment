package com.blubank.doctorappointment.common.exception;


public class NoAppointmentSlotsAvailableException extends RuntimeException {
    public NoAppointmentSlotsAvailableException(String message) {
        super(message);
    }
}