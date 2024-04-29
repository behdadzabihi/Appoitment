package com.blubank.doctorappointment.model;

import com.blubank.doctorappointment.common.BaseEntity;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Data
public class Patient extends BaseEntity {

    @NotNull
    private String firstname;

    @NotNull
    private String lastname;

    @NotNull
    private String phoneNumber;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "patient",cascade = CascadeType.ALL)
    private List<Appointment> appointments;

}
