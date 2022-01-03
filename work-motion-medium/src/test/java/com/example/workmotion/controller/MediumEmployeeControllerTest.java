package com.example.workmotion.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.workmotion.AbstractWorkMotionTest;
import com.example.workmotion.domain.Employee;
import com.example.workmotion.domain.EmployeeEvent;
import com.example.workmotion.domain.EmployeeState;
import com.fasterxml.jackson.core.JsonProcessingException;

public class MediumEmployeeControllerTest extends AbstractWorkMotionTest {

  @Test
  public void createEmployee() throws Exception {
    MvcResult mvcResult = postEmployee();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(HttpStatus.OK.value(), status);
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    Employee receivedEmployee = mapFromJson(content, Employee.class);
    assertNotNull(receivedEmployee.getId());
    assertEquals(EmployeeState.ADDED, receivedEmployee.getState());
  }

  private MvcResult postEmployee() throws JsonProcessingException, Exception {
    String uri = "/medium/employee";
    Employee employee = Employee.builder().firstName("firstname").lastName("latname").age(20).phone(123456789).build();

    String inputJson = super.mapToJson(employee);
    MvcResult mvcResult = mvc
        .perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
        .andReturn();
    return mvcResult;
  }

  @Test
  public void getEmployee() throws Exception {
    postEmployee();
    String uri = "/medium/employee";

    MvcResult mvcResult = mvc
        .perform(MockMvcRequestBuilders.get(uri + "//1").contentType(MediaType.APPLICATION_JSON_VALUE))
        .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(HttpStatus.OK.value(), status);
    String content = mvcResult.getResponse().getContentAsString();
    Employee receivedEmployee = mapFromJson(content, Employee.class);

    assertNotNull(receivedEmployee.getId());
    assertEquals(EmployeeState.ADDED, receivedEmployee.getState());
  }
  
  @Test
  public void employeeFlowTest_start_to_end_successful() throws JsonProcessingException, Exception {

    // GIVEN
    String uri = "/medium/employee";
    Employee employee = Employee.builder().firstName("firstname").lastName("latname").age(20).phone(123456789).build();

    // WHEN

    // create
    MvcResult mvcResult = postEmployee(uri, employee);

    // THEN
    int status = mvcResult.getResponse().getStatus();
    assertEquals(HttpStatus.OK.value(), status);
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    Employee receivedEmployee = mapFromJson(content, Employee.class);
    assertNotNull(receivedEmployee.getId());
    assertEquals(EmployeeState.ADDED, receivedEmployee.getState());

    // THEN get
    mvcResult = getEmployee(uri, receivedEmployee.getId());

    status = mvcResult.getResponse().getStatus();
    assertEquals(HttpStatus.OK.value(), status);
    content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    receivedEmployee = mapFromJson(content, Employee.class);
    assertNotNull(receivedEmployee.getId());
    assertEquals(EmployeeState.ADDED, receivedEmployee.getState());

    // WHEN state change - CHECK
    receivedEmployee.setTransition(EmployeeEvent.CHECK.name());
    mvcResult = putEmployeeTransition(uri, receivedEmployee.getId(), receivedEmployee);

    // THEN get
    mvcResult = getEmployee(uri, receivedEmployee.getId());

    status = mvcResult.getResponse().getStatus();
    assertEquals(HttpStatus.OK.value(), status);
    content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    receivedEmployee = mapFromJson(content, Employee.class);
    assertNotNull(receivedEmployee.getId());
    assertEquals(EmployeeState.IN_CHECK, receivedEmployee.getState());

    // WHEN state change - APPROVE
    receivedEmployee.setTransition(EmployeeEvent.APPROVE.name());
    mvcResult = putEmployeeTransition(uri, receivedEmployee.getId(), receivedEmployee);

    // THEN get
    mvcResult = getEmployee(uri, receivedEmployee.getId());

    status = mvcResult.getResponse().getStatus();
    assertEquals(HttpStatus.OK.value(), status);
    content = mvcResult.getResponse().getContentAsString();
    receivedEmployee = mapFromJson(content, Employee.class);
    assertEquals(EmployeeState.APPROVED, receivedEmployee.getState());


    // WHEN state change - ACTIVATE
    receivedEmployee.setTransition(EmployeeEvent.ACTIVATE.name());
    mvcResult = putEmployeeTransition(uri, receivedEmployee.getId(), receivedEmployee);

    // THEN get
    mvcResult = getEmployee(uri, receivedEmployee.getId());

    status = mvcResult.getResponse().getStatus();
    assertEquals(HttpStatus.OK.value(), status);
    content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    receivedEmployee = mapFromJson(content, Employee.class);
    assertNotNull(receivedEmployee.getId());
    assertEquals(EmployeeState.ACTIVE, receivedEmployee.getState());

    // first to end - successful, happy path done. :)

  }
  
  private MvcResult postEmployee(String uri, Employee employee) throws JsonProcessingException, Exception {
    String inputJson = super.mapToJson(employee);
    MvcResult mvcResult = mvc
        .perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
        .andReturn();
    return mvcResult;
  }
  
  private MvcResult getEmployee(String uri, Long id) throws Exception {
    MvcResult mvcResult = mvc
        .perform(MockMvcRequestBuilders.get(uri + "//" + id).contentType(MediaType.APPLICATION_JSON_VALUE))
        .andReturn();
    return mvcResult;
  }

  private MvcResult putEmployeeTransition(String uri, Long id, Employee employee) throws Exception {
    String inputJson = super.mapToJson(employee);
    MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri + "//" + id + "//transition").contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(inputJson)).andReturn();
    return mvcResult;
  }

}
