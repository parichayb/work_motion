package com.example.workmotion.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
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
  
  @Autowired
  InMemoryStateMachinePersist stateMachinePersist;
  
  public static final String HEADER_NAME = "employee-id";
  
  
  @Override
  @Transactional
  public StateContext<EmployeeState, EmployeeEvent> postTransition(
      StateContext<EmployeeState, EmployeeEvent> stateContext) {
    
    StateMachine<EmployeeState, EmployeeEvent> stateMachine = stateContext.getStateMachine();
    List<EmployeeState> states = stateMachine.getState().getIds().stream().collect(Collectors.toList());
    Message<EmployeeEvent> message = stateContext.getMessage();
    
    Optional.ofNullable(message ).ifPresent(msg -> {
      Optional.ofNullable(Long.class.cast(message.getHeaders().getOrDefault(HEADER_NAME, 1L))).ifPresent(employeeId -> {
        Employee employee = employeeRepository.getById(employeeId);
        employee.setState(states);
        employeeRepository.save(employee);
        
        StateMachinePersister<EmployeeState, EmployeeEvent, String> persister = new DefaultStateMachinePersister<>(stateMachinePersist);
        try {
          persister.persist(stateMachine, Long.toString(employeeId));
        } catch (Exception e) {
          e.printStackTrace();
        }
        
      });
    });
    return super.postTransition(stateContext);
  }
  
  

}
