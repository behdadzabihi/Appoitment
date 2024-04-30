package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.common.PagingData;
import com.blubank.doctorappointment.dto.AppointmentDTO;
import com.blubank.doctorappointment.dto.PatientDTO;
import com.blubank.doctorappointment.mapper.AppointmentMapper;
import com.blubank.doctorappointment.mapper.PatientMapper;
import com.blubank.doctorappointment.model.Appointment;
import com.blubank.doctorappointment.model.Patient;
import com.blubank.doctorappointment.service.impl.AppointmentServiceImpl;
import com.blubank.doctorappointment.service.impl.PatientServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
@AllArgsConstructor
public class PatientController {

    private AppointmentServiceImpl appointmentServiceImpl;


    private PatientServiceImpl patientService;


    private PatientMapper patientMapper;

    private AppointmentMapper   appointmentMapper;


    @PostMapping("patients")
    public ResponseEntity<PatientDTO> save(@RequestBody PatientDTO patientDTO){
        Patient patient = patientMapper.toPatientDTOS(patientDTO);
        patientService.save(patient);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/appointments/{doctorId}/{day}")
    public ResponseEntity<PagingData<AppointmentDTO>> getOpenAppointmentsForDay(@PathVariable Long doctorId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day
      ,@RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
        Page<Appointment> appointmentPage=appointmentServiceImpl.findOpenAppointmentsForDay(doctorId,day, page, size);
        int total=appointmentPage.getTotalPages();
        List<Appointment> appointments=appointmentPage.getContent();
        List<AppointmentDTO> appointmentDTOS=appointmentMapper.toAppointmentDTOS(appointments);
        PagingData<AppointmentDTO> appointmentDTOPagingData=new PagingData<>(total,page,appointmentDTOS);
        return ResponseEntity.ok(appointmentDTOPagingData);
    }
    @GetMapping("/appointments")
    public ResponseEntity<AppointmentDTO> getPatientAppointments(@RequestParam String phoneNumber) {
        Appointment appointment = appointmentServiceImpl.findAppointmentsByPatientPhoneNumber(phoneNumber);
        AppointmentDTO appointmentDTO= appointmentMapper.toAppointmentDTO(appointment);
        return ResponseEntity.ok(appointmentDTO);
    }

    public PagingData<AppointmentDTO> paging(AppointmentServiceImpl appointmentService, Long doctorId, int page, int size){
        Page<Appointment> appointmentPage=appointmentService.getOpenAppointments(doctorId, page, size);
        int total=appointmentPage.getTotalPages();
        List<Appointment> appointments=appointmentPage.getContent();
        List<AppointmentDTO> appointmentDTOS=appointmentMapper.toAppointmentDTOS(appointments);
        PagingData<AppointmentDTO> appointmentDTOPagingData=new PagingData<>(total,page,appointmentDTOS);
        return appointmentDTOPagingData;
    }
}
