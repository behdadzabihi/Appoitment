package com.blubank.doctorappointment.dto;

import com.blubank.doctorappointment.common.BaseDTO;
import com.blubank.doctorappointment.model.Doctor;
import com.blubank.doctorappointment.model.Patient;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class AppointmentDTO extends BaseDTO {


    @ApiModelProperty(required = false,hidden = true)
    private LocalDateTime startTime;

    @ApiModelProperty(required = false,hidden = true)
    private LocalDateTime endTime;

    @ApiModelProperty(required = false,hidden = true)
    private boolean deleted = false;

    @ApiModelProperty(required = false,hidden = true)
    private DoctorDTO doctor;

    @ApiModelProperty(required = false,hidden = true)
    private PatientDTO patient;

}
