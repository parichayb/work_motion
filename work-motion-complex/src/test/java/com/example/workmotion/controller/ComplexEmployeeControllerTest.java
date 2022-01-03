package com.example.workmotion.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.example.workmotion.AbstractWorkMotionTest;
import com.example.workmotion.domain.Employee;
import com.example.workmotion.domain.EmployeeEvent;
import com.example.workmotion.domain.EmployeeState;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ComplexEmployeeControllerTest extends AbstractWorkMotionTest {

  @Test
  public void employeeFlowTest_start_to_end_successful() throws JsonProcessingException, Exception {

    // GIVEN
    String uri = "/complex/employee";
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

    // get
    mvcResult = getEmployee(uri, receivedEmployee.getId());

    status = mvcResult.getResponse().getStatus();
    assertEquals(HttpStatus.OK.value(), status);
    content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    receivedEmployee = mapFromJson(content, Employee.class);
    assertNotNull(receivedEmployee.getId());
    assertEquals(EmployeeState.ADDED, receivedEmployee.getState());

    // state change - added to in check - security started and permit started
    receivedEmployee.setTransition(EmployeeEvent.CHECK.name());
    mvcResult = putEmployeeTransition(uri, receivedEmployee.getId(), receivedEmployee);

    // get
    mvcResult = getEmployee(uri, receivedEmployee.getId());

    status = mvcResult.getResponse().getStatus();
    assertEquals(HttpStatus.OK.value(), status);
    content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    receivedEmployee = mapFromJson(content, Employee.class);
    assertNotNull(receivedEmployee.getId());
    assertEquals(EmployeeState.CHECK_START, receivedEmployee.getState());

    // state change - in check to check done partially - security done,
    // permit pending
    receivedEmployee.setTransition(EmployeeEvent.SECURITY_CHECK_DONE.name());
    mvcResult = putEmployeeTransition(uri, receivedEmployee.getId(), receivedEmployee);

    // get
    mvcResult = getEmployee(uri, receivedEmployee.getId());

    status = mvcResult.getResponse().getStatus();
    assertEquals(HttpStatus.OK.value(), status);
    content = mvcResult.getResponse().getContentAsString();
    receivedEmployee = mapFromJson(content, Employee.class);
    assertEquals(EmployeeState.IN_CHECK, receivedEmployee.getState());

    // state change - check in done fully - security done, permit done
    receivedEmployee.setTransition(EmployeeEvent.PERMIT_DONE.name());
    mvcResult = putEmployeeTransition(uri, receivedEmployee.getId(), receivedEmployee);

    // get
    mvcResult = getEmployee(uri, receivedEmployee.getId());

    status = mvcResult.getResponse().getStatus();
    assertEquals(HttpStatus.OK.value(), status);
    content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    receivedEmployee = mapFromJson(content, Employee.class);
    assertNotNull(receivedEmployee.getId());
    assertEquals(EmployeeState.APPROVED, receivedEmployee.getState());

    // state change - approve to active
    receivedEmployee.setTransition(EmployeeEvent.ACTIVATE.name());
    mvcResult = putEmployeeTransition(uri, receivedEmployee.getId(), receivedEmployee);

    // get
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

  @Test
  public void createEmployeeNew() throws Exception {
    // GIVEN
    String uri = "/complex/employee";
    Employee employee = Employee.builder().firstName("firstname").lastName("latname").age(20).phone(123456789).build();

    // WHEN
    MvcResult mvcResult = postEmployee(uri, employee);

    // THEN
    int status = mvcResult.getResponse().getStatus();
    assertEquals(HttpStatus.OK.value(), status);
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    Employee receivedEmployee = mapFromJson(content, Employee.class);
    assertNotNull(receivedEmployee.getId());
    assertEquals(EmployeeState.ADDED, receivedEmployee.getState());
  }

  @Test
  public void getEmployeeDetails() throws Exception {
    // GIVEN
    String uri = "/complex/employee";
    Employee employee = Employee.builder().firstName("firstname").lastName("lastname").age(20).phone(123456789).build();
    postEmployee(uri, employee);

    // WHEN
    MvcResult mvcResult = getEmployee(uri, 1L);

    // THEN

    int status = mvcResult.getResponse().getStatus();
    assertEquals(HttpStatus.OK.value(), status);
    String content = mvcResult.getResponse().getContentAsString();
    Employee receivedEmployee = mapFromJson(content, Employee.class);

    assertNotNull(receivedEmployee.getId());
    assertEquals(EmployeeState.ADDED, receivedEmployee.getState());
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
