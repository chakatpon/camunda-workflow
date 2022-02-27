package com.x10.viriyah.services.business;

import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.ExternalResponse;
import com.x10.viriyah.services.impl.ExternalService;
import com.x10.viriyah.utils.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendPdfWithSmsService {

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

    @Value("${url.service.pdf.send.sms}")
    private String sendSmsUrl;

    public void execute(DelegateExecution execution) {
        ExternalRequest request = ExternalRequest.builder()
                .policyNumber((String) execution.getVariable("policyNumber"))
                .mobile((String) execution.getVariable("mobile"))
                .build();

        try {
            ResponseEntity<ExternalResponse> response = externalService.postRequest(sendSmsUrl, request);
            if (response.getBody() == null || "N".equals(response.getBody().getStatus())) {
                log.error("Policy {} Send PDF with SMS to {} failed.", request.getPolicyNumber(), request.getMobile());
                logUtil.error(sendSmsUrl, execution, request, response.getBody());
                execution.setVariable("sendPdfError", true);
                return;
            }

        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            logUtil.exception(e, sendSmsUrl, execution, request);
            execution.setVariable("sendPdfError", true);
            return;
        }

        log.info("Policy {} Send PDF with SMS to {} success.", request.getPolicyNumber(), request.getMobile());
    }
}
