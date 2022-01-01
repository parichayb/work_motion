package com.example.workmotion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

@SpringBootApplication
@OpenAPIDefinition
public class SimpleWorkMotionApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleWorkMotionApplication.class, args);
	}

}
