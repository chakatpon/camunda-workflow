package com.x10.viriyah.services.business;

import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.ExternalResponse;
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
public class GeneratePdfService {

    private LogUtil logUtil;

    @Autowired
    public void setLogUtil(LogUtil logUtil) {
        this.logUtil = logUtil;
    }

    private ExternalService externalService;

    @Autowired
    public void setExternalService(ExternalService externalService) {
        this.externalService = externalService;
    }

    @Value("${url.service.pdf.generate}")
    private String generatePdfUrl;

    public void execute(DelegateExecution execution) {
        if (execution.getVariable("broker") == null) {
            String errorMsg = String.format("Broker Customer not found in Decision Table with class: %s, subclass: %s, partner: %s", execution.getVariable("class"), execution.getVariable("subClass"), execution.getVariable("partner"));
            log.error(errorMsg);
            ExternalResponse res = new ExternalResponse();
            res.setError(errorMsg);
            res.setStatus("N");
            logUtil.error(generatePdfUrl, execution, ExternalRequest.builder().build(), res);
            throw new BpmnError("External Service Error.");
        }

        log.info("Start generate PDF file with policy number: {}", execution.getVariable("policyNumber"));
        ExternalRequest request = ExternalRequest.builder().policyNumber((String) execution.getVariable("policyNumber")).build();
        try {
            ResponseEntity<ExternalResponse> response = externalService.postRequest(generatePdfUrl, request);
            if (response.getBody() == null || "N".equals(response.getBody().getStatus())) {
                log.error("Generate PDF file with policy number: {} failed.", execution.getVariable("policyNumber"));
                logUtil.error(generatePdfUrl, execution, request, response.getBody());
                throw new BpmnError("External Service Error.");
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            logUtil.exception(e, generatePdfUrl, execution, request);
            throw new BpmnError("External Service Error.");
        }

        log.info("Done generate PDF file with policy number: {}", execution.getVariable("policyNumber"));
    }

}
