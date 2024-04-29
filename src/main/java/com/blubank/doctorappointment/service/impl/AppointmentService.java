package com.blubank.doctorappointment.service.impl;

import com.blubank.doctorappointment.common.exception.AppointmentAlreadyTakenException;
import com.blubank.doctorappointment.dao.AppointmentRepository;
import com.blubank.doctorappointment.model.Appointment;
import com.blubank.doctorappointment.model.Doctor;
import com.blubank.doctorappointment.model.Patient;
import com.blubank.doctorappointment.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService implements com.blubank.doctorappointment.service.AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private DoctorServiceImpl doctorServiceImpl;

    @Autowired
    private PatientServiceImpl patientService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @CacheEvict(value = "openAppointments", allEntries = true)
    public List<Appointment> createAppointments(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("Creating appointments for doctorId: {} from {} to {}", doctorId, startTime, endTime);
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("End date cannot be sooner than start date.");
        }

        List<Appointment> appointments = new ArrayList<>();
        LocalDateTime currentTime = startTime;

        while (currentTime.isBefore(endTime)) {
            LocalDateTime nextTime = currentTime.plusMinutes(30);
            if (nextTime.isAfter(endTime)) {
                break;
            }

            Appointment appointment = new Appointment();
            Doctor doctor = doctorServiceImpl.findById(doctorId);
            appointment.setDoctor(doctor);
            appointment.setStartTime(currentTime);
            appointment.setEndTime(nextTime);
            appointments.add(appointment);

            currentTime = nextTime;
        }

        if (appointments.isEmpty()) {
            throw new IllegalArgumentException("The specified time range does not allow for any 30-minute appointments.");
        }

        List<Appointment> savedAppointments = appointmentRepository.saveAll(appointments);
        sendAppointmentCreatedMessage(savedAppointments);
        return savedAppointments;
    }

    @Cacheable(value = "openAppointments", key = "#doctorId")
    public List<Appointment> getOpenAppointments(Long doctorId) {
        logger.info("Fetching open appointments for doctorId: {}", doctorId);
        return appointmentRepository.findByDoctorIdAndPatientIsNull(doctorId);
    }

    public List<Appointment> getTakenAppointments(Long doctorId) {
        List<Appointment> takenAppointments = appointmentRepository.findByDoctorIdAndPatientIsNotNull(doctorId);
        return takenAppointments.stream()
                .map(appointment -> {
                    appointment.setPatient(appointment.getPatient()); // Ensure patient details are loaded
                    return appointment;
                })
                .collect(Collectors.toList());
    }

    public void deleteAppointment(Long appointmentId) {
        logger.info("Deleting appointment with ID: {}", appointmentId);
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (appointment.getPatient() != null) {
            throw new AppointmentAlreadyTakenException("Appointment is taken by a patient");
        }


        appointment.setDeleted(true);
        appointmentRepository.save(appointment);
    }

    public List<Appointment> findOpenAppointmentsForDay(Long doctorId, LocalDate day) {
        logger.info("Finding open appointments for doctorId: {} on day: {}", doctorId, day);
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = day.plusDays(1).atStartOfDay();
        return appointmentRepository.findByDoctorIdAndStartTimeBetweenAndPatientIsNull(doctorId, startOfDay, endOfDay);
    }

    public List<Appointment> findAppointmentsByPatientPhoneNumber(String phoneNumber) {
        logger.info("Finding appointments by patient phone number: {}", phoneNumber);
        return appointmentRepository.findByPatientPhoneNumber(phoneNumber);
    }

    public Appointment updateAppointment(Appointment appointment) {
        logger.info("Updating appointment with ID: {}", appointment.getId());
        Appointment lastAppointment = appointmentRepository.findById(appointment.getId())
                .orElseThrow(() -> new NotFoundException("Appointment not found"));
        Long doctorId = appointment.getDoctor().getId();
        Doctor doctor = doctorServiceImpl.findById(doctorId);
        Long patientId = appointment.getPatient().getId();
        Patient patient = patientService.findById(patientId);
        lastAppointment.setId(patient.getId());
        lastAppointment.setDoctor(doctor);
        lastAppointment.setPatient(patient);
        lastAppointment.setStartTime(appointment.getStartTime());
        lastAppointment.setEndTime(appointment.getEndTime());
        lastAppointment.setDeleted(appointment.isDeleted());
        return appointmentRepository.save(lastAppointment);
    }

    private void sendAppointmentCreatedMessage(List<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            String message = "Appointment created: " + appointment.getId();
            redisTemplate.convertAndSend("appointment-created-channel", message);
            logger.info("Sent appointment created message: {}", message);
        }
    }
}
