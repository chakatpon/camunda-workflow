package com.x10.viriyah.controller;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.GetDocumentECMResponse;
import com.x10.viriyah.models.GetDocumentECMResponse_singleFile;
import com.x10.viriyah.models.RequestBodyStartflow;
import com.x10.viriyah.models.objFileLibrary;


import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricVariableInstance;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;



import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import io.swagger.v3.oas.annotations.Hidden;




import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ServiceController {
    @Autowired
	private RestTemplate rest;

	@Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

	@Value("${url.service.ecm.ecmapi}")
	private String ecmURL;

    @Value("${url.service.ecm.org}")
	private String ecmORG;

    private final String POLICY_DELIVERY_WF_KEY = "Policy_Delivery";



    @PostMapping("/startflow")
	public ResponseEntity<Object> startWorkFlow(
        @RequestBody RequestBodyStartflow body
    ) {
        Map<String, Object> variables = new HashMap<String, Object>();

        variables.put("originalClass", body.getClasses().toUpperCase());
        variables.put("originalSubClass", body.getSubClass().toUpperCase());
        variables.put("originalPartner", body.getPartner().toUpperCase());

        body.setClasses("ALL");
        body.setSubClass("ALL");

        variables.put("transactionId", String.valueOf(Calendar.getInstance().getTime().getTime()));
        variables.put("class", body.getClasses().toUpperCase());
        variables.put("subClass", body.getSubClass().toUpperCase());
        variables.put("partner", body.getPartner().toUpperCase());
        variables.put("policyNumber", body.getPolicyNumber().toUpperCase());
        variables.put("email", body.getEmail());
        variables.put("mobile", body.getMobileNumber());
        variables.put("pdfPassword", body.getPdfPassword());
        variables.put("applicationNo", body.getApplicationNo().toUpperCase());
        variables.put("referenceCode", body.getReferenceCode());



        ProcessInstance instance = runtimeService.startProcessInstanceByKey(POLICY_DELIVERY_WF_KEY, variables);

        HistoricVariableInstance hisValsignPDFInsuredStatus = historyService.createHistoricVariableInstanceQuery().processInstanceId(instance.getId()).variableName("signPDFInsuredStatus").singleResult();
        String signPDFInsuredStatus = (String) hisValsignPDFInsuredStatus.getValue();
        HistoricVariableInstance hisValFlowFailed = historyService.createHistoricVariableInstanceQuery().processInstanceId(instance.getId()).variableName("flowFailed").singleResult();
        String flowFailed = (String) hisValFlowFailed.getValue();
        
        if ("Y".equals(signPDFInsuredStatus)) {
            //PDFServiceImpl pdfsvc = new PDFServiceImpl();
            GetDocumentECMResponse getdoc = GetDocumentECM(ecmORG, body.getPolicyNumber().toUpperCase(),"Y");
            return ResponseEntity.ok(getdoc);
        } else {
            if ("Y".equals(flowFailed)) {
                return ResponseEntity.ok("failed");
            } else {
                return ResponseEntity.ok("success");
            }
        }
    }


    @PostMapping("/startflow3")
	public ResponseEntity<Object> startWorkFlow3(
        @RequestBody RequestBodyStartflow body
    ) {
        Map<String, Object> variables = new HashMap<String, Object>();

        variables.put("class", body.getClasses().toUpperCase());

        String policyNumber = body.getPolicyNumber().toUpperCase();
		if (policyNumber.indexOf("/กธ/") > 0) {
			policyNumber = policyNumber.replace("/กธ/","-");
            variables.put("policyNumber", policyNumber);
		} else {
            variables.put("policyNumber", body.getPolicyNumber().toUpperCase());
        }
        log.debug((String) variables.get("class"),(String) variables.get("policyNumber"));
        ProcessInstance instance = runtimeService.startProcessInstanceByKey("GetPolicyInfo", variables);

        HistoricVariableInstance hasWsResponse = historyService.createHistoricVariableInstanceQuery().processInstanceId(instance.getId()).variableName("WsResponse").singleResult();
        String WsResponse = "";
        if (hasWsResponse != null) {
            WsResponse = (String) hasWsResponse.getValue();
        }

        return ResponseEntity.ok(WsResponse);
    }

    @PostMapping("/startflow2")
	public ResponseEntity<Object> startWorkFlow2(
        @RequestBody RequestBodyStartflow body
    ) {
        Map<String, Object> variables = new HashMap<String, Object>();

        variables.put("originalClass", body.getClasses().toUpperCase());
        variables.put("originalSubClass", body.getSubClass().toUpperCase());

        body.setClasses("ALL");
        body.setSubClass("ALL");

        variables.put("transactionId", String.valueOf(Calendar.getInstance().getTime().getTime()));
        variables.put("class", body.getClasses().toUpperCase());
        variables.put("subClass", body.getSubClass().toUpperCase());
        variables.put("partner", body.getPartner().toUpperCase());
        variables.put("email", body.getEmail());
        variables.put("mobile", body.getMobileNumber());
        variables.put("pdfPassword", body.getPdfPassword());
        variables.put("applicationNo", body.getApplicationNo().toUpperCase());
        variables.put("referenceCode", body.getReferenceCode());

        String policyNumber = body.getPolicyNumber().toUpperCase();
		if (policyNumber.indexOf("/กธ/") > 0) {
			policyNumber = policyNumber.replace("/กธ/","-");
            variables.put("policyNumber", policyNumber);
		} else {
            variables.put("policyNumber", body.getPolicyNumber().toUpperCase());
        }

        ProcessInstance instance = runtimeService.startProcessInstanceByKey(POLICY_DELIVERY_WF_KEY, variables);

        //HistoricVariableInstance hisValsignPDFInsuredStatus = historyService.createHistoricVariableInstanceQuery().processInstanceId(instance.getId()).variableName("signPDFInsuredStatus").singleResult();
        //String signPDFInsuredStatus = "";
        //if (hisValsignPDFInsuredStatus != null) {
        //     signPDFInsuredStatus = (String) hisValsignPDFInsuredStatus.getValue();
        //}
        HistoricVariableInstance hisValFlowFailed = historyService.createHistoricVariableInstanceQuery().processInstanceId(instance.getId()).variableName("flowFailed").singleResult();
        String flowFailed = "";
        if (hisValFlowFailed != null) {
            flowFailed = (String) hisValFlowFailed.getValue();
        }


            if ("Y".equals(flowFailed)) {
                log.info("Workflow Failed: {}", variables.get("policyNumber"));
                return ResponseEntity.ok("failed");
            } else {
                //PDFServiceImpl pdfsvc = new PDFServiceImpl();
                GetDocumentECMResponse getdoc = GetDocumentECM(ecmORG, policyNumber,"");
                log.info("Workflow Success: {} -- ECM Doc: {}", variables.get("policyNumber"), getdoc);
                return ResponseEntity.ok(getdoc);
            }


    }

    @Hidden
    @GetMapping("/test")
    public ResponseEntity<GetDocumentECMResponse> test() {
        //PDFServiceImpl pdfsvc = new PDFServiceImpl();
            GetDocumentECMResponse getdoc = GetDocumentECM(ecmORG, "58101-0000365","Y");
            //getdoc.getObjFileLibrary().re

            log.info("test");
            return ResponseEntity.ok(getdoc);

    }

    private GetDocumentECMResponse GetDocumentECM(String organizationCode, String policyNumber, String isProtected) {


        String AppName = "PolicySigned";
        if (isProtected == "Y") {
            AppName = AppName + "Protected";
        } else {
            if (isProtected == "N") {
                AppName = AppName + "Protected";
            } else {
                AppName = AppName + "%";
            }

        }

        ExternalRequest request = ExternalRequest.builder()
                                        .organizationCode(organizationCode)
                                        .refDocId(policyNumber)
                                        .appName(AppName)
                                        .sortby("RefDocType, Created DESC")
                                        .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String requeststr = "";
        try {
        requeststr = objectMapper.writeValueAsString(request);
        } catch(Exception e) {

        }
        //requeststr = JSONSerializer.serializeObject(request);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Basic Y2huZGV2OmNobjIxYw==");
        HttpEntity<String> entity = new HttpEntity<String>(requeststr,headers);

        ResponseEntity<GetDocumentECMResponse> response;

        try {

            //get list of doc


			response = rest.postForEntity(ecmURL, entity, GetDocumentECMResponse.class);

            return response.getBody();
        } catch (Exception ex){

            try {
                //try single doc

                ResponseEntity<GetDocumentECMResponse_singleFile> response2;
                response2 = rest.postForEntity(ecmURL, entity, GetDocumentECMResponse_singleFile.class);

                //add result to list
                List<objFileLibrary> listfile = new ArrayList<objFileLibrary>();
                listfile.add(response2.getBody().getObjFileLibrary());
                GetDocumentECMResponse ecmdoc = new   GetDocumentECMResponse();
                ecmdoc.setObjFileLibrary(listfile);

                return ecmdoc;

            } catch (Exception ex2)
            {

                log.error("ECM Doc Fail Result: " + ex2.getMessage());
                return new GetDocumentECMResponse();
            }


        }

        }




}
