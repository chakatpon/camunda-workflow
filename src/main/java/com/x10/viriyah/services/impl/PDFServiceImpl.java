package com.x10.viriyah.services.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;


import java.io.IOException;


import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.nio.charset.StandardCharsets; 

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.GetDocumentECMResponse;
import com.x10.viriyah.models.objFileLibrary;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PDFServiceImpl implements PDFService {

	@Autowired
	private RestTemplate rest;
    

    @Value("${url.service.pdf.sign}")
	private String signPdfUrl;

//    @Value("${url.service.ecm.ecmapi}")
//	private String ecmURL;

 //   @Value("${url.service.ecm.org}")
//	private String ecmORG;

    @Override
    public byte[] addPassword(byte[] templateByte, String pdfPassword)
    {

        String USER_PASS = pdfPassword;
        String OWNER_PASS = "viriyahins2021";
        PdfReader pdfReader = null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(templateByte.length);

        byteArrayOutputStream.write(templateByte, 0, templateByte.length); 

        try
        {

            pdfReader = new PdfReader(templateByte);
            PdfStamper stamper = new PdfStamper(pdfReader, byteArrayOutputStream);
            stamper.setEncryption(USER_PASS.getBytes(), OWNER_PASS.getBytes(),
                    PdfWriter.ALLOW_PRINTING,
                    PdfWriter.ENCRYPTION_AES_256 | PdfWriter.DO_NOT_ENCRYPT_METADATA);
            stamper.close();
            pdfReader.close();

            //return byteArrayOutputStream.toByteArray();

        }
        catch (Exception e)
        {

            e.printStackTrace();
        }

        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public byte[] signPDF(byte[] pdfcontent) 
    {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            //headers.add("PRIVATE-TOKEN", "xyz");

            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("workerName","inet");
            map.add("encoding","base64");
            map.add("data",Base64.getEncoder().encodeToString(pdfcontent));
            map.add("processType","signDocument");
            map.add("REQUEST_METADATA","");

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            
            ResponseEntity<byte[]> response = restTemplate.exchange("http://primekey.viriyah.co.th:8080/signserver/process",
                            HttpMethod.POST,
                            entity,
                            byte[].class);

            return response.getBody();
            
    }


    public byte[] downloadPDF(String pdfurl) throws IOException {
    
        byte[] res = new byte[0];

        URL url = new URL(pdfurl);

        Boolean redirect = false;
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        int status = conn.getResponseCode();
        if (status != HttpURLConnection.HTTP_OK) {
            if (status == HttpURLConnection.HTTP_MOVED_TEMP
                || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER)
            redirect = true;
        }

        if (redirect) 
        {
            String newUrl = conn.getHeaderField("Location").replace("http://vmedia", "https://vmedia");
            url = new URL(newUrl);
            
        }

        
        try (BufferedInputStream in = new BufferedInputStream(url.openStream());
        ByteArrayOutputStream OutputStream = new ByteArrayOutputStream()) {
            byte dataBuffer[] = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                OutputStream.write(dataBuffer, 0, bytesRead);
            }

            res = OutputStream.toByteArray();
        } catch (IOException e) {
            // handle exception
        }

        

        return res;

      }

    
      @Override
      public GetDocumentECMResponse GetDocumentECM(String ecmURL, String organizationCode, String policyNumber, String isProtected) {
        try {


            String AppName = "PolicySigned";
            if (isProtected == "Y") {
                AppName = AppName + "Protected";
            } else {
                if (isProtected == "N") {
                    AppName = AppName + "Protected";
                } else {
                    AppName = AppName + "%";
                } 
                
            }

            ExternalRequest request = ExternalRequest.builder()
                                            .organizationCode(organizationCode)
                                            .refDocId(policyNumber)
                                            .appName(AppName)
                                            .sortby("RefDocType, Created DESC")
											.build();
                                            
            ObjectMapper objectMapper = new ObjectMapper();
            String requeststr = objectMapper.writeValueAsString(request);
            //requeststr = JSONSerializer.serializeObject(request);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization","Basic Y2huZGV2OmNobjIxYw==");
            headers.set("Accept", "*/*");

            HttpEntity<String> entity = new HttpEntity<String>(requeststr,headers);

            GetDocumentECMResponse response;
            log.info("ecmURL: " + ecmURL);
            log.info("request: " + requeststr);
            rest.getMessageConverters()
        		.add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			response = rest.postForObject(ecmURL, entity, GetDocumentECMResponse.class);

            ObjectMapper objectMapper0 = new ObjectMapper();
            String responsestr = objectMapper0.writeValueAsString(response);
            log.info("response: " + responsestr);

            return response;
        } catch (com.fasterxml.jackson.core.JsonProcessingException ex){
            log.error("GetDocumentECMJsonProcessingException: " + ex.getMessage());
            GetDocumentECMResponse res = new GetDocumentECMResponse();
            List<objFileLibrary> listf = new ArrayList<objFileLibrary>();
            objFileLibrary ff = new objFileLibrary();
            ff.setFileId("1");
            ff.setFileName(ex.getMessage());
          
            listf.add(ff);
            res.setObjFileLibrary(listf);
            return res;
        } catch (Exception ex){
            log.error("GetDocumentECMError: " + ex.getMessage());
            GetDocumentECMResponse res = new GetDocumentECMResponse();
            List<objFileLibrary> listf = new ArrayList<objFileLibrary>();
            objFileLibrary ff = new objFileLibrary();
            ff.setFileId("1");
            ff.setFileName(ex.getMessage());
          
            listf.add(ff);
            res.setObjFileLibrary(listf);
            return res;
        }    

        }

}
