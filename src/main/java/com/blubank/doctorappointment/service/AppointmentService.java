package com.blubank.doctorappointment.service;

import com.blubank.doctorappointment.model.Appointment;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {

    List<Appointment> createAppointments(Long doctorId, LocalDateTime startTime, LocalDateTime endTime);

    Page<Appointment> getOpenAppointments(Long doctorId,int page,int size);

    Page<Appointment> getTakenAppointments(Long doctorId,int page,int size);

    void deleteAppointment(Long appointmentId);

    Page<Appointment> findOpenAppointmentsForDay(Long doctorId, LocalDate day,int page,int size);

    Appointment findAppointmentsByPatientPhoneNumber(String phoneNumber);

    Appointment updateAppointment(Appointment appointment);
}
