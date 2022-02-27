package com.x10.viriyah.models.apiCommunication;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class sms {


    @JsonProperty("mobileNumbers")
    @JsonAlias("mobileNumbers")
    private List<destmobile> mobileNumbers;

    private String message;

    private String templateID;

    private templateDetails templateDetails;
    
}

