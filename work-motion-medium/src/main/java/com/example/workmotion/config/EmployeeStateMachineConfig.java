package com.example.workmotion.config;

import java.util.EnumSet;

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
    states.withStates()
    .initial(EmployeeState.ADDED)
    .states(EnumSet.allOf(EmployeeState.class))
    .end(EmployeeState.ACTIVE);

  }
  
  @Override
  public void configure(StateMachineTransitionConfigurer<EmployeeState, EmployeeEvent> transitions) throws Exception {
    
    
    transitions
    .withExternal().source(EmployeeState.ADDED).target(EmployeeState.ADDED).event(EmployeeEvent.ADD)
    .and()
    .withExternal().source(EmployeeState.ADDED).target(EmployeeState.IN_CHECK).event(EmployeeEvent.CHECK)
    .and()
    .withExternal().source(EmployeeState.IN_CHECK).target(EmployeeState.APPROVED).event(EmployeeEvent.APPROVE)
    .and()
    .withExternal().source(EmployeeState.APPROVED).target(EmployeeState.ACTIVE).event(EmployeeEvent.ACTIVATE);
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
