package com.blubank.doctorappointment.dao;


import com.blubank.doctorappointment.model.Doctor;
import com.blubank.doctorappointment.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient,Long> {
}
