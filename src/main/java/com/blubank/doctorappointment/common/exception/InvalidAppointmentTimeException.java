package com.blubank.doctorappointment.common.exception;

public class InvalidAppointmentTimeException extends RuntimeException {
    public InvalidAppointmentTimeException(String message) {
        super(message);
    }
}
