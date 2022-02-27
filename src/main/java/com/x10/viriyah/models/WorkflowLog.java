package com.x10.viriyah.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "workflowLogs")
public class WorkflowLog {

    @EmbeddedId
    private WorkflowLogPk pk;

    @Column(name = "policyNumber")
    private String policyNumber;

    @Column(name = "partnerName")
    private String partnerName;

    @Column(name = "stepName")
    private String stepName;

    @Column(name = "resultStatus")
    private String resultStatus;

    @Column(name = "jsonData")
    private String jsonData;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "createdDate")
    private Date createdDate;

    @Column(name = "createdBy")
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updatedDate")
    private Date updatedDate;

    @Column(name = "updatedBy")
    private String updatedBy;

}
