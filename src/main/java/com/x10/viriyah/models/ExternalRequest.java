package com.x10.viriyah.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExternalRequest {

    @JsonInclude(Include.NON_NULL)
    private String policyNumber;

    @JsonInclude(Include.NON_NULL)
    private String classes;

    @JsonInclude(Include.NON_NULL)
    private String subClass;

    @JsonInclude(Include.NON_NULL)
    private Boolean isProtected;

    @JsonInclude(Include.NON_NULL)
    private String pdfPassword;

    @JsonInclude(Include.NON_NULL)
    private String partner;

    @JsonInclude(Include.NON_NULL)
    private String email;

    @JsonInclude(Include.NON_NULL)
    private String mobile;

    @JsonInclude(Include.NON_NULL)
    private String applicationNo;

    @JsonInclude(Include.NON_NULL)
    private String referenceCode;

    @JsonProperty("OrganizationCode")
    @JsonAlias("OrganizationCode")
    @JsonInclude(Include.NON_NULL)
    private String organizationCode;

    @JsonProperty("RefDocType")
    @JsonAlias("RefDocType")
    @JsonInclude(Include.NON_NULL)
    private String refDocType;

    @JsonProperty("RefDocId")
    @JsonAlias("RefDocId")
    @JsonInclude(Include.NON_NULL)
    private String refDocId;

    @JsonProperty("AppName")
    @JsonAlias("AppName")
    @JsonInclude(Include.NON_NULL)
    private String appName;


    @JsonProperty("sortby")
    @JsonAlias("sortby")
    @JsonInclude(Include.NON_NULL)
    private String sortby;

    @JsonProperty("nodeId")
    @JsonAlias("nodeId")
    @JsonInclude(Include.NON_NULL)
    private String nodeId;

    @JsonProperty("expiresAt")
    @JsonAlias("expiresAt")
    @JsonInclude(Include.NON_NULL)
    private String expiresAt;

}
