package com.blubank.doctorappointment.common;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
@Data
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Version
//    private Integer version;
//
//    @CreatedDate
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date createdData;
//
//    @CreatedBy
//    private String createdBy;
//
//    @LastModifiedDate
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date lastModifiedData;
//
//    @LastModifiedBy
//    private String lastModifiedBy;
}
