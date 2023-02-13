package com.yzlc.task.task;

import org.springframework.stereotype.Component;
import com.yzlc.common.annotation.Stopwatch;

@Component
public class TestTask {
    @Stopwatch
    //@Scheduled(cron = "0/1 * * * * ?")
    public String test() throws Exception {
        return "test";
    }
}
