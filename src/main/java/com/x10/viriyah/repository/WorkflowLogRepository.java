package com.x10.viriyah.repository;

import java.util.List;

import com.x10.viriyah.models.WorkflowLog;
import com.x10.viriyah.models.WorkflowLogPk;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowLogRepository extends CrudRepository<WorkflowLog, WorkflowLogPk> {

    public List<WorkflowLog> findByPkTransactionId(String transactionId);

}
