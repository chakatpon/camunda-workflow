package com.x10.viriyah;

import org.camunda.bpm.engine.runtime.JobQuery;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@RunWith(MockitoJUnitRunner.class)
@Deployment(resources = {"policy_delivery.bpmn", "customer_broker.dmn"})
public class ProcessTest {

    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Rule
    @ClassRule
    public static ProcessEngineRule engine = TestCoverageProcessEngineRuleBuilder.create().build();

    @Before
    public void setup() {
    }

    @After
    public void tearDown() {
        Mocks.reset();
    }

    @Test
    public void testHappyPath() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("class", "13");
        variables.put("subClass", "15");
        variables.put("partner", "TEST");
        variables.put("sendPdfError", null);
        // Start process with Java API and variables
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("Policy_Delivery", variables);
        assertThat(processInstance).calledProcessInstance();
        assertThat(processInstance).isWaitingAt("Activity_0ixsgr2");
        execute(job());
        assertThat(processInstance).isWaitingAt("Gateway_AsyncBeforeDelivery");
        execute(job(jobQuery().activityId("Gateway_AsyncBeforeDelivery")));
        assertThat(processInstance).isEnded();
    }

    @Test
    public void testErrorPath() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("class", "13");
        variables.put("subClass", "15");
        variables.put("partner", "TEST");
        variables.put("sendPdfError", true);
        // Start process with Java API and variables
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("Policy_Delivery", variables);
        assertThat(processInstance).calledProcessInstance();
        assertThat(processInstance).isWaitingAt("Activity_0ixsgr2");
        execute(job());
        assertThat(processInstance).isWaitingAt("Gateway_AsyncBeforeDelivery");
        execute(job(jobQuery().activityId("Gateway_AsyncBeforeDelivery")));
        assertThat(processInstance).isEnded();
    }

    @Test
    public void testTimeOutLog() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("class", "13");
        variables.put("subClass", "15");
        variables.put("partner", "TEST");
        variables.put("sendPdfError", null);
        // Start process with Java API and variables
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("Policy_Delivery", variables);
        assertThat(processInstance).calledProcessInstance();
        assertThat(processInstance).isWaitingAt("Activity_0ixsgr2");
        execute(job());
        assertThat(processInstance).isWaitingAt("Gateway_AsyncBeforeDelivery");
        execute(job(jobQuery().activityId("Event_Settimeout")));
        assertThat(processInstance).calledProcessInstance("Activity_Timeout");
        assertThat(processInstance).isEnded();
    }

}
