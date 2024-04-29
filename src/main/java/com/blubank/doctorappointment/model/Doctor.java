package com.blubank.doctorappointment.model;

import com.blubank.doctorappointment.common.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
public class Doctor extends BaseEntity {

    @NotNull
    private String name;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "doctor",cascade = CascadeType.ALL)
    private List<Appointment> appointments;

}
