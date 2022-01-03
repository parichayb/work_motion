package com.example.workmotion.service;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;

import com.example.workmotion.domain.Employee;
import com.example.workmotion.domain.EmployeeEvent;
import com.example.workmotion.domain.EmployeeState;

public interface EmployeeService {

  public Employee addNewEmployee(Employee employee) throws Exception;
  
  public StateMachine<EmployeeState, EmployeeEvent> transitionForEmployee(Long id, EmployeeEvent event)  throws Exception;

  public void approveEmployee(StateContext<EmployeeState, EmployeeEvent> ctx)  throws Exception;

}
