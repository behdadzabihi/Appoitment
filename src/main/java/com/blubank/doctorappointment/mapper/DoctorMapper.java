package com.blubank.doctorappointment.mapper;

import com.blubank.doctorappointment.dto.DoctorDTO;
import com.blubank.doctorappointment.model.Doctor;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;


@Mapper(componentModel = "spring")
@Component
public interface DoctorMapper extends BaseMapper<DoctorDTO, Doctor> {
    @Override
    DoctorDTO toT(Doctor doctor);

    @Override
    Doctor toV(DoctorDTO doctorDTO);

    @Override
    List<DoctorDTO> toT(List<Doctor> list);

    @Override
    List<Doctor> toV(List<DoctorDTO> list);

}
