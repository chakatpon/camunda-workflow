package com.x10.viriyah;

import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.ExternalResponse;
import com.x10.viriyah.services.business.SendPdfWithSmsService;
import com.x10.viriyah.services.business.SendSuccessEmailService;
import com.x10.viriyah.services.impl.EmailService;
import com.x10.viriyah.services.impl.ExternalService;
import com.x10.viriyah.utils.LogUtil;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.impl.pvm.runtime.ExecutionImpl;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SendSuccessEmailTest {

    private final LogUtil logUtil = mock(LogUtil.class);
    private SendSuccessEmailService sendSuccessEmailService;
    private final EmailService emailService = mock(EmailService.class);

    @Before
    public void setup() {
        sendSuccessEmailService = new SendSuccessEmailService();
        sendSuccessEmailService.setLogUtil(logUtil);
        sendSuccessEmailService.setMailService(emailService);
        ReflectionTestUtils.setField(sendSuccessEmailService, "sendMailTo", "http://foo");
    }

    @After
    public void tearDown() {
        Mocks.reset();
    }

    @Test
    public void testSendSuccessEmailWithFlagNAlwayPass() {
        ReflectionTestUtils.setField(sendSuccessEmailService, "sendMailSuccessFlag", "N");
        DelegateExecution execution = new ExecutionImpl();
        sendSuccessEmailService.execute(execution);
    }

    @Test
    public void testSendSuccessEmailWithFlagYAlwayPass() {
        ReflectionTestUtils.setField(sendSuccessEmailService, "sendMailSuccessFlag", "Y");
        DelegateExecution execution = new ExecutionImpl();
        sendSuccessEmailService.execute(execution);
    }

}
