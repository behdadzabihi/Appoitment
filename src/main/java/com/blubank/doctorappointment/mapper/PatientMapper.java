package com.blubank.doctorappointment.mapper;

import com.blubank.doctorappointment.dto.DoctorDTO;
import com.blubank.doctorappointment.dto.PatientDTO;
import com.blubank.doctorappointment.model.Doctor;
import com.blubank.doctorappointment.model.Patient;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;


@Mapper(componentModel = "spring")
@Component
public interface PatientMapper extends BaseMapper<PatientDTO, Patient> {
    @Override
    PatientDTO toT(Patient patient);

    @Override
    Patient toV(PatientDTO patientDTO);

    @Override
    List<PatientDTO> toT(List<Patient> list);

    @Override
    List<Patient> toV(List<PatientDTO> list);

}
