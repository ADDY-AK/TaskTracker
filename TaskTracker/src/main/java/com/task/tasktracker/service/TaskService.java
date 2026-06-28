package com.task.tasktracker.service;


import com.task.tasktracker.model.Task;
import com.task.tasktracker.model.TaskStatus;
import java.util.List;

public interface TaskService {
    Task addTask(String description);
    Task updateTaskDescription(Integer id, String description);
    boolean deleteTask(Integer id);
    Task updateTaskStatus(Integer id, TaskStatus status);
    List<Task> getAllTasks();
    List<Task> getTasksByStatus(TaskStatus status);
}
