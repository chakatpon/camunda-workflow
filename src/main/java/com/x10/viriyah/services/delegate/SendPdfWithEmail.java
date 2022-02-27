package com.x10.viriyah.services.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.x10.viriyah.models.ExternalResponse;
import com.x10.viriyah.models.apiCommunication.*;
import com.x10.viriyah.models.value;
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
public class SendPdfWithEmail implements JavaDelegate {

    @Autowired
    private RestTemplate rest;

    @Value("${url.service.policyinfo}")
    private String policyInfoUrl;

    @Value("${url.service.pdf.send.email}")
    private String sendEmailUrl;

    @Value("${url.service.pdf.send.clientId}")
    private String clientId;

    @Value("${url.service.pdf.send.clientSecret}")
    private String clientSecret;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String stepName = "send-pdf-email";
        String policyNumber = (String) execution.getVariable("policyNumber");
        MDC.put("stepName", stepName);
        MDC.put("policyNumber", policyNumber);
        MDC.put("transactionNo", MDC.get("transactionNo") == null ? UUID.randomUUID().toString() : MDC.get("transactionNo"));

        log.info("Start send PDF with email.");

        ObjectMapper objectMapper0 = new ObjectMapper();
        String polstr = (String) execution.getVariable("policyinfo");    // .writeValueAsString(policyInfo);
        log.info("policy information: {}", polstr);
        ExternalResponse policyInfo = objectMapper0.readValue(polstr, ExternalResponse.class);

        List<value> polinfo = policyInfo.getValue();

        value pol = new value();
        if (polinfo.size() > 0) {
            pol = polinfo.get(0);
        }

        channels channels = new channels();
        channels.setEmail(true);
        channels.setSms(false);
        channels.setLine(false);

        destemail dest1 = destemail.builder()
                .email((String) execution.getVariable("email"))
                .build();

        List<destemail> emailto = new ArrayList<destemail>();
        emailto.add(dest1);


        String pdfpwd = (String) execution.getVariable("pdfPassword");
        int pwdlength = pdfpwd.length();
        if (pdfpwd.length() >= 4) {
            pdfpwd = pdfpwd.substring(pdfpwd.length() - 4, pdfpwd.length());
            pdfpwd = ("*************").substring(0, pwdlength - 4) + pdfpwd;
        }

        List<String> attributelist = Arrays.asList("firstName",
                "lastName",
                "pdtype",
                "policyNumber",
                "grossPremium",
                "startDate",
                "expiryDate",
                "idCardMask",
                "downloadLink",
                "remark",
                "paymentMode");

        List<String> valuelist = Arrays.asList(pol.getFirstName(),
                pol.getLastName(),
                pol.getPolicyTitle(),
                (String) execution.getVariable("policyNumber"),
                pol.getGrossPremium(),
                pol.getPolicyStartDate(),
                pol.getPolicyEndDate(),
                pdfpwd,
                (String) execution.getVariable("returnLink"),
                pol.getRemark(),
                pol.getPaymentMode());

        templateDetails templatedt = templateDetails.builder()
                .attributelist(attributelist)
                .valuelist(valuelist)
                .build();


        urls iurls = urls.builder()
                .fileName((String) execution.getVariable("policyNumber"))
                .fileType("pdf")
                .url((String) execution.getVariable("returnLink"))
                .build();
        List<urls> urlslist = Arrays.asList(iurls);
        attachments iattachments = attachments.builder()
                .urls(urlslist)
                .build();

        //List<attachments> attachmentslist = Arrays.asList(iattachments);


        //--- code to support NonMotor
        String templateID = "";
        String classes = (String) execution.getVariable("originalClass");
        String producttitle = "";
        if ("NONMOTOR".equals(classes)) {//--- (NonMotor)
            templateID = "000003";
            producttitle = pol.getRemark();
        } else {//--- (default, VMI, CMI)
            templateID = "000001";
            producttitle = pol.getPolicyTitle();
        }


        email emailcomm = email.builder()
                .subject("จัดส่งกรมธรรม์/Policy Delivery \"" + producttitle + "\" เลขที่/Policy Number " + (String) execution.getVariable("policyNumber"))
                .sender("noreply@viriyah.co.th")
                .emailto(emailto)
                .content("")
                .contentType("text/html")
                .encoding("UTF8")
                .templateID(templateID)
                .templateDetails(templatedt)
                .attachments(iattachments)
                .build();


        apiCommunicationRequest request = apiCommunicationRequest.builder()
                .channels(channels)
                .email(emailcomm)
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
        headers.set("sourceTransID", "Camunda-EPolicy-" + (String) execution.getVariable("policyNumber"));
        headers.set("clientId", clientId);
        headers.set("clientSecret", clientSecret);
        headers.set("requestTime", requestTimeStr);
        headers.set("languagePreference", "th");
        HttpEntity<String> entity = new HttpEntity<String>(requeststr, headers);

        ResponseEntity<ExternalResponse> response = null;
        try {
            rest.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            response = rest.postForEntity(sendEmailUrl, entity, ExternalResponse.class);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));

            ExternalResponse res = new ExternalResponse();
            res.setError(e.getMessage());
            res.setStatus("N");

            //logUtil.error(sendEmailUrl, execution, request, res);
            execution.setVariable("sendPdfWithEmailError", true);
            return;
        }

        if ("N".equals(response.getBody().getStatus())) {
            //log.error("Send PDF with email to: {} for policy: {} failed.", request.getEmail(), request.getPolicyNumber());
            //logUtil.error(sendEmailUrl, execution, request, response.getBody());
            execution.setVariable("sendPdfWithEmailError", true);
        }

        //log.info("Send PDF with email to: {} for policy: {} success.", request.getEmail(), request.getPolicyNumber());
    }

}
