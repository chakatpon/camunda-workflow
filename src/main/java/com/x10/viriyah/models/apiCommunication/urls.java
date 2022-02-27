package com.x10.viriyah.models.apiCommunication;



import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class urls {

    private String fileName;
    private String fileType;
    private String url;
    
}

