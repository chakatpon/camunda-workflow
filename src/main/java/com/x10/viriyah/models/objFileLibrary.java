package com.x10.viriyah.models;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class objFileLibrary {
    
    @JsonProperty("AppName")
    @JsonAlias("AppName")
    @JsonInclude(Include.NON_NULL)
    private String appName;

    @JsonProperty("Created")
    @JsonAlias("Created")
    @JsonInclude(Include.NON_NULL)
    private String created;
 
    @JsonProperty("CreatedBy")
    @JsonAlias("CreatedBy")
    @JsonInclude(Include.NON_NULL)
    private String createdBy;
 
    @JsonProperty("OrganizationCode")
    @JsonAlias("OrganizationCode")
    @JsonInclude(Include.NON_NULL)
    private String organizationCode;
 
    @JsonProperty("RefDocId")
    @JsonAlias("RefDocId")
    @JsonInclude(Include.NON_NULL)
    private String refDocId;
 
    @JsonProperty("RefDocType")
    @JsonAlias("RefDocType")
    @JsonInclude(Include.NON_NULL)
    private String refDocType;
 
    @JsonProperty("Updated")
    @JsonAlias("Updated")
    @JsonInclude(Include.NON_NULL)
    private String updated;
 
    @JsonProperty("UpdatedBy")
    @JsonAlias("UpdatedBy")
    @JsonInclude(Include.NON_NULL)
    private String updatedBy;
 
    @JsonProperty("XmlData")
    @JsonAlias("XmlData")
    @JsonInclude(Include.NON_NULL)
    private String xmlData;
 
    @JsonProperty("fileId")
    @JsonAlias("fileId")
    @JsonInclude(Include.NON_NULL)
    private String fileId;
 
    @JsonProperty("fileName")
    @JsonAlias("fileName")
    @JsonInclude(Include.NON_NULL)
    private String fileName;
 
    @JsonProperty("filePath")
    @JsonAlias("filePath")
    @JsonInclude(Include.NON_NULL)
    private String filePath;
 
    @JsonProperty("fileSizeKB")
    @JsonAlias("fileSizeKB")
    @JsonInclude(Include.NON_NULL)
    private String fileSizeKB;
 
    @JsonProperty("fileStatus")
    @JsonAlias("fileStatus")
    @JsonInclude(Include.NON_NULL)
    private String fileStatus;
        
}
