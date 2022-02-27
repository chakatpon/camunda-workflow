package com.x10.viriyah.services.delegate;

import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.ExternalResponse;
import com.x10.viriyah.utils.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Slf4j
@Component
public class GeneratePdfDelegate implements JavaDelegate {

	@Autowired
	private RestTemplate rest;

	@Autowired
	private LogUtil logUtil;

	@Value("${url.service.pdf.generate}")
	private String generatePdfUrl;

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		String stepName = "generate-pdf";
		String policyNumber = (String) execution.getVariable("policyNumber");
		MDC.put("stepName", stepName);
		MDC.put("policyNumber", policyNumber);
		MDC.put("transactionNo", MDC.get("transactionNo") == null ? UUID.randomUUID().toString() : MDC.get("transactionNo"));

		log.info("Start generate PDF file");

		ExternalRequest request = ExternalRequest.builder()
						.classes((String) execution.getVariable("originalClass"))
						.subClass((String) execution.getVariable("originalSubClass"))
						.policyNumber((String) execution.getVariable("policyNumber"))
						.build();
		ResponseEntity<ExternalResponse> response;
		try {
			response = rest.postForEntity(generatePdfUrl, request, ExternalResponse.class);
		} catch (Exception e) {
			log.error(ExceptionUtils.getStackTrace(e));

			ExternalResponse res = new ExternalResponse();
			res.setError(e.getMessage());
			res.setStatus("N");

			logUtil.error(generatePdfUrl, execution, request, res);
			throw new BpmnError("External Service Error.");
		}

		if ("N".equals(response.getBody().getStatus())) {
			log.error("Generate PDF file with policy number: {} failed.", execution.getVariable("policyNumber"));
			ExternalResponse res = new ExternalResponse();
											res.setError("Generate PDF file with policy number: "+execution.getVariable("policyNumber")+" failed.");
											res.setStatus("N");
			logUtil.error(generatePdfUrl, execution, request, res);
			// log.error("External service return fail.");
			execution.setVariable("sendPdfError", true);
			throw new BpmnError("External Service Error.");
		}
		execution.setVariable("sendPdfError", null);
		log.info("Done generate PDF file with policy number: {}", execution.getVariable("policyNumber"));
	}

}
