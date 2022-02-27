package com.x10.viriyah.services.delegate;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.ExternalResponse;
import com.x10.viriyah.models.GetDocumentECMResponse_singleFile;
import com.x10.viriyah.models.ecm.sharedlinkResponse;
import com.x10.viriyah.models.objFileLibrary;
import com.x10.viriyah.utils.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.camunda.bpm.engine.delegate.BpmnError;
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
import java.util.Calendar;
import java.util.UUID;

@Slf4j
@Component
public class SignPdfDelegate implements JavaDelegate {

    @Autowired
    private RestTemplate rest;

    @Autowired
    private LogUtil logUtil;

    @Value("${url.service.pdf.protect}")
    private String protectPdfUrl;

    @Value("${url.service.ecm.ecmapi}")
    private String ecmURL;

    @Value("${url.service.ecm.sharelinkapi}")
    private String sharelinkURL;

    @Value("${url.service.ecm.shortlinkdomain}")
    private String shortlinkdomain;

    @Value("${url.service.ecm.org}")
    private String ecmORG;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String stepName = "protect-pdf";
        String policyNumber = (String) execution.getVariable("policyNumber");
        MDC.put("stepName", stepName);
        MDC.put("policyNumber", policyNumber);
        MDC.put("transactionNo", MDC.get("transactionNo") == null ? UUID.randomUUID().toString() : MDC.get("transactionNo"));

        log.info("Start signing PDF file");
        execution.setVariable("returnLink", "");
        //execution.setVariable("returnLinkUnProtected", "");

        if ("Y".equals(execution.getVariable("insured"))) {
            log.info("Signing with protect {}", execution.getVariable("insured"));
            ExternalRequest request = ExternalRequest.builder()
                    .policyNumber((String) execution.getVariable("policyNumber"))
                    .isProtected(true)
                    .pdfPassword((String) execution.getVariable("pdfPassword"))
                    .classes((String) execution.getVariable("originalClass"))
                    .build();
            log.info("Start protected PDF with Protected mode.");

            ResponseEntity<ExternalResponse> response;
            try {
                response = rest.postForEntity(protectPdfUrl, request, ExternalResponse.class);
                execution.setVariable("signPDFInsuredStatus", response.getBody().getStatus());
                log.info("Sign with protect {}", response.getBody().getStatus());
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));

