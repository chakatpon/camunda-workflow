package com.x10.viriyah.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class WorkflowLogPk implements Serializable {
    private static final long serialVersionUID = -4778988672926592914L;

    @Column(name = "transactionId")
    private String transactionId;

    @Column(name = "errorDescription")
    private String errorDescription;

}
