package com.x10.viriyah.models.apiCommunication;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class templateDetails {

  
    @JsonProperty("attribute")
    @JsonAlias("attribute")  
    private List<String> attributelist;

    @JsonProperty("value")
    @JsonAlias("value")
    private List<String> valuelist;
    
}

