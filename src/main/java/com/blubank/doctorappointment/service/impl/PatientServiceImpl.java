package com.blubank.doctorappointment.service.impl;

import com.blubank.doctorappointment.dao.PatientRepository;
import com.blubank.doctorappointment.model.Doctor;
import com.blubank.doctorappointment.model.Patient;
import com.blubank.doctorappointment.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImpl implements BaseService<Patient> {

    @Autowired
    private PatientRepository repository;

    @Override
    public Patient save(Patient patient) {
        return repository.save(patient);
    }

    @Override
    public Patient update(Patient patient) {
        Patient lastPatient = findById(patient.getId());
        lastPatient.setId(patient.getId());
        lastPatient.setFirstname(patient.getFirstname());
        lastPatient.setLastname(patient.getLastname());
        lastPatient.setPhoneNumber(patient.getPhoneNumber());
        return repository.save(lastPatient);
    }

    @Override
    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    @Override
    public Patient findById(Long id) {
        Optional<Patient> optionalPatient = repository.findById(id);
        if (!optionalPatient.isPresent()) {
            throw new NotFoundException("Patient not found");
        }
        return optionalPatient.get();
    }

    @Override
    public List<Patient> findAll() {
        return repository.findAll();
    }
}
