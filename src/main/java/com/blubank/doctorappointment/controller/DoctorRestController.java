package com.blubank.doctorappointment.controller;

import com.blubank.doctorappointment.common.PagingData;
import com.blubank.doctorappointment.dto.AppointmentDTO;
import com.blubank.doctorappointment.dto.DoctorDTO;
import com.blubank.doctorappointment.mapper.AppointmentMapper;
import com.blubank.doctorappointment.mapper.DoctorMapper;
import com.blubank.doctorappointment.model.Appointment;
import com.blubank.doctorappointment.model.Doctor;
import com.blubank.doctorappointment.service.impl.AppointmentServiceImpl;
import com.blubank.doctorappointment.service.impl.DoctorServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class DoctorRestController {


    private AppointmentServiceImpl appointmentServiceImpl;


    private DoctorServiceImpl doctorService;


    private DoctorMapper doctorMapper;


    private AppointmentMapper appointmentMapper;

    @PostMapping("/appointments")
    public ResponseEntity<Doctor> create(@RequestBody DoctorDTO doctorDTO) {
        Doctor doctor = doctorMapper.toDoctorDTOS(doctorDTO);
        doctorService.save(doctor);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{doctorId}/appointments")
    public ResponseEntity<?> createAppointments(@PathVariable Long doctorId,
                                                @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                                                @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        try {
            List<Appointment> appointments = appointmentServiceImpl.createAppointments(doctorId, startTime, endTime);
            return ResponseEntity.ok(appointments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{doctorId}/appointments/open")
    public ResponseEntity<PagingData<AppointmentDTO>> getOpenAppointments(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PagingData<AppointmentDTO> appointmentDTOPagingData=paging(appointmentServiceImpl, doctorId, page, size);
        return ResponseEntity.ok(appointmentDTOPagingData);
    }


    @GetMapping("/{doctorId}/appointments/taken")
    public ResponseEntity<PagingData<AppointmentDTO>> getTakenAppointments(
            @PathVariable Long doctorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PagingData<AppointmentDTO> appointmentDTOPagingData=paging(appointmentServiceImpl, doctorId, page, size);
        return ResponseEntity.ok(appointmentDTOPagingData);
    }

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long appointmentId) {
        try {
            appointmentServiceImpl.deleteAppointment(appointmentId);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getReason());
        }
    }

    @PutMapping("/appointments/{appointmentId}")
    public ResponseEntity<AppointmentDTO> updatePatientAppointment(@RequestBody AppointmentDTO appointmentDTO) {
        Appointment appointment = appointmentMapper.toAppointmentDTO(appointmentDTO);
        appointmentServiceImpl.updateAppointment(appointment);
        return ResponseEntity.ok(appointmentDTO);
    }
    public PagingData<AppointmentDTO> paging(AppointmentServiceImpl appointmentService,Long doctorId,int page, int size){
        Page<Appointment> appointmentPage=appointmentService.getOpenAppointments(doctorId, page, size);
        int total=appointmentPage.getTotalPages();
        List<Appointment> appointments=appointmentPage.getContent();
        List<AppointmentDTO> appointmentDTOS=appointmentMapper.toAppointmentDTOS(appointments);
        PagingData<AppointmentDTO> appointmentDTOPagingData=new PagingData<>(total,page,appointmentDTOS);
        return appointmentDTOPagingData;
    }
}
