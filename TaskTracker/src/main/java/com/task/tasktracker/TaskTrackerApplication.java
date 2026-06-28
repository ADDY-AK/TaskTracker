package com.task.tasktracker;

import com.task.tasktracker.controller.TaskController;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TaskTrackerApplication implements CommandLineRunner {

    private final TaskController taskController;
    private final ApplicationContext context;

    @org.springframework.beans.factory.annotation.Value("${app.cli.enabled:true}")
    private boolean cliEnabled;

    public TaskTrackerApplication(TaskController taskController, ApplicationContext context) {
        this.taskController = taskController;
        this.context = context;
    }

    public static void main(String[] args) {
        SpringApplication.run(TaskTrackerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        if (!cliEnabled) {
            return;
        }
        int exitCode = taskController.handleCommand(args);
        System.exit(SpringApplication.exit(context, () -> exitCode));
    }
}
