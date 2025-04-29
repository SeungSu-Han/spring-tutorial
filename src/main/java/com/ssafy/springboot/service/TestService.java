package com.ssafy.springboot.service;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.springboot.Domain.Test;
import com.ssafy.springboot.repository.TestRepository;

import lombok.RequiredArgsConstructor;

@Service
@Repository
@RequiredArgsConstructor
public class TestService {
 
    private final TestRepository testRepository;

    /* Read All*/
    @Transactional(readOnly = true)
    public List<Test> findAllTests() {
        return testRepository.findAll();
    }
    
    @Transactional
    public Test insertTests(Test test) {
        return testRepository.save(test);
    }
}
