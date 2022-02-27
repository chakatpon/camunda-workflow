package com.x10.viriyah.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;


import lombok.Data;

@Data
public class ExternalResponse {

    @JsonProperty("status")
    @JsonAlias("Status")
    @JsonInclude(Include.NON_NULL)
    private String status;

    @JsonProperty("error")
    @JsonAlias("Error")
    @JsonInclude(Include.NON_NULL)
    private String error;

    @JsonProperty("applicationNo")
    @JsonAlias("applicationNo")
    @JsonInclude(Include.NON_NULL)
    private String applicationNo;

    @JsonProperty("@odata.context")
    @JsonAlias("@odata.context")
    @JsonInclude(Include.NON_NULL)
    private String odataContext;

    @JsonProperty("value")
    @JsonAlias("value")
    @JsonInclude(Include.NON_NULL)
    private List<value> value;


    @JsonInclude(Include.NON_NULL)
    private String integrationStatusCode;

    @JsonInclude(Include.NON_NULL)
    private String integrationErrorMessage;

    @JsonInclude(Include.NON_NULL)
    private String displayErrorMessage;

    @JsonInclude(Include.NON_NULL)
    private sms sms;

    @JsonInclude(Include.NON_NULL)
    private String email;

}
