package com.blubank.doctorappointment.service;
import com.blubank.doctorappointment.common.exception.AppointmentAlreadyTakenException;
import com.blubank.doctorappointment.common.exception.AppointmentNotFoundException;
import com.blubank.doctorappointment.common.exception.NotFoundException;
import com.blubank.doctorappointment.dao.AppointmentRepository;
import com.blubank.doctorappointment.model.Appointment;
import com.blubank.doctorappointment.model.Doctor;
import com.blubank.doctorappointment.model.Patient;
import com.blubank.doctorappointment.service.impl.AppointmentServiceImpl;
import com.blubank.doctorappointment.service.impl.DoctorServiceImpl;
import com.blubank.doctorappointment.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorServiceImpl doctorServiceImpl;

    @Mock
    private PatientServiceImpl patientService;

    @InjectMocks
    private AppointmentServiceImpl appointmentServiceImpl;

    @Test
    public void testCreateAppointments_EndTimeBeforeStartTime() {
        LocalDateTime startTime = LocalDateTime.of(2023, 4, 1, 9, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 4, 1, 8, 0);
        Long doctorId = 1L;

        assertThrows(IllegalArgumentException.class, () -> appointmentServiceImpl.createAppointments(doctorId, startTime, endTime));
    }

    @Test
    public void testCreateAppointments_PeriodLessThan30Minutes() {
        LocalDateTime startTime = LocalDateTime.of(2023, 4, 1, 9, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 4, 1, 9, 15);
        Long doctorId = 1L;

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        lenient().when(doctorServiceImpl.findById(doctorId)).thenReturn(doctor);

        assertThrows(IllegalArgumentException.class, () -> appointmentServiceImpl.createAppointments(doctorId, startTime, endTime));
    }


    @Test
    public void testGetOpenAppointments() {
        Long doctorId = 1L;
        Appointment appointment1 = new Appointment();
        Appointment appointment2 = new Appointment();
        List<Appointment> expectedAppointments = Arrays.asList(appointment1, appointment2);

        when(appointmentRepository.findByDoctorIdAndPatientIsNull(doctorId)).thenReturn(expectedAppointments);

        List<Appointment> openAppointments = appointmentServiceImpl.getOpenAppointments(doctorId);

        assertEquals(expectedAppointments, openAppointments);
    }

    @Test
    public void testGetTakenAppointments() {
        Long doctorId = 1L;
        Patient patient = new Patient();
        Appointment appointment1 = new Appointment();
        appointment1.setPatient(patient);
        Appointment appointment2 = new Appointment();
        appointment2.setPatient(patient);
        List<Appointment> expectedAppointments = Arrays.asList(appointment1, appointment2);

        when(appointmentRepository.findByDoctorIdAndPatientIsNotNull(doctorId)).thenReturn(expectedAppointments);

        List<Appointment> takenAppointments = appointmentServiceImpl.getTakenAppointments(doctorId);

        assertEquals(expectedAppointments, takenAppointments);
        takenAppointments.forEach(appointment -> assertEquals(patient, appointment.getPatient()));
    }
    @Test
    public void testDeleteAppointment_AppointmentNotFound() {
        Long appointmentId = 1L;

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> appointmentServiceImpl.deleteAppointment(appointmentId));
    }

    @Test
    public void testDeleteAppointment_AppointmentTaken() {
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();
        appointment.setPatient(new Patient());

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        assertThrows(AppointmentAlreadyTakenException.class, () -> appointmentServiceImpl.deleteAppointment(appointmentId));
    }

    @Test
    public void testDeleteAppointment_Success() {
        Long appointmentId = 1L;
        Appointment appointment = new Appointment();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        appointmentServiceImpl.deleteAppointment(appointmentId);


        assertTrue(appointment.isDeleted());

    }
    @Test
    public void testFindOpenAppointmentsForDay() {
        Long doctorId = 1L;
        LocalDate day = LocalDate.now();
        LocalDateTime startOfDay = day.atStartOfDay();
        LocalDateTime endOfDay = day.plusDays(1).atStartOfDay();
        Appointment appointment1 = new Appointment();
        Appointment appointment2 = new Appointment();
        List<Appointment> expectedAppointments = Arrays.asList(appointment1, appointment2);

        when(appointmentRepository.findByDoctorIdAndStartTimeBetweenAndPatientIsNull(doctorId, startOfDay, endOfDay)).thenReturn(expectedAppointments);

        List<Appointment> openAppointments = appointmentServiceImpl.findOpenAppointmentsForDay(doctorId, day);

        assertEquals(expectedAppointments, openAppointments);
    }

    @Test
    public void testFindAppointmentsByPatientPhoneNumber() {
        String phoneNumber = "1234567890";
        Appointment appointment1 = new Appointment();
        Appointment appointment2 = new Appointment();
        List<Appointment> expectedAppointments = Arrays.asList(appointment1, appointment2);

        when(appointmentRepository.findByPatientPhoneNumber(phoneNumber)).thenReturn(expectedAppointments);

        List<Appointment> appointments = appointmentServiceImpl.findAppointmentsByPatientPhoneNumber(phoneNumber);

        assertEquals(expectedAppointments, appointments);
    }
    @Test
    public void testUpdateAppointment_Success() throws Exception {
        // Prepare mock data
        Long appointmentId = 1L;
        Long doctorId = 2L;
        Long patientId = 3L;
        LocalDateTime newStartTime = LocalDateTime.now().plusHours(1);
        LocalDateTime newEndTime = LocalDateTime.now().plusHours(2);
        boolean isDeleted = false;

        Appointment existingAppointment = new Appointment();
        existingAppointment.setId(appointmentId);

        Appointment updateData = new Appointment();
        updateData.setStartTime(newStartTime);
        updateData.setEndTime(newEndTime);
        updateData.setDeleted(isDeleted);
        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        updateData.setDoctor(doctor);
        Patient patient = new Patient();
        patient.setId(patientId);
        updateData.setPatient(patient);

        // Mock repository behavior
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(doctorServiceImpl.findById(doctorId)).thenReturn(doctor);
        when(patientService.findById(patientId)).thenReturn(patient);
        when(appointmentRepository.save(existingAppointment)).thenReturn(existingAppointment);

        // Call the method under test
        Appointment updatedAppointment = appointmentServiceImpl.updateAppointment(updateData);

        // Assertions
        assertNotNull(updatedAppointment);
        assertEquals(appointmentId, updatedAppointment.getId());
        assertEquals(newStartTime, updatedAppointment.getStartTime());
        assertEquals(newEndTime, updatedAppointment.getEndTime());
        assertEquals(isDeleted, updatedAppointment.isDeleted());
        assertEquals(doctorId, updatedAppointment.getDoctor().getId());
        assertEquals(patientId, updatedAppointment.getPatient().getId());
    }


    public void testUpdateAppointment_NotFound() throws  AppointmentNotFoundException {
        Long appointmentId = 1L;
        Appointment updateData = new Appointment();

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        appointmentServiceImpl.updateAppointment(updateData);
    }
}



