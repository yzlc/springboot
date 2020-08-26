package xyz.yzlc.task.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.yzlc.task.task.TestTask;

/**
 * @author yzlc
 */
@RestController
@RequestMapping("")
public class TestController {
    @Autowired
    private TestTask testTask;

    @GetMapping("/test")
    public String test() throws Exception {
        return testTask.test();
    }
}
