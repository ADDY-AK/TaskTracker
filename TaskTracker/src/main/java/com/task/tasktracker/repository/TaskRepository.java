package com.task.tasktracker.repository;

import com.task.tasktracker.model.Task;
import java.util.List;
import java.util.Optional;

public interface TaskRepository {
    List<Task> findAll();
    Optional<Task> findById(Integer id);
    Task save(Task task);
    boolean deleteById(Integer id);
}

