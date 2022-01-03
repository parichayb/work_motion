package com.example.workmotion.service;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
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
  
  private final InMemoryStateMachinePersist stateMachinePersist;

  @Override
  @Transactional
  public Employee addNewEmployee(Employee employee) throws Exception {
    List<EmployeeState> state = new ArrayList<>();
    state.add(EmployeeState.ADDED);
    employee.setState(state);
    //employee.setState(EmployeeState.ADDED);
    
    Employee savedEmployee = employeeRepository.save(employee);
    StateMachine<EmployeeState, EmployeeEvent> sm = build(savedEmployee.getId());
    persistSM(sm,savedEmployee.getId());
    
    return savedEmployee;
  }

  @Override
  public StateMachine<EmployeeState, EmployeeEvent> transitionForEmployee(Long id, EmployeeEvent event) throws Exception {
    return updateEmplyeeState(id, event);
  }

  private StateMachine<EmployeeState, EmployeeEvent> updateEmplyeeState(Long id, EmployeeEvent event) throws Exception {
    StateMachine<EmployeeState, EmployeeEvent> sm = fetchSM(id);
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
  
  private void persistSM(StateMachine<EmployeeState, EmployeeEvent> stateMachine, Long id) throws Exception {
    //InMemoryStateMachinePersist stateMachinePersist = new InMemoryStateMachinePersist();
    StateMachinePersister<EmployeeState, EmployeeEvent, String> persister = new DefaultStateMachinePersister<>(stateMachinePersist);
    
    persister.persist(stateMachine, Long.toString(id));

  }
  
  private StateMachine<EmployeeState, EmployeeEvent> fetchSM(Long employeeId) throws Exception {
    StateMachine<EmployeeState, EmployeeEvent> sm = stateMachineFactory.getStateMachine(Long.toString(employeeId));

    //InMemoryStateMachinePersist stateMachinePersist = new InMemoryStateMachinePersist();
    StateMachinePersister<EmployeeState, EmployeeEvent, String> persister = new DefaultStateMachinePersister<>(stateMachinePersist);
    
    StateMachine<EmployeeState, EmployeeEvent> restoredSM = persister.restore(sm, Long.toString(employeeId));
    restoredSM.getStateMachineAccessor().doWithAllRegions(sma -> 
      sma.addStateMachineInterceptor(employeeStateChangeInterceptor));
      
    return restoredSM;
  }

  private StateMachine<EmployeeState, EmployeeEvent> build(Long employeeId) {
    Employee employee = employeeRepository.getById(employeeId);
    StateMachine<EmployeeState, EmployeeEvent> sm = stateMachineFactory.getStateMachine(Long.toString(employeeId));

    
    sm.stop();

    sm.getStateMachineAccessor().doWithAllRegions(sma -> {
      sma.addStateMachineInterceptor(employeeStateChangeInterceptor);
      sma.resetStateMachine(new DefaultStateMachineContext<>(employee.getState().stream().findFirst().get(), null, null, null));
    });

    sm.start();
    

    return sm;
  }

}
