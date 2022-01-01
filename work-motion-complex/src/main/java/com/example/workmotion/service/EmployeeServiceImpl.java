package com.example.workmotion.service;

import javax.transaction.Transactional;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.statemachine.support.StateMachineInterceptor;
import org.springframework.stereotype.Service;

import com.example.workmotion.domain.Employee;
import com.example.workmotion.domain.EmployeeEvent;
import com.example.workmotion.domain.EmployeeState;
import com.example.workmotion.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

  public static final String HEADER_NAME = "employee-id";

  private final EmployeeRepository employeeRepository;

  private final StateMachineFactory<EmployeeState, EmployeeEvent> stateMachineFactory;

  private final StateMachineInterceptor<EmployeeState, EmployeeEvent> employeeStateChangeInterceptor;

  @Override
  @Transactional
  public Employee addNewEmployee(Employee employee) {
    employee.setState(EmployeeState.ADDED);
    return employeeRepository.save(employee);
  }

  @Override
  @Transactional
  public StateMachine<EmployeeState, EmployeeEvent> checkEmployee(Long id) {
    return updateEmplyeeState(id, EmployeeEvent.CHECK);
  }

  @Override
  @Transactional
  public StateMachine<EmployeeState, EmployeeEvent> approveEmployee(Long id) {
    return updateEmplyeeState(id, EmployeeEvent.APPROVE);

  }

  @Override
  @Transactional
  public StateMachine<EmployeeState, EmployeeEvent> activateEmployee(Long id) {
    return updateEmplyeeState(id, EmployeeEvent.ACTIVATE);

  }

  @Override
  public StateMachine<EmployeeState, EmployeeEvent> transitionForEmployee(Long id, EmployeeEvent event) {
    return updateEmplyeeState(id, event);
  }

  private StateMachine<EmployeeState, EmployeeEvent> updateEmplyeeState(Long id, EmployeeEvent event) {
    StateMachine<EmployeeState, EmployeeEvent> sm = build(id);
    sendEvent(id, sm, event);
    return sm;
  }

  public void approveEmployee(final StateContext<EmployeeState, EmployeeEvent> ctx) {

    log.info("Context = " + ctx);
  }

  private void sendEvent(Long id, StateMachine<EmployeeState, EmployeeEvent> sm, EmployeeEvent event) {
    Message<EmployeeEvent> msg = MessageBuilder.withPayload(event).setHeader(HEADER_NAME, id).build();
    sm.sendEvent(msg);
  }

  private StateMachine<EmployeeState, EmployeeEvent> build(Long employeeId) {
    Employee employee = employeeRepository.getById(employeeId);
    StateMachine<EmployeeState, EmployeeEvent> sm = stateMachineFactory.getStateMachine(Long.toString(employeeId));

    sm.stop();

    sm.getStateMachineAccessor().doWithAllRegions(sma -> {
      sma.addStateMachineInterceptor(employeeStateChangeInterceptor);
      sma.resetStateMachine(new DefaultStateMachineContext<>(employee.getState(), null, null, null));
    });

    sm.start();

    return sm;
  }

}
