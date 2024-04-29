package com.blubank.doctorappointment.dao;


import com.blubank.doctorappointment.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
    List<Appointment> findByDoctorIdAndPatientIsNull(Long doctorId);
    List<Appointment> findByDoctorIdAndPatientIsNotNull(Long doctorId);
    List<Appointment> findByDoctorIdAndStartTimeBetweenAndPatientIsNull(Long doctorId, LocalDateTime start, LocalDateTime end);
    List<Appointment> findByPatientPhoneNumber(String phoneNumber);
}
