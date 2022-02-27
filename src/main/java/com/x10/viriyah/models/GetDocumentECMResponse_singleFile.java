package com.x10.viriyah.models;



import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;


import lombok.Data;

@Data
public class GetDocumentECMResponse_singleFile {

    @JsonProperty("objFileLibrary")
    @JsonAlias("objFileLibrary")
    @JsonInclude(Include.NON_NULL)
    private com.x10.viriyah.models.objFileLibrary objFileLibrary;
    
    public GetDocumentECMResponse_singleFile() {

    }

}

