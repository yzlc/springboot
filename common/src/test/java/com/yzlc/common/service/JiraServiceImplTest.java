package com.yzlc.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
class JiraServiceImplTest {
    @Autowired
    private JiraService jiraService;
    public void main(String[] args) throws FileNotFoundException, JsonProcessingException {
//        getIssue("OAGJXT-1");
//        createIssue( "新创建issue描述", "新创建issue主题");
//       updateIssue("ADTEST-2");
//        deleteIssue("ADTEST-1");
//        changeStatus("ADTEST-1");
//        addCommon("ADTEST-1");
//        getProject(PROJECT_KEY);
//        createSubTask("创建子任务描述", "创建子任务主题","ADTEST-2");
//        getIssueAllTypes();
//        addAttachment("ADTEST-2", "D:/项目文件/test.txt");
//        ProjectInfo projectInfo = ProjectInfo.Builder.projectInfo().setName("").build();
    }

    @Test
    void createIssue() {
    }
}