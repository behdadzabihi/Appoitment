package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.dto.AppointmentDTO;
import com.blubank.doctorappointment.dto.DoctorDTO;
import com.blubank.doctorappointment.mapper.AppointmentMapper;
import com.blubank.doctorappointment.mapper.DoctorMapper;
import com.blubank.doctorappointment.model.Appointment;
import com.blubank.doctorappointment.model.Doctor;
import com.blubank.doctorappointment.service.impl.AppointmentService;
import com.blubank.doctorappointment.service.impl.DoctorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DoctorRestController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorServiceImpl doctorService;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private AppointmentMapper appointmentMapper;

    @PostMapping("/appointments")
    public ResponseEntity<Doctor> saveAppointment(@RequestBody DoctorDTO doctorDTO) {
        Doctor doctor= doctorMapper.toV(doctorDTO);
        doctorService.save(doctor);
        return ResponseEntity.status(HttpStatus.CREATED).build() ;
    }

    @PostMapping("/{doctorId}/appointments")
    public ResponseEntity<?> createAppointments(@PathVariable Long doctorId,
                                                @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                                                @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            List<Appointment> appointments = appointmentService.createAppointments(doctorId, startTime, endTime);
            return ResponseEntity.ok(appointments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{doctorId}/appointments/open")
    public ResponseEntity<List<Appointment>> getOpenAppointments(@PathVariable Long doctorId) {
        List<Appointment> openAppointments = appointmentService.getOpenAppointments(doctorId);
        return ResponseEntity.ok(openAppointments);
    }


    @GetMapping("/{doctorId}/appointments/taken")
    public ResponseEntity<List<Appointment>> getTakenAppointments(@PathVariable Long doctorId) {
        List<Appointment> takenAppointments = appointmentService.getTakenAppointments(doctorId);
        return ResponseEntity.ok(takenAppointments);
    }

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long appointmentId) {
        try {
            appointmentService.deleteAppointment(appointmentId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getReason());
        }
    }
    @PutMapping("/appointments/{appointmentId}")
    public ResponseEntity<AppointmentDTO> updatePatientAppointment(@RequestBody AppointmentDTO appointmentDTO) {
        Appointment appointment=appointmentMapper.toV(appointmentDTO);
        appointmentService.updateAppointment(appointment);
        return ResponseEntity.ok(appointmentDTO);
    }
}
