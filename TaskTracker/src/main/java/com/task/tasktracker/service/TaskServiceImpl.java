package com.task.tasktracker.service;
import com.task.tasktracker.exception.TaskNotFoundException;
import com.task.tasktracker.model.Task;
import com.task.tasktracker.model.TaskStatus;
import com.task.tasktracker.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task addTask(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty");
        }
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task(null, description.trim(), TaskStatus.TODO, now, now);
        return taskRepository.save(task);
    }

    @Override
    public Task updateTaskDescription(Integer id, String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Task description cannot be empty");
        }
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
        task.setDescription(description.trim());
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @Override
    public boolean deleteTask(Integer id) {
        boolean deleted = taskRepository.deleteById(id);
        if (!deleted) {
            throw new TaskNotFoundException("Task with ID " + id + " not found");
        }
        return true;
    }

    @Override
    public Task updateTaskStatus(Integer id, TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Task status cannot be null");
        }
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task with ID " + id + " not found"));
        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> getTasksByStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Task status cannot be null");
        }
        return taskRepository.findAll().stream()
                .filter(task -> task.getStatus() == status)
                .collect(Collectors.toList());
    }
}
