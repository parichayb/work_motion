package com.example.workmotion.service;

import org.springframework.statemachine.StateMachine;

import com.example.workmotion.domain.Employee;
import com.example.workmotion.domain.EmployeeEvent;
import com.example.workmotion.domain.EmployeeState;

public interface EmployeeService {

  public Employee addNewEmployee(Employee employee);
  
  public StateMachine<EmployeeState, EmployeeEvent> transitionForEmployee(Long id, EmployeeEvent event);

}
