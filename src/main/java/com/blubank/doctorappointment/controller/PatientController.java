package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.dto.PatientDTO;
import com.blubank.doctorappointment.mapper.PatientMapper;
import com.blubank.doctorappointment.model.Appointment;
import com.blubank.doctorappointment.model.Patient;
import com.blubank.doctorappointment.service.impl.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {
    @Autowired
    private  AppointmentService appointmentService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientMapper mapper;


    @PostMapping("patients")
    public ResponseEntity<PatientDTO> save(@RequestBody PatientDTO patientDTO){
        Patient patient = mapper.toPatient(patientDTO);
        patientService.save(patient);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/appointments/{doctorId}/{day}")
    public ResponseEntity<List<Appointment>> getOpenAppointmentsForDay(@PathVariable Long doctorId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
        List<Appointment> openAppointments = appointmentService.findOpenAppointmentsForDay(doctorId, day);
        return ResponseEntity.ok(openAppointments);
    }
    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getPatientAppointments(@RequestParam String phoneNumber) {
        List<Appointment> appointments = appointmentService.findAppointmentsByPatientPhoneNumber(phoneNumber);
        return ResponseEntity.ok(appointments);
    }
}
