package com.task.tasktracker.service;

import com.task.tasktracker.model.Task;
import com.task.tasktracker.model.TaskStatus;

import java.util.List;

public class TaskServiceImpl implements TaskService {
    @Override
    public Task addTask(String description) {
        return null;
    }

    @Override
    public Task updateTaskDescription(Integer id, String description) {
        return null;
    }

    @Override
    public boolean deleteTask(Integer id) {
        return false;
    }

    @Override
    public Task updateTaskStatus(Integer id, TaskStatus status) {
        return null;
    }

    @Override
    public List<Task> getAllTasks() {
        return List.of();
    }

    @Override
    public List<Task> getTasksByStatus(TaskStatus status) {
        return List.of();
    }
}
