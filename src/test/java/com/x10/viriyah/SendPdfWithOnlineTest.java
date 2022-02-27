package com.x10.viriyah;

import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.ExternalResponse;
import com.x10.viriyah.models.OnlineBody;
import com.x10.viriyah.services.business.SendPdfWithOnlineService;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SendPdfWithOnlineTest {

    private final LogUtil logUtil = mock(LogUtil.class);
    private final ExternalService externalService = spy(ExternalService.class);
    private SendPdfWithOnlineService sendPdfWithOnlineService;

    @Before
    public void setup() {
        sendPdfWithOnlineService = new SendPdfWithOnlineService();
        sendPdfWithOnlineService.setLogUtil(logUtil);
        sendPdfWithOnlineService.setExternalService(externalService);
        ReflectionTestUtils.setField(sendPdfWithOnlineService, "sendOnlineUrl", "http://foo");
        ReflectionTestUtils.setField(sendPdfWithOnlineService, "fixedBodySendOnline", "N");
    }

    @After
    public void tearDown() {
        Mocks.reset();
    }

    @Test
    public void testGenerateEntityWithFixBodyEntityShouldBeHaveFixValue() {
        ExternalRequest request = mock(ExternalRequest.class);
        HttpEntity<Object> requestEntity = sendPdfWithOnlineService.generateRequestEntity(request, "Y");
        assertTrue(requestEntity.getBody() instanceof OnlineBody);
        assertEquals("64100/POL/000000-000", ((OnlineBody) requestEntity.getBody()).getRefDocId());
    }

    @Test
    public void testResponseNullShouldBeSendPdfError() {
        DelegateExecution execution = new ExecutionImpl();
        when(externalService.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class))).thenReturn(ResponseEntity.ok().build());
        sendPdfWithOnlineService.execute(execution);
        assertEquals(true, execution.getVariable("sendPdfError"));
    }

    @Test
    public void testResponseStatusNShouldBeBpmnError() {
        DelegateExecution execution = new ExecutionImpl();
        ExternalResponse externalResponse = new ExternalResponse();
        externalResponse.setStatus("N");
        when(externalService.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class))).thenReturn(ResponseEntity.ok(externalResponse));
        sendPdfWithOnlineService.execute(execution);
        assertEquals(true, execution.getVariable("sendPdfError"));
    }

    @Test
    public void testResponseStatusYShouldBePass() {
        DelegateExecution execution = new ExecutionImpl();
        ExternalResponse externalResponse = new ExternalResponse();
        externalResponse.setStatus("Y");
        when(externalService.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class))).thenReturn(ResponseEntity.ok(externalResponse));
        sendPdfWithOnlineService.execute(execution);
        assertNull(execution.getVariable("sendPdfError"));
    }

}
