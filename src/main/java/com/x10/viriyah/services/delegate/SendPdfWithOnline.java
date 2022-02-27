package com.x10.viriyah.services.delegate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.ExternalResponse;
import com.x10.viriyah.utils.LogUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Component
public class SendPdfWithOnline implements JavaDelegate {

    @Autowired
    private RestTemplate rest;

    @Autowired
    private LogUtil logUtil;

    @Value("${url.service.pdf.send.online}")
    private String sendOnlineUrl;

    @Value("${fixed.body.send.online}")
    private String fixedBodySendOnline;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String stepName = "send-pdf-online";
        String policyNumber = (String) execution.getVariable("policyNumber");
        MDC.put("stepName", stepName);
        MDC.put("policyNumber", policyNumber);
        MDC.put("transactionNo", MDC.get("transactionNo") == null ? UUID.randomUUID().toString() : MDC.get("transactionNo"));

        log.info("Start send PDF with online.");

        String corePolicyRef = null;
        String partnerRef = (String) execution.getVariable("referenceCode");

        if (partnerRef == null) {
            corePolicyRef = execution.getVariable("policyNumber").toString();
            partnerRef = execution.getVariable("applicationNo").toString();
        } else {
            corePolicyRef = execution.getVariable("applicationNo").toString();
            partnerRef = execution.getVariable("referenceCode").toString();
        }

        ExternalRequest request = ExternalRequest.builder()
                .policyNumber((String) execution.getVariable("policyNumber"))
                .partner((String) execution.getVariable("partner"))
                .applicationNo(corePolicyRef)
                .referenceCode(partnerRef)
                .build();

        ResponseEntity<ExternalResponse> response = null;

        OnlineBody body = new OnlineBody();
        HttpEntity<Object> requestEntity;
        if ("Y".equals(fixedBodySendOnline)) {
            body.setSearchpattern("");
            body.setOrganizationCode("Viriyah");
            body.setRefDocType("");
            body.setRefDocId("64100/POL/000000-000");
            body.setAppName("PolicyUnsigned");
            body.setSortby("");

            String base64BasicAuth = new String(Base64.encodeBase64("bigcadmin:whatever".getBytes()));
            System.out.println("Basic " + base64BasicAuth);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + base64BasicAuth);
            requestEntity = new HttpEntity<Object>(body, headers);
        } else {
            requestEntity = new HttpEntity<Object>(request);
        }

        try {
            // response = rest.postForEntity(sendOnlineUrl, fixedBodySendOnline ? body : request, ExternalResponse.class);
            response = rest.exchange(sendOnlineUrl, HttpMethod.POST, requestEntity, ExternalResponse.class);
            // response.getStatusCode()
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));

            ExternalResponse res = new ExternalResponse();
            res.setError(e.getMessage());
            res.setStatus("N");

            logUtil.error(sendOnlineUrl, execution, request, res);
            execution.setVariable("sendPdfError", true);
            return;
        }

        if ("N".equals(response.getBody().getStatus())) {
            log.error("Policy {} send PDF with online of partner {} failed.", request.getPolicyNumber(), request.getPartner());
            logUtil.error(sendOnlineUrl, execution, request, response.getBody());
            execution.setVariable("sendPdfError", true);
            return;
        }

        log.info("Policy {} send PDF with online of partner {} success.", request.getPolicyNumber(), request.getPartner());
    }

    @Data
    private class OnlineBody {
        @JsonProperty("searchpattern")
        String searchpattern;
        @JsonProperty("OrganizationCode")
        String organizationCode;
        @JsonProperty("RefDocType")
        String refDocType;
        @JsonProperty("RefDocId")
        String refDocId;
        @JsonProperty("AppName")
        String appName;
        @JsonProperty("sortby")
        String sortby;
    }

}
