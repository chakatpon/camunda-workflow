package com.x10.viriyah;

import com.x10.viriyah.services.business.SummaryLogService;
import com.x10.viriyah.services.impl.EmailService;
import com.x10.viriyah.services.impl.WorkflowLogService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.impl.pvm.runtime.ExecutionImpl;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class SummaryLogTest {

    private final WorkflowLogService workflowLogService = mock(WorkflowLogService.class);
    private SummaryLogService summaryLogService;
    private final EmailService emailService = mock(EmailService.class);

    @Before
    public void setup() {
        summaryLogService = new SummaryLogService();
        summaryLogService.setWorkflowLogService(workflowLogService);
        summaryLogService.setMailService(emailService);
        ReflectionTestUtils.setField(summaryLogService, "sendMailTo", "http://foo");
    }

    @After
    public void tearDown() {
        Mocks.reset();
    }

    @Test
    public void testSendSuccessEmailWithFlagNAlwayPass() {
        ReflectionTestUtils.setField(summaryLogService, "sendMailFailedFlag", "N");
        DelegateExecution execution = new ExecutionImpl();
        summaryLogService.execute(execution);
    }

    @Test
    public void testSendSuccessEmailWithFlagYAlwayPass() {
        ReflectionTestUtils.setField(summaryLogService, "sendMailFailedFlag", "Y");
        DelegateExecution execution = new ExecutionImpl();
        summaryLogService.execute(execution);
    }

}
