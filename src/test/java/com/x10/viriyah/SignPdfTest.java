package com.x10.viriyah;

import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.SignPDFResponse;
import com.x10.viriyah.services.business.SignPdfService;
import com.x10.viriyah.services.impl.ExternalService;
import com.x10.viriyah.utils.LogUtil;
import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SignPdfTest {

    private final LogUtil logUtil = mock(LogUtil.class);
    private final ExternalService externalService = spy(ExternalService.class);
    private SignPdfService signPdfService;

    @Before
    public void setup() {
        signPdfService = new SignPdfService();
        signPdfService.setLogUtil(logUtil);
        signPdfService.setExternalService(externalService);
        ReflectionTestUtils.setField(signPdfService, "protectPdfUrl", "http://foo");
    }

    @After
    public void tearDown() {
        Mocks.reset();
    }

    @Test(expected = BpmnError.class)
    public void testResponseStatusNShouldBeBpmnError() {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable("broker")).thenReturn(false);
        SignPDFResponse externalResponse = new SignPDFResponse();
        externalResponse.setStatus("N");
        when(externalService.postRequest(anyString(), any(ExternalRequest.class), any(Class.class))).thenReturn(ResponseEntity.ok(externalResponse));
        signPdfService.execute(execution);
    }

    @Test
    public void testResponseStatusYShouldBePass() {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable("broker")).thenReturn(true);
        SignPDFResponse externalResponse = new SignPDFResponse();
        externalResponse.setStatus("Y");
        when(externalService.postRequest(anyString(), any(ExternalRequest.class), any(Class.class))).thenReturn(ResponseEntity.ok(externalResponse));
        signPdfService.execute(execution);
    }

}
