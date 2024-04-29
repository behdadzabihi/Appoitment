package com.blubank.doctorappointment.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BaseDTO {

    @ApiModelProperty(required = false,hidden = true)
    private Long id;

    @ApiModelProperty(required = true,hidden = false)
    private Integer version;

    @ApiModelProperty(required = true,hidden = false)
    private Date createdData;

    @ApiModelProperty(required = true,hidden = false)
    private String createdBy;

    @ApiModelProperty(required = true,hidden = false)
    private Date lastModifiedData;

    @ApiModelProperty(required = true,hidden = false)
    private String lastModifiedBy;
}
