package com.x10.viriyah.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class value {

    // "policystartdate": "11-08-2021 00:00:00",
    // "platenumber": "5กล897   ",
    // "policyenddate": "11-08-2022 16:30:00",
    // "firstnameth": "ธัญญา",
    // "grosspremium": "0",
    // "flagonline": "3",
    // "lastnameth": "มั่งมีศรีสุข",
    // "policytitle": "ประกันภัยรถยนต์ประเภท 1"

    public value() {

    }

    @JsonProperty("policystartdate")
    @JsonAlias("policystartdate")
    @JsonInclude(Include.NON_NULL)
    private String policyStartDate;

    @JsonProperty("platenumber")
    @JsonAlias("platenumber")
    @JsonInclude(Include.NON_NULL)
    private String plateNumber;

    @JsonProperty("policyenddate")
    @JsonAlias("policyenddate")
    @JsonInclude(Include.NON_NULL)
    private String policyEndDate;

    @JsonProperty("firstname")
    @JsonAlias("firstname")
    @JsonInclude(Include.NON_NULL)
    private String firstName;
    
    @JsonProperty("lastname")
    @JsonAlias("lastname")
    @JsonInclude(Include.NON_NULL)
    private String lastName;

    @JsonProperty("grosspremium")
    @JsonAlias("grosspremium")
    @JsonInclude(Include.NON_NULL)
    private String grossPremium;

    @JsonProperty("flagonline")
    @JsonAlias("flagonline")
    @JsonInclude(Include.NON_NULL)
    private String flagOnline;

    @JsonProperty("policytitle")
    @JsonAlias("policytitle")
    @JsonInclude(Include.NON_NULL)
    private String policyTitle;
            
    @JsonProperty("paymentmode")
    @JsonAlias("paymentmode")
    @JsonInclude(Include.NON_NULL)
    private String paymentMode;
                
    @JsonProperty("remark")
    @JsonAlias("remark")
    @JsonInclude(Include.NON_NULL)
    private String remark;
}
