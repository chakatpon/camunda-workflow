package com.x10.viriyah.services.delegate;

import com.x10.viriyah.models.objCustomerBroker;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class SetDefaultCustomerBrokerDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String stepName = "set-default-broker";
        String policyNumber = (String) execution.getVariable("policyNumber");
        MDC.put("stepName", stepName);
        MDC.put("policyNumber", policyNumber);
        MDC.put("transactionNo", MDC.get("transactionNo") == null ? UUID.randomUUID().toString() : MDC.get("transactionNo"));

        if (execution.getVariable("broker") == null) {
            log.info("Set default customer broker variables");
            //set default CustomerBroker
            objCustomerBroker custbroker = objCustomerBroker.builder().build();

            execution.setVariable("class", (String) execution.getVariable("originalClass"));
            execution.setVariable("subClass", (String) execution.getVariable("originalSubClass"));
            //execution.setVariable("partner", (String) execution.getVariable("originalPartner"));

            execution.setVariable("insured", custbroker.getInsured());
            execution.setVariable("broker", custbroker.getBroker());
            execution.setVariable("sendEmail", custbroker.getSendEmail());
            execution.setVariable("sendSms", custbroker.getSendSms());
            execution.setVariable("sendOnline", custbroker.getSendOnline());
            execution.setVariable("mail", custbroker.getMail());
            execution.setVariable("returnLink", custbroker.getReturnLink());
        }

    }

}
