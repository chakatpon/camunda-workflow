package com.x10.viriyah.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SignPDFResponse extends ExternalResponse {

    @JsonProperty("signPDFLink")
    @JsonAlias("SignPDFLink")
    private List<String> signPDFLink;
    
}
