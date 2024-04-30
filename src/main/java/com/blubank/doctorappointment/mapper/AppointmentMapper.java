package com.blubank.doctorappointment.mapper;

import com.blubank.doctorappointment.dto.AppointmentDTO;
import com.blubank.doctorappointment.model.Appointment;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DoctorMapper.class, PatientMapper.class})
@Component
public interface AppointmentMapper {

    Appointment toAppointmentDTO(AppointmentDTO appointmentDTO);

    AppointmentDTO toAppointmentDTO(Appointment appointment);

    List<AppointmentDTO> toAppointmentDTOS(List<Appointment> list);
}
