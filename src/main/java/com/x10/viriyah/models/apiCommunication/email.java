package com.x10.viriyah.models.apiCommunication;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class email {

    private String sender;

    private String subject;

    @JsonProperty("to")
    @JsonAlias("to")
    private List<destemail> emailto;

    private String content;

    private String contentType;

    private String encoding;

    private String templateID;

    private templateDetails templateDetails;

    @JsonProperty("attachments")
    @JsonAlias("attachments")
    @JsonInclude(Include.NON_NULL)
    private attachments attachments;
    
}

