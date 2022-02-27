package com.x10.viriyah.services.impl;

import java.util.List;

import com.x10.viriyah.models.WorkflowLog;

public interface WorkflowLogService {

    public List<WorkflowLog> getWorkflowLogByTransactionId(String transactionId);

    public void saveWorkflowLog(WorkflowLog entity) throws Exception;
    
}
