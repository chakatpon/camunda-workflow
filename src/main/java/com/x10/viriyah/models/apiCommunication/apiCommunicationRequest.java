package com.x10.viriyah.models.apiCommunication;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class apiCommunicationRequest {
    
    @JsonProperty("channels")
    @JsonAlias("channels")
    @JsonInclude(Include.NON_NULL)
    private channels channels;

    @JsonProperty("email")
    @JsonAlias("email")
    @JsonInclude(Include.NON_NULL)
    private email email;    
    
    @JsonProperty("sms")
    @JsonAlias("sms")
    @JsonInclude(Include.NON_NULL)
    private sms sms;    

}

