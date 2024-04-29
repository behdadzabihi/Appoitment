package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.model.Appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {

    List<Appointment> createAppointments(Long doctorId, LocalDateTime startTime, LocalDateTime endTime);

    List<Appointment> getOpenAppointments(Long doctorId);

    List<Appointment> getTakenAppointments(Long doctorId);

    void deleteAppointment(Long appointmentId);

    List<Appointment> findOpenAppointmentsForDay(Long doctorId, LocalDate day);

    List<Appointment> findAppointmentsByPatientPhoneNumber(String phoneNumber);
}
