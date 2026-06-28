package com.task.tasktracker.controller;

import com.task.tasktracker.exception.TaskNotFoundException;
import com.task.tasktracker.model.Task;
import com.task.tasktracker.model.TaskStatus;
import com.task.tasktracker.service.TaskService;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class TaskController {

    private final TaskService taskService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    public int handleCommand(String[] args) {
        if (args == null || args.length == 0) {
            printUsage();
            return 1;
        }

        String command = args[0].toLowerCase();

        try {
            switch (command) {
                case "add":
                    return handleAdd(args);
                case "update":
                    return handleUpdate(args);
                case "delete":
                    return handleDelete(args);
                case "mark-in-progress":
                    return handleMarkStatus(args, TaskStatus.IN_PROGRESS);
                case "mark-done":
                    return handleMarkStatus(args, TaskStatus.DONE);
                case "list":
                    return handleList(args);
                case "help":
                case "--help":
                case "-h":
                    printUsage();
                    return 0;
                default:
                    System.err.println("Error: Unknown command '" + args[0] + "'");
                    printUsage();
                    return 1;
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        } catch (TaskNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }

    private int handleAdd(String[] args) {
        if (args.length < 2) {
            System.err.println("Error: Missing description. Usage: task-cli add \"<task description>\"");
            return 1;
        }
        String description = args[1];
        Task task = taskService.addTask(description);
        System.out.println("Task added successfully (ID: " + task.getId() + ")");
        return 0;
    }

    private int handleUpdate(String[] args) {
        if (args.length < 3) {
            System.err.println("Error: Missing ID or description. Usage: task-cli update <id> \"<new description>\"");
            return 1;
        }
        Integer id = parseId(args[1]);
        if (id == null) return 1;

        String description = args[2];
        Task task = taskService.updateTaskDescription(id, description);
        System.out.println("Task updated successfully (ID: " + task.getId() + ")");
        return 0;
    }

    private int handleDelete(String[] args) {
        if (args.length < 2) {
            System.err.println("Error: Missing ID. Usage: task-cli delete <id>");
            return 1;
        }
        Integer id = parseId(args[1]);
        if (id == null) return 1;

        taskService.deleteTask(id);
        System.out.println("Task deleted successfully (ID: " + id + ")");
        return 0;
    }

    private int handleMarkStatus(String[] args, TaskStatus status) {
        if (args.length < 2) {
            System.err.println("Error: Missing ID. Usage: task-cli " + (status == TaskStatus.DONE ? "mark-done" : "mark-in-progress") + " <id>");
            return 1;
        }
        Integer id = parseId(args[1]);
        if (id == null) return 1;

        taskService.updateTaskStatus(id, status);
        String statusStr = status == TaskStatus.DONE ? "done" : "in-progress";
        System.out.println("Task marked as " + statusStr + " successfully (ID: " + id + ")");
        return 0;
    }

    private int handleList(String[] args) {
        List<Task> tasks;
        if (args.length > 1) {
            String filter = args[1].toLowerCase();
            try {
                // Handle alias "todo" or status names
                TaskStatus status = TaskStatus.fromValue(filter);
                tasks = taskService.getTasksByStatus(status);
            } catch (IllegalArgumentException e) {
                System.err.println("Error: Invalid status filter '" + args[1] + "'. Must be one of: todo, in-progress, done.");
                return 1;
            }
        } else {
            tasks = taskService.getAllTasks();
        }

        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return 0;
        }

        printTasksTable(tasks);
        return 0;
    }

    private void printTasksTable(List<Task> tasks) {
        // Find maximum description length to format nicely, min length of 11 for alignment
        int maxDescLen = 11;
        for (Task task : tasks) {
            if (task.getDescription() != null && task.getDescription().length() > maxDescLen) {
                maxDescLen = Math.min(task.getDescription().length(), 60); // Cap description width at 60 characters for readability
            }
        }

        String format = "%-4s | %-" + maxDescLen + "s | %-12s | %-19s | %-19s%n";
        System.out.format(format, "ID", "Description", "Status", "Created At", "Updated At");
        System.out.println("-".repeat(4 + 3 + maxDescLen + 3 + 12 + 3 + 19 + 3 + 19));

        for (Task task : tasks) {
            String desc = task.getDescription();
            if (desc.length() > 60) {
                desc = desc.substring(0, 57) + "...";
            }
            System.out.format(format,
                    task.getId(),
                    desc,
                    task.getStatus().getValue(),
                    task.getCreatedAt().format(DATE_FORMATTER),
                    task.getUpdatedAt().format(DATE_FORMATTER)
            );
        }
    }

    private Integer parseId(String val) {
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            System.err.println("Error: Invalid ID '" + val + "'. ID must be an integer.");
            return null;
        }
    }

    private void printUsage() {
        System.out.println("Task Tracker CLI - Usage:");
        System.out.println("  task-cli add \"<description>\"             Add a new task");
        System.out.println("  task-cli update <id> \"<description>\"      Update a task's description");
        System.out.println("  task-cli delete <id>                     Delete a task");
        System.out.println("  task-cli mark-in-progress <id>           Mark a task as in progress");
        System.out.println("  task-cli mark-done <id>                  Mark a task as done");
        System.out.println("  task-cli list                            List all tasks");
        System.out.println("  task-cli list <status>                   List tasks by status (todo, in-progress, done)");
    }
}

