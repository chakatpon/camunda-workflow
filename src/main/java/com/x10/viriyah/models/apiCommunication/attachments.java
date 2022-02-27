package com.x10.viriyah.models.apiCommunication;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class attachments {

    @JsonInclude(Include.NON_NULL)
    private List<urls> urls;
    
    
}

