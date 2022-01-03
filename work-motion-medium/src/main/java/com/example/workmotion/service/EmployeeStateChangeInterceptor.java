package com.example.workmotion.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import com.example.workmotion.domain.Employee;
import com.example.workmotion.domain.EmployeeEvent;
import com.example.workmotion.domain.EmployeeState;
import com.example.workmotion.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmployeeStateChangeInterceptor extends StateMachineInterceptorAdapter<EmployeeState, EmployeeEvent> {
  
  @Autowired
  EmployeeRepository employeeRepository;
  
  public static final String HEADER_NAME = "employee-id";
  
  @Override
  public void preStateChange(State<EmployeeState, EmployeeEvent> state, Message<EmployeeEvent> message,
      Transition<EmployeeState, EmployeeEvent> transition, StateMachine<EmployeeState, EmployeeEvent> stateMachine) {
    
    Long.class.cast(message.getHeaders().getOrDefault(HEADER_NAME, 1L));
    
    Optional.ofNullable(message).ifPresent(msg -> {
      Optional.ofNullable(Long.class.cast(message.getHeaders().getOrDefault(HEADER_NAME, 1L))).ifPresent(employeeId -> {
        Employee employee = employeeRepository.getById(employeeId);
        employee.setState(state.getId());
        employeeRepository.save(employee);
      });
    });
    
    
  }

}
