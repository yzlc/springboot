package com.yzlc.common.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
class SvnServiceImplTest {
    @Autowired
    private SvnService svnService;

    @Test
    void checkOut() {
        svnService.checkOut();
    }

    @Test
    void doUpdate() {
        svnService.doUpdate();
    }
}