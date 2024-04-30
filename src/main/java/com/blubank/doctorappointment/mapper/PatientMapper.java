package com.blubank.doctorappointment.mapper;

import com.blubank.doctorappointment.dto.PatientDTO;
import com.blubank.doctorappointment.model.Patient;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;


@Mapper(componentModel = "spring")
@Component
public interface PatientMapper {

    PatientDTO toPatients(Patient patient);


    Patient toPatientDTOS(PatientDTO patientDTO);


    List<PatientDTO> toPatients(List<Patient> list);


    List<Patient> toPatientDTOS(List<PatientDTO> list);

}
