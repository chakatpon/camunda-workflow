package com.x10.viriyah.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OnlineBody {

    @JsonProperty("searchpattern")
    String searchpattern;

    @JsonProperty("OrganizationCode")
    String organizationCode;

    @JsonProperty("RefDocType")
    String refDocType;

    @JsonProperty("RefDocId")
    String refDocId;

    @JsonProperty("AppName")
    String appName;

    @JsonProperty("sortby")
    String sortby;

}
