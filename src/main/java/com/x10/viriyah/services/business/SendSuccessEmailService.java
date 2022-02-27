package com.x10.viriyah.services.business;

import com.x10.viriyah.services.impl.EmailService;
import com.x10.viriyah.utils.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SendSuccessEmailService {

    private EmailService mailService;

    @Autowired
    public void setMailService(EmailService mailService) {
        this.mailService = mailService;
    }

    private LogUtil logUtil;

    @Autowired
    public void setLogUtil(LogUtil logUtil) {
        this.logUtil = logUtil;
    }

    @Value("${fixed.mail.report}")
    private String sendMailTo;

    @Value("${fixed.mail.sendOn.success}")
    private String sendMailSuccessFlag;

    public void execute(DelegateExecution execution) {
        log.info("Start - Send mail workflow success.");
        execution.setVariable("flowFailed", "N");
        if ("Y".equals(sendMailSuccessFlag)) {
            String email = sendMailTo; //.stream().collect(Collectors.joining(","));
            String subject = "Success camunda";
            String content = "Camunda flow successfully.";
            try {
                mailService.sendEmail(email, subject, content);
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            }
            logUtil.success(execution);
        }
    }

}
