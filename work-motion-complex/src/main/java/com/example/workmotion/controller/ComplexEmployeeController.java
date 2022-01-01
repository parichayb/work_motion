package com.example.workmotion.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.workmotion.domain.Employee;
import com.example.workmotion.domain.EmployeeEvent;
import com.example.workmotion.repository.EmployeeRepository;
import com.example.workmotion.service.EmployeeService;

@RestController
@RequestMapping(value = "/medium")
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class ComplexEmployeeController {

  @Autowired
  EmployeeRepository employeeRepository;
  @Autowired
  EmployeeService employeeService;

  
  @PostMapping("/employee")
  ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
    Employee newEmployee = employeeService.addNewEmployee(employee);
    createLinks(newEmployee);
    return new ResponseEntity<Employee>(newEmployee, HttpStatus.OK);
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

  @PutMapping(path = "/employee/{id}/transition")
  ResponseEntity<Employee> updateEmployeeState(@PathVariable Long id, @RequestBody Employee employee) {

    employeeService.transitionForEmployee(id, EmployeeEvent.valueOf(employee.getTransition()));

    Employee savedEmployee = employeeRepository.findById(id).get();

    createLinks(savedEmployee);

    return new ResponseEntity<Employee>(savedEmployee, HttpStatus.OK);

  }
  
  @PutMapping(path = "/employee/{id}/state-transition")
  ResponseEntity<Employee> updateEmployeeStateTransition(@PathVariable Long id, @RequestBody EmployeeEvent event) {

    employeeService.transitionForEmployee(id, event);

    Employee savedEmployee = employeeRepository.findById(id).get();

    createLinks(savedEmployee);

    return new ResponseEntity<Employee>(savedEmployee, HttpStatus.OK);

  }

  @PutMapping(path = "/employee/{id}")
  ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
    Employee savedEmployee = employeeRepository.findById(id).get();
    savedEmployee.setFirstName(employee.getFirstName());
    savedEmployee.setLastName(employee.getLastName());
    savedEmployee.setAge(employee.getAge());
    savedEmployee.setPhone(employee.getPhone());
    employeeRepository.save(savedEmployee);

    createLinks(savedEmployee);

    return new ResponseEntity<Employee>(savedEmployee, HttpStatus.OK);

  }

  private void createLinks(Employee savedEmployee) {
    Link link = linkTo(methodOn(ComplexEmployeeController.class).getEmployee(savedEmployee.getId())).withSelfRel();
    LinkRelation rel = new LinkRelation() {
      @Override
      public String value() {
        return "transition";
      }
    };
    Link eventLink = linkTo(methodOn(ComplexEmployeeController.class).updateEmployeeState(savedEmployee.getId(), null))
        .withRel(rel).withName("For next trasition").withType("HTTP Method : PUT");
    savedEmployee.add(link);
    savedEmployee.add(eventLink);
  }

}
