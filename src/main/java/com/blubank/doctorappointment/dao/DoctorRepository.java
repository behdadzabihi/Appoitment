package com.blubank.doctorappointment.dao;


import com.blubank.doctorappointment.model.Appointment;
import com.blubank.doctorappointment.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Long> {
}
