package com.example.workmotion.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import com.example.workmotion.domain.EmployeeEvent;
import com.example.workmotion.domain.EmployeeState;

@SpringBootTest
public class StateMachineConfigTest {

  @Autowired
  private StateMachineFactory<EmployeeState, EmployeeEvent> stateMachineFactory;

  @Test
  public void testStateMachineConfig_firstSecurity_thenPermit() {

    StateMachine<EmployeeState, EmployeeEvent> stateMachine = stateMachineFactory.getStateMachine(UUID.randomUUID());

    stateMachine.start();

    assertEquals(EmployeeState.ADDED, stateMachine.getState().getId());

    stateMachine.sendEvent(EmployeeEvent.ADD);

    assertEquals(EmployeeState.ADDED, stateMachine.getState().getId());

    stateMachine.sendEvent(EmployeeEvent.CHECK);

    assertTrue(CollectionUtils.isEqualCollection(Arrays.asList(EmployeeState.CHECK_START, 
        EmployeeState.SECURITY_CHECK_STARTED, EmployeeState.PERMIT_STARTED), stateMachine.getState().getIds()));


    stateMachine.sendEvent(EmployeeEvent.SECURITY_CHECK_DONE);
    
    assertTrue(CollectionUtils.isEqualCollection(Arrays.asList(EmployeeState.CHECK_START, 
        EmployeeState.SECURITY_CHECK_FINISHED, EmployeeState.PERMIT_STARTED), stateMachine.getState().getIds()));

    stateMachine.sendEvent(EmployeeEvent.PERMIT_DONE);

    assertEquals(EmployeeState.APPROVED, stateMachine.getState().getId());

    stateMachine.sendEvent(EmployeeEvent.ACTIVATE);

    assertEquals(EmployeeState.ACTIVE, stateMachine.getState().getId());

  }
  
  @Test
  public void testStateMachineConfig_firstPermit_thenSecurity() {

    StateMachine<EmployeeState, EmployeeEvent> stateMachine = stateMachineFactory.getStateMachine(UUID.randomUUID());

    stateMachine.start();

    assertEquals(EmployeeState.ADDED, stateMachine.getState().getId());

    stateMachine.sendEvent(EmployeeEvent.ADD);

    assertEquals(EmployeeState.ADDED, stateMachine.getState().getId());

    stateMachine.sendEvent(EmployeeEvent.CHECK);

    assertTrue(CollectionUtils.isEqualCollection(Arrays.asList(EmployeeState.CHECK_START, 
        EmployeeState.SECURITY_CHECK_STARTED, EmployeeState.PERMIT_STARTED), stateMachine.getState().getIds()));

    stateMachine.sendEvent(EmployeeEvent.PERMIT_DONE);
    
    assertTrue(CollectionUtils.isEqualCollection(Arrays.asList(EmployeeState.CHECK_START, 
        EmployeeState.SECURITY_CHECK_STARTED, EmployeeState.PERMIT_FINISHED), stateMachine.getState().getIds()));
    
    stateMachine.sendEvent(EmployeeEvent.SECURITY_CHECK_DONE);

    assertEquals(EmployeeState.APPROVED, stateMachine.getState().getId());

    stateMachine.sendEvent(EmployeeEvent.ACTIVATE);

    assertEquals(EmployeeState.ACTIVE, stateMachine.getState().getId());

  }

}
