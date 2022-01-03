package com.example.workmotion.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

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
  public void testStateMachineConfig() {

    StateMachine<EmployeeState, EmployeeEvent> stateMachine = stateMachineFactory.getStateMachine(UUID.randomUUID());

    stateMachine.start();

    assertEquals(EmployeeState.ADDED, stateMachine.getState().getId());

    stateMachine.sendEvent(EmployeeEvent.ADD);

    assertEquals(EmployeeState.ADDED, stateMachine.getState().getId());

    stateMachine.sendEvent(EmployeeEvent.CHECK);

    assertEquals(EmployeeState.IN_CHECK, stateMachine.getState().getId());

    stateMachine.sendEvent(EmployeeEvent.APPROVE);
    
    assertEquals(EmployeeState.APPROVED, stateMachine.getState().getId());
    
    stateMachine.sendEvent(EmployeeEvent.ACTIVATE);

    assertEquals(EmployeeState.ACTIVE, stateMachine.getState().getId());

  }
  
}
