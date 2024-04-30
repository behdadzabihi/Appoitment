package com.blubank.doctorappointment.service.impl;

import com.blubank.doctorappointment.dao.DoctorRepository;
import com.blubank.doctorappointment.model.Doctor;
import com.blubank.doctorappointment.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorServiceImpl implements BaseService<Doctor> {

    @Autowired
    private DoctorRepository repository;

    @Override
    public Doctor save(Doctor doctor) {
        return repository.save(doctor);
    }

    @Override
    public Doctor update(Doctor doctor) {
        Doctor lastDoctor = findById(doctor.getId());
        lastDoctor.setName(doctor.getName());
        return repository.save(lastDoctor);
    }

    @Override
    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }

    @Override
    public Doctor findById(Long id) {
        Optional<Doctor> optionalDoctor=repository.findById(id);
        if(!optionalDoctor.isPresent()){
            throw new NotFoundException("Doctor not found");
        }
        return optionalDoctor.get();
    }

    @Override
    public List<Doctor> findAll() {
        return repository.findAll();
    }
}
