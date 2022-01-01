package com.example.workmotion.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.workmotion.domain.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  
}
