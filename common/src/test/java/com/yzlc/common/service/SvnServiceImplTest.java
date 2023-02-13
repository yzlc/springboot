package com.yzlc.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;

@RunWith(SpringRunner.class)
@SpringBootTest
class SvnServiceImplTest {
    @Autowired
    private SvnService svnService;

    public void main(String[] args) {
        // System.out.println(checkOut());
        // System.out.println(doCleanup());
    }

    @Test
    void createIssue() {
    }
}