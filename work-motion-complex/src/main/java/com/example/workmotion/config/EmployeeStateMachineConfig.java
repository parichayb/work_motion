package com.example.workmotion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import com.example.workmotion.domain.EmployeeEvent;
import com.example.workmotion.domain.EmployeeState;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableStateMachineFactory
@Slf4j
public class EmployeeStateMachineConfig extends StateMachineConfigurerAdapter<EmployeeState, EmployeeEvent> {
  

  @Override
  public void configure(StateMachineStateConfigurer<EmployeeState, EmployeeEvent> states) throws Exception {
    states
     .withStates()
      .initial(EmployeeState.ADDED)
      .fork(EmployeeState.CHECK_START)
      .join(EmployeeState.CHECK_END)
      .state(EmployeeState.APPROVED)
      .state(EmployeeState.ACTIVE)
      .end(EmployeeState.ACTIVE)
     .and().withStates()
      .parent(EmployeeState.CHECK_START)
      .initial(EmployeeState.SECURITY_CHECK_STARTED)
      .end(EmployeeState.SECURITY_CHECK_FINISHED)
     .and().withStates()
      .parent(EmployeeState.CHECK_START)
      .initial(EmployeeState.PERMIT_STARTED)
      .end(EmployeeState.PERMIT_FINISHED)
      ;

  }
  
  @Override
  public void configure(StateMachineTransitionConfigurer<EmployeeState, EmployeeEvent> transitions) throws Exception {
    
    transitions.withExternal()
      .source(EmployeeState.ADDED).target(EmployeeState.ADDED).event(EmployeeEvent.ADD)
    .and().withExternal()
      .source(EmployeeState.ADDED).target(EmployeeState.CHECK_START).event(EmployeeEvent.CHECK)
    .and().withExternal()
      .source(EmployeeState.SECURITY_CHECK_STARTED).target(EmployeeState.SECURITY_CHECK_FINISHED).event(EmployeeEvent.SECURITY_CHECK_DONE)
    .and().withExternal()
      .source(EmployeeState.PERMIT_STARTED).target(EmployeeState.PERMIT_FINISHED).event(EmployeeEvent.PERMIT_DONE)
    .and().withFork()
      .source(EmployeeState.CHECK_START).target(EmployeeState.SECURITY_CHECK_STARTED).target(EmployeeState.PERMIT_STARTED)
    .and().withJoin()
      .source(EmployeeState.SECURITY_CHECK_FINISHED).source(EmployeeState.PERMIT_FINISHED).target(EmployeeState.CHECK_END)
    .and().withExternal()
      .source(EmployeeState.CHECK_END).target(EmployeeState.APPROVED)
    .and().withExternal()
      .source(EmployeeState.APPROVED).target(EmployeeState.ACTIVE).event(EmployeeEvent.ACTIVATE)
      
      ;

    
  }
  
  @Override
  public void configure(StateMachineConfigurationConfigurer<EmployeeState, EmployeeEvent> config) throws Exception {
    StateMachineListenerAdapter<EmployeeState, EmployeeEvent> adapter = new StateMachineListenerAdapter<EmployeeState, EmployeeEvent>(){
      @Override
      public void stateChanged(State<EmployeeState, EmployeeEvent> from, State<EmployeeState, EmployeeEvent> to) {
        log.info(String.format("Employee state has changed from: %s to: %s ", from, to));
      }
    };
    
    config.withConfiguration().listener(adapter);
  }
  

}