                ExternalResponse res = new ExternalResponse();
                res.setError(e.getMessage());
                res.setStatus("N");
                logUtil.error(protectPdfUrl, execution, request, res);
                throw new BpmnError("External Service Error.");
            }

            if ("N".equals(response.getBody().getStatus())) {
                //save log to DB
                log.error("Protected PDF with Protected mode failed.");
                ExternalResponse res = new ExternalResponse();
                //set JSON to response to client (Mule)
                res.setError("Protected PDF with Protected mode failed.");
                res.setStatus("N");
                logUtil.error(protectPdfUrl, execution, request, res);
                // log.error("External service return fail.");
                //throw new BpmnError("External Service Error.");
                return;
            }
            execution.setVariable("sendPdfError", null);
            log.info("Done protected PDF with protected mode.");


            com.x10.viriyah.models.GetDocumentECMResponse_singleFile doc = new com.x10.viriyah.models.GetDocumentECMResponse_singleFile();
            sharedlinkResponse sharelinkdoc = new sharedlinkResponse();

            try {


                String AppName = "PolicySignedProtected";


                ExternalRequest requestecm = ExternalRequest.builder()
                        .organizationCode(ecmORG)
                        .refDocType("Policy")
                        .refDocId((String) execution.getVariable("policyNumber"))
                        .appName(AppName)
                        .sortby("RefDocType, Created DESC")
                        .build();

                ObjectMapper objectMapperecm = new ObjectMapper();
                String requeststr = objectMapperecm.writeValueAsString(requestecm);
                //requeststr = JSONSerializer.serializeObject(request);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Authorization", "Basic Y2huZGV2OmNobjIxYw==");
                headers.set("Accept", "*/*");
                headers.set("Cache-Control", "no-cache");

                HttpEntity<String> entity = new HttpEntity<String>(requeststr, headers);


                log.info("ecmURL: " + ecmURL);
                log.info("request: " + requeststr);
                rest.getMessageConverters()
                        .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

                doc = rest.postForObject(ecmURL, entity, GetDocumentECMResponse_singleFile.class);

                ObjectMapper objectMapper0 = new ObjectMapper();
                String responsestr = objectMapper0.writeValueAsString(doc);
                log.info("response: " + responsestr);


                // create shared link
                Calendar now = Calendar.getInstance();
                now.add(Calendar.YEAR, 2);
                String expiresAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now.getTime());
                ExternalRequest requestsharelink = ExternalRequest.builder()
                        .nodeId(doc.getObjFileLibrary().getFileId())
                        .expiresAt(expiresAt)
                        .build();

                //ObjectMapper objectMappersharedlink = new ObjectMapper();
                String requestsharedlinkstr = objectMapperecm.writeValueAsString(requestsharelink);
                //requeststr = JSONSerializer.serializeObject(request);
                HttpHeaders headers2 = new HttpHeaders();
                headers2.setContentType(MediaType.APPLICATION_JSON);
                headers2.set("Authorization", "Basic Y2huZGV2OmNobjIxYw==");
                headers2.set("Accept", "*/*");
                headers2.set("Cache-Control", "no-cache");

                HttpEntity<String> entity2 = new HttpEntity<String>(requestsharedlinkstr, headers2);


                log.info("sharelinkURL: " + sharelinkURL);
                log.info("request(SharedLink): " + requestsharedlinkstr);
                rest.getMessageConverters()
                        .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

                sharelinkdoc = rest.postForObject(sharelinkURL, entity2, sharedlinkResponse.class);


            } catch (Exception ex) {
                log.error("GetDocumentECMError: " + ex.getMessage());
                doc = new GetDocumentECMResponse_singleFile();
                objFileLibrary ff = new objFileLibrary();
                ff.setFileId("1");
                ff.setFileName(ex.getMessage());
                doc.setObjFileLibrary(ff);

            }

            //doc.getObjFileLibrary().get(0).getCreated()

            //log.info("returnLink: " + doc.getObjFileLibrary().getFilePath());
            //execution.setVariable("returnLink", doc.getObjFileLibrary().getFilePath());

            log.info("returnLink: " + shortlinkdomain + sharelinkdoc.getEntry().getId());
            execution.setVariable("returnLink", shortlinkdomain + sharelinkdoc.getEntry().getId());

        }


        if ("Y".equals(execution.getVariable("broker"))) {
            log.info("  --  Signing without protect {}", execution.getVariable("broker"));
            ExternalRequest request = ExternalRequest.builder()
                    .policyNumber((String) execution.getVariable("policyNumber"))
                    .isProtected(false)
                    .pdfPassword((String) execution.getVariable("pdfPassword"))
                    .classes((String) execution.getVariable("originalClass"))
                    .build();
            log.info("Start protected PDF with UnProtected mode.");


            ResponseEntity<ExternalResponse> response;
            try {
                response = rest.postForEntity(protectPdfUrl, request, ExternalResponse.class);
                execution.setVariable("signPDFBrokerStatus", response.getBody().getStatus());
                log.info("  =  Sign without protect {}", response.getBody().getStatus());
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));

                ExternalResponse res = new ExternalResponse();
                res.setError(e.getMessage());
                res.setStatus("N");
                logUtil.error(protectPdfUrl, execution, request, res);
                throw new BpmnError("External Service Error.");
            }

            if ("N".equals(response.getBody().getStatus())) {
                log.error("Protected PDF with UnProtected mode failed.");
                ExternalResponse res = new ExternalResponse();
                res.setError("Protected PDF with UnProtected mode failed.");
                res.setStatus("N");
                logUtil.error(protectPdfUrl, execution, request, res);
                // log.error("External service return fail.");
                //throw new BpmnError("External Service Error.");

                //reset returnlink to empty, for retrying capable in camunda..
                execution.setVariable("returnLink", "");
                return;
            }
            execution.setVariable("sendPdfError", null);
            log.info("Done protected PDF with UnProtected mode.");
        }
    }
}
