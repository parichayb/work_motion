package com.example.workmotion.service;

import java.util.HashMap;

import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.stereotype.Component;

import com.example.workmotion.domain.EmployeeEvent;
import com.example.workmotion.domain.EmployeeState;

@Component
public class InMemoryStateMachinePersist implements StateMachinePersist<EmployeeState, EmployeeEvent, String> {

  private final HashMap<String, StateMachineContext<EmployeeState, EmployeeEvent>> contexts = new HashMap<>();

  @Override
  public void write(StateMachineContext<EmployeeState, EmployeeEvent> context, String contextObj) throws Exception {
    contexts.put(contextObj, context);
  }

  @Override
  public StateMachineContext<EmployeeState, EmployeeEvent> read(String contextObj) throws Exception {
    return contexts.get(contextObj);
  }
}