package com.x10.viriyah.services.business;

import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.ExternalResponse;
import com.x10.viriyah.models.OnlineBody;
import com.x10.viriyah.services.impl.ExternalService;
import com.x10.viriyah.utils.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendPdfWithOnlineService {

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

    @Value("${url.service.pdf.send.online}")
    private String sendOnlineUrl;

    @Value("${fixed.body.send.online}")
    private String fixedBodySendOnline;

    public HttpEntity<Object> generateRequestEntity(ExternalRequest request, String fixedBodySendOnline) {
        OnlineBody body = new OnlineBody();
        if ("Y".equals(fixedBodySendOnline)) {
            body.setSearchpattern("");
            body.setOrganizationCode("Viriyah");
            body.setRefDocType("");
            body.setRefDocId("64100/POL/000000-000");
            body.setAppName("PolicyUnsigned");
            body.setSortby("");

            String base64BasicAuth = new String(Base64.encodeBase64("bigcadmin:whatever".getBytes()));
            System.out.println("Basic "+base64BasicAuth);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + base64BasicAuth);
            return new HttpEntity<Object>(body, headers);
        } else {
            return new HttpEntity<Object>(request);
        }
    }

    public void execute(DelegateExecution execution) {
        ExternalRequest request = ExternalRequest.builder()
                .policyNumber((String) execution.getVariable("policyNumber"))
                .partner((String) execution.getVariable("partner"))
                .build();

        HttpEntity<Object> requestEntity = generateRequestEntity(request, fixedBodySendOnline);
        try {
            ResponseEntity<ExternalResponse> response = externalService.exchange(sendOnlineUrl, HttpMethod.POST, requestEntity);
            if (response.getBody() == null || "N".equals(response.getBody().getStatus())) {
                log.error("Policy {} Send PDF with Online of partner {} failed.", request.getPolicyNumber(), request.getPartner());
                logUtil.error(sendOnlineUrl, execution, request, response.getBody());
                execution.setVariable("sendPdfError", true);
                return;
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            logUtil.exception(e, sendOnlineUrl, execution, request);
            execution.setVariable("sendPdfError", true);
            return;
        }

        log.info("Policy {} Send PDF with Online of partner {} success.", request.getPolicyNumber(), request.getPartner());
    }

}
