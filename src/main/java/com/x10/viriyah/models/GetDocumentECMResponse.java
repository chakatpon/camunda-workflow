package com.x10.viriyah.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
//import com.x10.viriyah.models.objFileLibrary;

import lombok.Data;

@Data
public class GetDocumentECMResponse {

    @JsonProperty("objFileLibrary")
    @JsonAlias("objFileLibrary")
    @JsonInclude(Include.NON_NULL)
    private List<com.x10.viriyah.models.objFileLibrary> objFileLibrary;
    
    public GetDocumentECMResponse() {

    }

}

