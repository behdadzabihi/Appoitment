package com.blubank.doctorappointment.model;

import com.blubank.doctorappointment.common.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
public class Appointment extends BaseEntity {



    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;


    private boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "docter_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

}
