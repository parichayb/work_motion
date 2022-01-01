package com.example.workmotion.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Affordance;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.workmotion.domain.Employee;
import com.example.workmotion.domain.EmployeeEvent;
import com.example.workmotion.domain.EmployeeState;
import com.example.workmotion.repository.EmployeeRepository;

@RestController
@RequestMapping(value = "/simple")
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class SimpleEmployeeController {

  @Autowired
  EmployeeRepository employeeRepository;

  @PostMapping("/employee")
  ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
    employee.setState(EmployeeState.ADDED);
    employee.setNextEvent(EmployeeEvent.ADD.nextEvent());
    Employee savedEmployee = employeeRepository.save(employee);

    createLinks(savedEmployee);

    return new ResponseEntity<Employee>(savedEmployee, HttpStatus.OK);
  }

  @GetMapping(path = "/employee/{id}")
  ResponseEntity<Employee> getEmployee(@PathVariable Long id) {
    Employee savedEmployee = null;
    Optional<Employee> optionalEmp = employeeRepository.findById(id);
    if (optionalEmp.isPresent()) {
      savedEmployee = optionalEmp.get();
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee Not Found");
    }
    createLinks(savedEmployee);
    return new ResponseEntity<Employee>(savedEmployee, HttpStatus.OK);
  }

  @PatchMapping(path = "/employee/{id}/{nextEvent}")
  ResponseEntity<Employee> updateEmployeeState(@PathVariable Long id, @PathVariable String nextEvent) {
    Employee savedEmployee = employeeRepository.findById(id).get();
    EmployeeEvent event = EmployeeEvent.valueOf(nextEvent);
    savedEmployee.setState(event.fetchState());
    savedEmployee.setNextEvent(event.nextEvent());

    employeeRepository.save(savedEmployee);

    createLinks(savedEmployee);

    return new ResponseEntity<Employee>(savedEmployee, HttpStatus.OK);

  }

  @PutMapping(path = "/employee/{id}")
  ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
    Employee savedEmployee = employeeRepository.findById(id).get();
    savedEmployee.setFirstName(employee.getFirstName());
    savedEmployee.setLastName(employee.getLastName());
    savedEmployee.setAge(employee.getAge());
    employeeRepository.save(savedEmployee);

    createLinks(savedEmployee);

    return new ResponseEntity<Employee>(savedEmployee, HttpStatus.OK);

  }

  private void createLinks(Employee savedEmployee) {
    Link link = linkTo(methodOn(SimpleEmployeeController.class).getEmployee(savedEmployee.getId())).withSelfRel();
    LinkRelation rel = new LinkRelation() {
      @Override
      public String value() {
        return "next-event";
      }
    };
    Link eventLink = linkTo(methodOn(SimpleEmployeeController.class).updateEmployeeState(savedEmployee.getId(),
        savedEmployee.getNextEvent().name())).withRel(rel).withName("Next Event").withType("HTTP Method : PATCH");
    savedEmployee.add(link);
    savedEmployee.add(eventLink);
  }

}
