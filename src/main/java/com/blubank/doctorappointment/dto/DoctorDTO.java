package com.blubank.doctorappointment.dto;

import com.blubank.doctorappointment.common.BaseDTO;
import com.blubank.doctorappointment.model.Appointment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
public class DoctorDTO extends BaseDTO {

    @ApiModelProperty(required = false,hidden = true)
    private String name;

}
