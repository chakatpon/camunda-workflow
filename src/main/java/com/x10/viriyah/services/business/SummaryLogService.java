package com.x10.viriyah.services.business;

import com.x10.viriyah.models.WorkflowLog;
import com.x10.viriyah.services.impl.EmailService;
import com.x10.viriyah.services.impl.WorkflowLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SummaryLogService {

    private WorkflowLogService workflowLogService;

    @Autowired
    public void setWorkflowLogService(WorkflowLogService workflowLogService) {
        this.workflowLogService = workflowLogService;
    }

    private EmailService mailService;

    @Autowired
    public void setMailService(EmailService mailService) {
        this.mailService = mailService;
    }

    @Value("${fixed.mail.report}")
    private String sendMailTo;

    @Value("${fixed.mail.sendOn.failed}")
    private String sendMailFailedFlag;

    public void execute(DelegateExecution execution) {
        log.info("Start - Send mail workflow error.");
        execution.setVariable("flowFailed", "Y");
        if ("Y".equals(sendMailFailedFlag)) {
            List<WorkflowLog> workflowLog = workflowLogService.getWorkflowLogByTransactionId((String) execution.getVariable("transactionId"));

            String email = sendMailTo;
            String subject = "Error camunda";
            String content = workflowLog.stream().map(WorkflowLog::toString).collect(Collectors.joining("\n"));
            try {
                mailService.sendEmail(email, subject, content);
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }
}
