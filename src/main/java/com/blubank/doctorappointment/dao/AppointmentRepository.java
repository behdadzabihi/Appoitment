package com.blubank.doctorappointment.dao;


import com.blubank.doctorappointment.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
    Page<Appointment> findByDoctorIdAndPatientIsNull( Pageable pageable,Long doctorId);
    Page<Appointment> findByDoctorIdAndPatientIsNotNull( Pageable pageable,Long doctorId);
    Page<Appointment> findByDoctorIdAndStartTimeBetweenAndPatientIsNull( Pageable pageable,Long doctorId, LocalDateTime start, LocalDateTime end);
    Appointment findByPatientPhoneNumber(String phoneNumber);
}
