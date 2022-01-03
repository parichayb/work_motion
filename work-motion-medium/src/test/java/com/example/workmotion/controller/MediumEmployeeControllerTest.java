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
        .perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON_VALUE).pathInfo("//1"))
        .andReturn();

    int status = mvcResult.getResponse().getStatus();
    assertEquals(HttpStatus.OK.value(), status);
    String content = mvcResult.getResponse().getContentAsString();
    Employee receivedEmployee = mapFromJson(content, Employee.class);

    assertNotNull(receivedEmployee.getId());
    assertEquals(EmployeeState.ADDED, receivedEmployee.getState());
  }

}
