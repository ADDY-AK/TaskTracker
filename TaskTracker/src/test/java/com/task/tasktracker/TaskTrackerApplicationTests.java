package com.task.tasktracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.cli.enabled=false")
class TaskTrackerApplicationTests {

    @Test
    void contextLoads() {
    }

}
