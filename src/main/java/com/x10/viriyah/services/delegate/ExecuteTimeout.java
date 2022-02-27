package com.x10.viriyah.services.delegate;

import com.x10.viriyah.models.WorkflowLog;
import com.x10.viriyah.services.impl.WorkflowLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ExecuteTimeout implements JavaDelegate {

    @Autowired
    private WorkflowLogService service;

    @Autowired
    private JavaMailSender sender;

    @Value("${fixed.mail.report}")
    private String mailSendTo;

    @Value("${fixed.mail.sendOn.failed}")
    private String sendMailFailedFlag;

    @Value("${fixed.mail.from}")
    private String mailFrom;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String stepName = "send-timeout-email";
        String policyNumber = (String) execution.getVariable("policyNumber");
        MDC.put("stepName", stepName);
        MDC.put("policyNumber", policyNumber);
        MDC.put("transactionNo", MDC.get("transactionNo") == null ? UUID.randomUUID().toString() : MDC.get("transactionNo"));

        log.info("Start - Notify Admin -- Workflow error (Timeout).");
        execution.setVariable("flowFailed", "Y");
        if ("Y".equals(sendMailFailedFlag)) {
            List<WorkflowLog> workflowLog = service.getWorkflowLogByTransactionId((String) execution.getVariable("transactionId"));

            String mailSubject = "Error Camunda Execute Timeout (For Mule) " + execution.getVariable("policyNumber");
            String mailContent = workflowLog.stream().map(o -> o.toString()).collect(Collectors.joining("\n"));

            MimeMessage msg = sender.createMimeMessage();
            try {
                MimeMessageHelper helper = new MimeMessageHelper(msg, true);
                helper.setFrom(mailFrom);
                helper.setTo(InternetAddress.parse(mailSendTo));
                helper.setSubject(mailSubject);
                helper.setText(mailContent, "<p>" + mailContent.replace("\n", "\n<br />") + "</p>");

                sender.send(msg);
            } catch (Exception e) {
                e.printStackTrace();
                log.error(ExceptionUtils.getStackTrace(e));
            }


        }
    }
}
