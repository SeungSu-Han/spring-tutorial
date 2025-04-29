package com.ssafy.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.springboot.Domain.Test;

public interface TestRepository extends JpaRepository<Test, Long> {}
