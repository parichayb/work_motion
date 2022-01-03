package com.example.workmotion;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.workmotion.controller.ComplexEmployeeController;

@SpringBootTest
  public class SmokeTest {

    @Autowired
    private ComplexEmployeeController controller;

    @Test
    public void contextLoads() throws Exception {
      assertThat(controller).isNotNull();
    }
  }


