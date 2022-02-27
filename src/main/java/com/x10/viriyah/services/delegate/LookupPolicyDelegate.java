package com.x10.viriyah.services.delegate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.ExternalResponse;
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
import java.util.UUID;

@Slf4j
@Component
public class LookupPolicyDelegate implements JavaDelegate {

    @Autowired
    private RestTemplate rest;

    @Value("${url.service.policyinfoNonMotor}")
    private String policyInfoNonMotorUrl;

    @Value("${url.service.policyinfo}")
    private String policyInfoUrl;

    @Value("${url.service.pdf.send.email}")
    private String sendEmailUrl;

    public ExternalResponse getPolicyInfo(DelegateExecution execution) throws Exception {
        ExternalRequest request = ExternalRequest.builder()
                .policyNumber((String) execution.getVariable("policyNumber"))
                .classes((String) execution.getVariable("originalClass"))
                .build();

        //--- code to support NonMotor PolicyInfo
        String policyInfoAPIUrl = "";
        String classes = (String) execution.getVariable("originalClass");
        if ("NONMOTOR".equals(classes)) {//--- use policyinfoUrl (NonMotor)
            policyInfoAPIUrl = policyInfoNonMotorUrl;
        } else { //--- use policyinfoUrl (default, VMI, CMI)
            policyInfoAPIUrl = policyInfoUrl;
        }

        ResponseEntity<ExternalResponse> response = null;
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            String requeststr = objectMapper.writeValueAsString(request);

            log.info("Request url: {}", policyInfoAPIUrl);
            log.info("Request string: {}", requeststr);
            //requeststr = JSONSerializer.serializeObject(request);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Cache-Control", "no-cache, no-store");
            headers.set("x-cdata-authtoken", "3b8W0r7d9Z6m9p2J0z0n");
            HttpEntity<String> entity = new HttpEntity<String>(requeststr, headers);
            log.info(entity.getBody());

            rest.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            response = rest.postForEntity(policyInfoAPIUrl, entity, ExternalResponse.class);

            return response.getBody();
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));

            ExternalResponse res = new ExternalResponse();
            res.setError(e.getMessage());
            res.setStatus("N");

            //logUtil.error(policyInfoUrl, execution, request, res);
            execution.setVariable("getPolicyInfoError", true);
            return res;
        }
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String stepName = "lookup-policy-information";
        String policyNumber = (String) execution.getVariable("policyNumber");
        MDC.put("stepName", stepName);
        MDC.put("policyNumber", policyNumber);
        MDC.put("transactionNo", MDC.get("transactionNo") == null ? UUID.randomUUID().toString() : MDC.get("transactionNo"));

        log.info("Start lookup policy info.");
        ExternalResponse policyInfo = getPolicyInfo(execution);

        ObjectMapper objectMapper0 = new ObjectMapper();
        String polstr = objectMapper0.writeValueAsString(policyInfo);
        execution.setVariable("policyinfo", polstr);
        log.info("policy information: {}", polstr);
    }

}
