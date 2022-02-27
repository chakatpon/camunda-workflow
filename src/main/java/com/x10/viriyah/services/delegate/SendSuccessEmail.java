package com.x10.viriyah.services.delegate;

import com.x10.viriyah.utils.LogUtil;
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
import java.util.UUID;

@Slf4j
@Component
public class SendSuccessEmail implements JavaDelegate {


    @Autowired
    private JavaMailSender sender;

    @Autowired
    private LogUtil logUtil;

    @Value("${fixed.mail.report}")
    private String mailSendTo;

    @Value("${fixed.mail.sendOn.success}")
    private String sendMailSuccessFlag;

    @Value("${fixed.mail.from}")
    private String mailFrom;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String stepName = "send-success-email";
        String policyNumber = (String) execution.getVariable("policyNumber");
        MDC.put("stepName", stepName);
        MDC.put("policyNumber", policyNumber);
        MDC.put("transactionNo", MDC.get("transactionNo") == null ? UUID.randomUUID().toString() : MDC.get("transactionNo"));

        log.info("Start - Notify Admin -- Workflow success.");
        execution.setVariable("flowFailed", "N");
        if ("Y".equals(sendMailSuccessFlag)) {
            String mailSubject = "Success camunda (For Mule) " + execution.getVariable("policyNumber");
            String mailContent = "Camunda flow successfully.";

            MimeMessage msg = sender.createMimeMessage();
            try {
                MimeMessageHelper helper = new MimeMessageHelper(msg, true);
                helper.setFrom(mailFrom);
                helper.setTo(InternetAddress.parse(mailSendTo));
                helper.setSubject(mailSubject);
                helper.setText(mailContent, "<p>" + mailContent.replace("\n", "\n<br />") + "</p>");

                sender.send(msg);
                logUtil.success(execution);
            } catch (Exception e) {
                //e.printStackTrace();
                log.error(ExceptionUtils.getStackTrace(e));
            }
        }
    }

}
