package xyz.yzlc.task.task;

import org.springframework.stereotype.Component;
import xyz.yzlc.common.annotation.Stopwatch;

@Component
public class TestTask {
    @Stopwatch
    //@Scheduled(cron = "0/1 * * * * ?")
    public String test() throws Exception {
        return "test";
    }
}
