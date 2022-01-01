package com.example.workmotion.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee extends RepresentationModel<EmployeeResource> {
  
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	@JsonProperty("first-name")
	String firstName;
	
	@JsonProperty("last-name")
	String lastName;
	
	int age;
	
	int phone;
	
	@Enumerated(EnumType.STRING)
	EmployeeState state;
	
	@Enumerated(EnumType.STRING)
	EmployeeEvent nextEvent;

}
