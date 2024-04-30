package com.blubank.doctorappointment.mapper;

import com.blubank.doctorappointment.dto.DoctorDTO;
import com.blubank.doctorappointment.model.Doctor;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;


@Mapper(componentModel = "spring")
@Component
public interface DoctorMapper  {

    DoctorDTO toDoctors(Doctor doctor);


    Doctor toDoctorDTOS(DoctorDTO doctorDTO);


    List<DoctorDTO> toDoctors(List<Doctor> list);


    List<Doctor> toDoctorDTOS(List<DoctorDTO> list);

}
