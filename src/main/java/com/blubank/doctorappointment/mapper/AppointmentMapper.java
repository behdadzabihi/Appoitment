package com.blubank.doctorappointment.mapper;

import com.blubank.doctorappointment.dto.AppointmentDTO;
import com.blubank.doctorappointment.model.Appointment;
import org.mapstruct.Mapper;

import java.util.List;
@Mapper(uses = {DoctorMapper.class, PatientMapper.class})
public interface AppointmentMapper extends BaseMapper<AppointmentDTO, Appointment> {
    @Override
    AppointmentDTO toT(Appointment appointment);

    @Override
    Appointment toV(AppointmentDTO appointmentDTO);

    @Override
    List<AppointmentDTO> toT(List<Appointment> list);

    @Override
    List<Appointment> toV(List<AppointmentDTO> list);
}
