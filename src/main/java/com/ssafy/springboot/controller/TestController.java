package com.ssafy.springboot.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.springboot.Domain.Test;
import com.ssafy.springboot.service.TestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/tests")
public class TestController {

    private final TestService testService;

    @GetMapping
    public List<Test> findAllTests() {
        return testService.findAllTests();
    }
    
    @PostMapping
    public Test insertTests(Test test) {
    	return testService.insertTests(test);
    }
}
