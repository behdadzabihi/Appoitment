package com.blubank.doctorappointment.service.impl;

import com.blubank.doctorappointment.common.exception.AppointmentAlreadyTakenException;
import com.blubank.doctorappointment.common.exception.AppointmentNotFoundException;
import com.blubank.doctorappointment.common.exception.InvalidAppointmentTimeException;
import com.blubank.doctorappointment.common.exception.NoAppointmentSlotsAvailableException;
import com.blubank.doctorappointment.dao.AppointmentRepository;
import com.blubank.doctorappointment.model.Appointment;
import com.blubank.doctorappointment.model.Doctor;
import com.blubank.doctorappointment.model.Patient;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AppointmentServiceImpl implements com.blubank.doctorappointment.service.AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);


    private AppointmentRepository appointmentRepository;


    private DoctorServiceImpl doctorServiceImpl;


    private PatientServiceImpl patientService;


    private RedisTemplate<String, Object> redisTemplate;

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    @CacheEvict(value = "openAppointments", allEntries = true)
    public List<Appointment> createAppointments(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("Creating appointments for doctorId: {} from {} to {}", doctorId, startTime, endTime);

        if (startTime.isAfter(endTime)) {
            logger.error("Invalid appointment time: End date cannot be sooner than start date.");
            throw new InvalidAppointmentTimeException("End date cannot be sooner than start date.");
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
            logger.error("No appointment slots available for the specified time range.");
            throw new NoAppointmentSlotsAvailableException("The specified time range does not allow for any 30-minute appointments.");
        }

        List<Appointment> savedAppointments = appointmentRepository.saveAll(appointments);
        sendAppointmentCreatedMessage(savedAppointments);
        return savedAppointments;
    }

    @Cacheable(value = "openAppointments", key = "#doctorId")
    public Page<Appointment> getOpenAppointments(Long doctorId, int page, int size) {
        logger.info("Fetching open appointments for doctorId: {}", doctorId);
        return appointmentRepository.findByDoctorIdAndPatientIsNull(PageRequest.of(page,size),doctorId);
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public Page<Appointment> getTakenAppointments(Long doctorId,int page, int size) {
        Page<Appointment> takenAppointments = appointmentRepository.findByDoctorIdAndPatientIsNotNull(PageRequest.of(page, size),doctorId);

        // Convert the Page<Appointment> to a stream
        List<Appointment> processedAppointments = takenAppointments.getContent().stream()
                .map(appointment -> {
                    // Example transformation: Ensure patient details are loaded
                    appointment.setPatient(appointment.getPatient());
                    return appointment;
                })
                .collect(Collectors.toList());
        return takenAppointments;
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
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

    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public Page<Appointment> findOpenAppointmentsForDay(Long doctorId, LocalDate day,int page,int size) {
        logger.info("Finding open appointments for doctorId: {} on day: {}", doctorId, day);
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = day.plusDays(1).atStartOfDay();
        return appointmentRepository.findByDoctorIdAndStartTimeBetweenAndPatientIsNull(PageRequest.of(page,size),doctorId, startOfDay, endOfDay);
    }

    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public Appointment findAppointmentsByPatientPhoneNumber(String phoneNumber) {
        logger.info("Finding appointments by patient phone number: {}", phoneNumber);
        return appointmentRepository.findByPatientPhoneNumber(phoneNumber);
    }

    @PreAuthorize("hasRole('ROLE_DOCTOR') or hasRole('ROLE_PATIENT')")
    @CacheEvict(value = "openAppointments", allEntries = true)
    public Appointment updateAppointment(Appointment appointment) throws AppointmentNotFoundException {
        logger.info("Updating appointment with ID: {}", appointment.getId());
        Appointment existingAppointment = appointmentRepository.findById(appointment.getId())
                .orElseThrow(() -> new AppointmentNotFoundException("Appointment not found"));

        existingAppointment.setStartTime(appointment.getStartTime() != null ? appointment.getStartTime() : existingAppointment.getStartTime());
        existingAppointment.setEndTime(appointment.getEndTime() != null ? appointment.getEndTime() : existingAppointment.getEndTime());
        existingAppointment.setDeleted(appointment.isDeleted());


        if (appointment.getDoctor() != null && appointment.getDoctor().getId() != null) {
            Long doctorId = appointment.getDoctor().getId();
            Doctor doctor = doctorServiceImpl.findById(doctorId);
            existingAppointment.setDoctor(doctor);
        }

        if (appointment.getPatient() != null && appointment.getPatient().getId() != null) {
            Long patientId = appointment.getPatient().getId();
            Patient patient = patientService.findById(patientId);
            existingAppointment.setPatient(patient);
        }

        Appointment savedAppointment = appointmentRepository.save(existingAppointment);
        return savedAppointment;
    }


    private void sendAppointmentCreatedMessage(List<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            String message = "Appointment created: " + appointment.getId();
            redisTemplate.convertAndSend("appointment-created-channel", message);
            logger.info("Sent appointment created message: {}", message);
        }
    }
}
