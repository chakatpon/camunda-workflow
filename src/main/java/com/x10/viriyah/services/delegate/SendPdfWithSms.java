package com.x10.viriyah.services.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.x10.viriyah.models.ExternalResponse;
import com.x10.viriyah.models.apiCommunication.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
public class SendPdfWithSms implements JavaDelegate {

    @Autowired
    private RestTemplate rest;


    @Value("${url.service.policyinfo}")
    private String policyInfoUrl;

    @Value("${url.service.pdf.send.sms}")
    private String sendSmsUrl;

    @Value("${url.service.pdf.send.clientId}")
    private String clientId;

    @Value("${url.service.pdf.send.clientSecret}")
    private String clientSecret;


    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String stepName = "send-pdf-sms";
        String policyNumber = (String) execution.getVariable("policyNumber");
        MDC.put("stepName", stepName);
        MDC.put("policyNumber", policyNumber);
        MDC.put("transactionNo", MDC.get("transactionNo") == null ? UUID.randomUUID().toString() : MDC.get("transactionNo"));

        log.info("Start send PDF with SMS.");

        // ObjectMapper objectMapper0 = new ObjectMapper();
        // String polstr = (String) execution.getVariable("policyinfo");	// .writeValueAsString(policyInfo);
        // log.info("policyinfo: " + polstr);
        // ExternalResponse policyInfo = objectMapper0.readValue(polstr, ExternalResponse.class);

        // List<value> polinfo = policyInfo.getValue();

        // value pol = new value();
        // if (polinfo.size() > 0) {
        // 	pol = polinfo.get(0);
        // }

        channels channels = new channels();
        channels.setEmail(false);
        channels.setSms(true);
        channels.setLine(false);

        destmobile dest1 = destmobile.builder()
                .mobileNumber((String) execution.getVariable("mobile"))
                .build();

        List<destmobile> smsto = new ArrayList<destmobile>();
        smsto.add(dest1);


        String pdfpwd = (String) execution.getVariable("pdfPassword");
        if (pdfpwd.length() >= 4) {
            pdfpwd = pdfpwd.substring(pdfpwd.length() - 4, pdfpwd.length());
        }

        List<String> attributelist = Arrays.asList("webLink", "citizenID");
        List<String> valuelist = Arrays.asList((String) execution.getVariable("returnLink"),
                pdfpwd);

        templateDetails templatedt = templateDetails.builder()
                .attributelist(attributelist)
                .valuelist(valuelist)
                .build();


        sms smscomm = sms.builder()
                .mobileNumbers(smsto)
                .message("")
                .templateID("000002")
                .templateDetails(templatedt)
                .build();


        apiCommunicationRequest request = apiCommunicationRequest.builder()
                .channels(channels)
                .sms(smscomm)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String requeststr = objectMapper.writeValueAsString(request);

        Calendar now = Calendar.getInstance();
        //now.add(Calendar.YEAR,2);
        String requestTimeStr = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(now.getTime());

        log.info("Request string: {}", requeststr);
        //requeststr = JSONSerializer.serializeObject(request);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
        headers.set("sourceTransID", "Camunda-EPolicy-" + (String) execution.getVariable("policyNumber"));
        headers.set("clientId", clientId);
        headers.set("clientSecret", clientSecret);
        headers.set("requestTime", requestTimeStr);
        headers.set("languagePreference", "th");
        HttpEntity<String> entity = new HttpEntity<String>(requeststr, headers);

        ResponseEntity<ExternalResponse> response = null;
        try {
            rest.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            response = rest.postForEntity(sendSmsUrl, entity, ExternalResponse.class);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));

            ExternalResponse res = new ExternalResponse();
            res.setError(e.getMessage());
            res.setStatus("N");

            //logUtil.error(sendEmailUrl, execution, request, res);
            execution.setVariable("sendPdfSMSError", true);
            return;
        }

        if ("N".equals(response.getBody().getStatus())) {
            //log.error("Send PDF with email to: {} for policy: {} failed.", request.getEmail(), request.getPolicyNumber());
            //logUtil.error(sendEmailUrl, execution, request, response.getBody());
            execution.setVariable("sendPdfSMSError", true);
        }

        //log.info("Policy {} Send PDF with SMS to {} success.", request.getPolicyNumber(), request.getMobile());
    }

}
