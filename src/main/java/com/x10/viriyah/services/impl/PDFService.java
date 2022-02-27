package com.x10.viriyah.services.impl;

import com.x10.viriyah.models.GetDocumentECMResponse;


public interface PDFService {
    
    public byte[] addPassword(byte[] templateByte, String pdfPassword);

    public byte[] signPDF(byte[] pdfcontent);

    public GetDocumentECMResponse GetDocumentECM(String ecmURL, String organizationCode, String policyNumber, String isProtected);
    
}
