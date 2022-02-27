package com.x10.viriyah.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class sms {

    // "policystartdate": "11-08-2021 00:00:00",
    // "platenumber": "5กล897   ",
    // "policyenddate": "11-08-2022 16:30:00",
    // "firstnameth": "ธัญญา",
    // "grosspremium": "0",
    // "flagonline": "3",
    // "lastnameth": "มั่งมีศรีสุข",
    // "policytitle": "ประกันภัยรถยนต์ประเภท 1"

    public sms() {
        
    }

    @JsonProperty("status")
    @JsonAlias("status")
    @JsonInclude(Include.NON_NULL)
    private String status;

    @JsonProperty("rows")
    @JsonAlias("rows")
    @JsonInclude(Include.NON_NULL)
    private String rows;

    @JsonProperty("successflag")
    @JsonAlias("successflag")
    @JsonInclude(Include.NON_NULL)
    private String successflag;

    @JsonProperty("description")
    @JsonAlias("description")
    @JsonInclude(Include.NON_NULL)
    private String description;
    
            
    
}
