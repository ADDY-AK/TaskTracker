package com.task.tasktracker.repository;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.tasktracker.model.Task;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepositoryImpl implements TaskRepository {
    private static final String FILE_NAME = "tasks.json";
    private final ObjectMapper objectMapper;
    private final String fileDirectory;

    public TaskRepositoryImpl(ObjectMapper objectMapper,
                              @org.springframework.beans.factory.annotation.Value("${app.task-file.directory:#{systemProperties['user.dir']}}") String fileDirectory) {
        this.objectMapper = objectMapper;
        this.fileDirectory = fileDirectory;
    }

    private Path getFilePath() {
        return Paths.get(fileDirectory, FILE_NAME).toAbsolutePath();
    }

    @Override
    public List<Task> findAll() {
        Path path = getFilePath();
        if (!Files.exists(path)) {
            writeAll(new ArrayList<>());
            return new ArrayList<>();
        }
        try {
            String content = Files.readString(path);
            if (content.trim().isEmpty()) {
                writeAll(new ArrayList<>());
                return new ArrayList<>();
            }
            return objectMapper.readValue(content, new TypeReference<List<Task>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error reading tasks file: " + e.getMessage(), e);
        }
    }

    private void writeAll(List<Task> tasks) {
        Path path = getFilePath();
        try {
            Path parent = path.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            String content = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(tasks);
            Files.writeString(path, content);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to tasks file: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Task> findById(Integer id) {
        return findAll().stream()
                .filter(task -> task.getId().equals(id))
                .findFirst();
    }

    @Override
    public Task save(Task task) {
        List<Task> tasks = findAll();
        if (task.getId() == null) {
            int nextId = tasks.stream()
                    .mapToInt(Task::getId)
                    .max()
                    .orElse(0) + 1;
            task.setId(nextId);
            tasks.add(task);
        } else {
            boolean found = false;
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId().equals(task.getId())) {
                    tasks.set(i, task);
                    found = true;
                    break;
                }
            }
            if (!found) {
                tasks.add(task);
            }
        }
        writeAll(tasks);
        return task;
    }

    @Override
    public boolean deleteById(Integer id) {
        List<Task> tasks = findAll();
        boolean removed = tasks.removeIf(task -> task.getId().equals(id));
        if (removed) {
            writeAll(tasks);
        }
        return removed;
    }
}
