package com.x10.viriyah.services.business;

import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.SignPDFResponse;
import com.x10.viriyah.services.impl.ExternalService;
import com.x10.viriyah.utils.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SignPdfService {

    private ExternalService externalService;

    @Autowired
    public void setExternalService(ExternalService externalService) {
        this.externalService = externalService;
    }

    private LogUtil logUtil;

    @Autowired
    public void setLogUtil(LogUtil logUtil) {
        this.logUtil = logUtil;
    }

    @Value("${url.service.pdf.protect}")
    private String protectPdfUrl;

    public void execute(DelegateExecution execution) {
        ExternalRequest request = ExternalRequest.builder()
                .policyNumber((String) execution.getVariable("policyNumber"))
                .isProtected((Boolean) execution.getVariable("broker"))
                .build();

        log.info("Start protected PDF with {} mode.", "Y".equals(request.getIsProtected()) ? "Customer" : "Broker");
        try {
            ResponseEntity<SignPDFResponse> response = externalService.postRequest(protectPdfUrl, request, SignPDFResponse.class);
            if (response.getBody() == null || "N".equals(response.getBody().getStatus())) {
                log.error("Protected PDF with {} mode failed.", "Y".equals(request.getIsProtected()) ? "Customer" : "Broker");
                logUtil.error(protectPdfUrl, execution, request, response.getBody());
                throw new BpmnError("External Service Error.");
            }

            execution.setVariable("signPDFLink", response.getBody().getSignPDFLink());
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            logUtil.exception(e, protectPdfUrl, execution, request);
            throw new BpmnError("External Service Error.");
        }

        execution.setVariable("sendPdfError", null);
        log.info("Done protected PDF with {} mode.", "Y".equals(request.getIsProtected()) ? "Customer" : "Broker");
    }

}
