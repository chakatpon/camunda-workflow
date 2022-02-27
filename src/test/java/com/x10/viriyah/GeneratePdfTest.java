package com.x10.viriyah;

import com.x10.viriyah.models.ExternalRequest;
import com.x10.viriyah.models.ExternalResponse;
import com.x10.viriyah.services.business.GeneratePdfService;
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
public class GeneratePdfTest {

    private final LogUtil logUtil = mock(LogUtil.class);
    private final ExternalService externalService = spy(ExternalService.class);
    private GeneratePdfService generatePdfService;

    @Before
    public void setup() {
        generatePdfService = new GeneratePdfService();
        generatePdfService.setLogUtil(logUtil);
        generatePdfService.setExternalService(externalService);
        ReflectionTestUtils.setField(generatePdfService, "generatePdfUrl", "http://foo");
    }

    @After
    public void tearDown() {
        Mocks.reset();
    }

    @Test(expected = BpmnError.class)
    public void testBrokerIsNullShouldBeBpmnError() {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable("broker")).thenReturn(null);
        generatePdfService.execute(execution);
    }

    @Test(expected = BpmnError.class)
    public void testResponseNullShouldBeBpmnError() {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable("broker")).thenReturn("test");
        generatePdfService.execute(execution);
    }

    @Test(expected = BpmnError.class)
    public void testResponseStatusNShouldBeBpmnError() {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable("broker")).thenReturn("test");
        ExternalResponse externalResponse = new ExternalResponse();
        externalResponse.setStatus("N");
        when(externalService.postRequest(anyString(), any(ExternalRequest.class))).thenReturn(ResponseEntity.ok(externalResponse));
        generatePdfService.execute(execution);
    }

    @Test
    public void testResponseStatusYShouldBePass() {
        DelegateExecution execution = mock(DelegateExecution.class);
        when(execution.getVariable("broker")).thenReturn("test");
        ExternalResponse externalResponse = new ExternalResponse();
        externalResponse.setStatus("Y");
        when(externalService.postRequest(anyString(), any(ExternalRequest.class))).thenReturn(ResponseEntity.ok(externalResponse));
        generatePdfService.execute(execution);
    }

}
