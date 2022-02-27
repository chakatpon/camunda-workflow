package com.x10.viriyah.services.impl;

import java.util.List;

import javax.transaction.Transactional;

import com.x10.viriyah.models.WorkflowLog;
import com.x10.viriyah.repository.WorkflowLogRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class WorkflowLogServiceImpl implements WorkflowLogService {
    
    @Autowired
    private WorkflowLogRepository repository;

    @Override
    public List<WorkflowLog> getWorkflowLogByTransactionId(String transactionId) {
        return repository.findByPkTransactionId(transactionId);
    }

    @Override
    public void saveWorkflowLog(WorkflowLog entity) throws Exception {
        repository.save(entity);
    }
}
