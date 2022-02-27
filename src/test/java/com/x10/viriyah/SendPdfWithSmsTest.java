package com.x10.viriyah;

import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.ExternalResponse;
import com.x10.viriyah.services.business.SendPdfWithSmsService;
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
public class SendPdfWithSmsTest {

    private final LogUtil logUtil = mock(LogUtil.class);
    private final ExternalService externalService = spy(ExternalService.class);
    private SendPdfWithSmsService sendPdfWithSmsService;

    @Before
    public void setup() {
        sendPdfWithSmsService = new SendPdfWithSmsService();
        sendPdfWithSmsService.setLogUtil(logUtil);
        sendPdfWithSmsService.setExternalService(externalService);
        ReflectionTestUtils.setField(sendPdfWithSmsService, "sendSmsUrl", "http://foo");
    }

    @After
    public void tearDown() {
        Mocks.reset();
    }

    @Test
    public void testResponseNullShouldBeSendPdfError() {
        DelegateExecution execution = new ExecutionImpl();
        when(externalService.postRequest(anyString(), any(ExternalRequest.class))).thenReturn(ResponseEntity.ok().build());
        sendPdfWithSmsService.execute(execution);
        assertEquals(true, execution.getVariable("sendPdfError"));
    }

    @Test
    public void testResponseStatusNShouldBeBpmnError() {
        DelegateExecution execution = new ExecutionImpl();
        ExternalResponse externalResponse = new ExternalResponse();
        externalResponse.setStatus("N");
        when(externalService.postRequest(anyString(), any(ExternalRequest.class))).thenReturn(ResponseEntity.ok(externalResponse));
        sendPdfWithSmsService.execute(execution);
        assertEquals(true, execution.getVariable("sendPdfError"));
    }

    @Test
    public void testResponseStatusYShouldBePass() {
        DelegateExecution execution = new ExecutionImpl();
        ExternalResponse externalResponse = new ExternalResponse();
        externalResponse.setStatus("Y");
        when(externalService.postRequest(anyString(), any(ExternalRequest.class))).thenReturn(ResponseEntity.ok(externalResponse));
        sendPdfWithSmsService.execute(execution);
        assertNull(execution.getVariable("sendPdfError"));
    }

}
