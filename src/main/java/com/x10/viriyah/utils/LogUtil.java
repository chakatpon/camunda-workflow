package com.x10.viriyah.utils;

import java.util.Date;
import java.util.concurrent.Future;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.ExternalResponse;
import com.x10.viriyah.models.WorkflowLog;
import com.x10.viriyah.models.WorkflowLogPk;
import com.x10.viriyah.services.impl.WorkflowLogService;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LogUtil {

    @Autowired
    private WorkflowLogService logService;
    
    public void success(DelegateExecution execution) {
        try {
            WorkflowLogPk pk = new WorkflowLogPk();
            pk.setTransactionId((String) execution.getVariable("transactionId"));
            pk.setErrorDescription("");
            WorkflowLog entity = new WorkflowLog();
            entity.setPk(pk);
            entity.setPolicyNumber((String) execution.getVariable("policyNumber"));
            entity.setPartnerName((String) execution.getVariable("partner"));
            entity.setStepName("");
            entity.setResultStatus("Y");
            entity.setJsonData("");
            entity.setCreatedDate(new Date());
            entity.setCreatedBy("SYSTEM");
            entity.setUpdatedDate(new Date());
            entity.setUpdatedBy("SYSTEM");
        
			logService.saveWorkflowLog(entity);
		} catch (Exception e) {
            e.printStackTrace();
            log.error("Cannot insert Workflow Log: "+e);
        }
    }

    public void exception(Exception e, String url, DelegateExecution execution, ExternalRequest request) {
        ExternalResponse res = new ExternalResponse();
        res.setError(e.getMessage());
        res.setStatus("N");
        this.error(url, execution, request, res);
    }

    public void error(String path, DelegateExecution execution, ExternalRequest request, ExternalResponse response) {
        Future<String> async = errorAsync(path, execution, request, response);
        while (!async.isDone()) {
            try {
                wait(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    @Async
    public Future<String> errorAsync(String path, DelegateExecution execution, ExternalRequest request, ExternalResponse response) {
        try {
            WorkflowLogPk pk = new WorkflowLogPk();
            pk.setTransactionId((String) execution.getVariable("transactionId"));
            pk.setErrorDescription(response.getError() == null ? "" : response.getError().substring(0, Math.min(255, response.getError().length())));
            WorkflowLog entity = new WorkflowLog();
            entity.setPk(pk);
            entity.setPolicyNumber((String) execution.getVariable("policyNumber"));
            entity.setPartnerName((String) execution.getVariable("partner"));
            entity.setStepName(path);
            entity.setResultStatus(response.getStatus());
            entity.setJsonData((new ObjectMapper()).writeValueAsString(request));
            entity.setCreatedDate(new Date());
            entity.setCreatedBy("SYSTEM");
            entity.setUpdatedDate(new Date());
            entity.setUpdatedBy("SYSTEM");
        
			logService.saveWorkflowLog(entity);
		} catch (Exception e) {
            log.error("Cannot insert Workflow Log: {}", ExceptionUtils.getStackTrace(e));
        }
        
        return ConcurrentUtils.constantFuture("success");
    }

}
