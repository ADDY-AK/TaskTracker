package com.task.tasktracker.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.task.tasktracker.model.Task;
import com.task.tasktracker.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskRepositoryTest {

    @TempDir
    Path tempDir;

    private TaskRepositoryImpl repository;
    private Path targetFile;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        repository = new TaskRepositoryImpl(objectMapper, tempDir.toString());
        targetFile = tempDir.resolve("tasks.json");
    }

    @Test
    void findAll_FileDoesNotExist_CreatesEmptyFile() throws IOException {
        assertFalse(Files.exists(targetFile));

        List<Task> tasks = repository.findAll();

        assertTrue(tasks.isEmpty());
        assertTrue(Files.exists(targetFile));
        assertEquals("[]", Files.readString(targetFile).replaceAll("\\s+", ""));
    }

    @Test
    void save_NewTask_AssignsIdAndWritesToFile() {
        Task task = new Task(null, "Test Task", TaskStatus.TODO, LocalDateTime.now(), LocalDateTime.now());

        Task saved = repository.save(task);

        assertNotNull(saved.getId());
        assertEquals(1, saved.getId());

        List<Task> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals("Test Task", all.get(0).getDescription());
    }

    @Test
    void save_ExistingTask_UpdatesTaskAndWritesToFile() {
        Task task = new Task(null, "Test Task", TaskStatus.TODO, LocalDateTime.now(), LocalDateTime.now());
        Task saved = repository.save(task);

        saved.setDescription("Updated Description");
        Task updated = repository.save(saved);

        assertEquals("Updated Description", updated.getDescription());

        List<Task> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals("Updated Description", all.get(0).getDescription());
    }

    @Test
    void findById_ReturnsCorrectTask() {
        Task t1 = new Task(null, "T1", TaskStatus.TODO, LocalDateTime.now(), LocalDateTime.now());
        Task t2 = new Task(null, "T2", TaskStatus.TODO, LocalDateTime.now(), LocalDateTime.now());
        repository.save(t1);
        repository.save(t2);

        Optional<Task> found = repository.findById(2);
        assertTrue(found.isPresent());
        assertEquals("T2", found.get().getDescription());

        Optional<Task> notFound = repository.findById(999);
        assertFalse(notFound.isPresent());
    }

    @Test
    void deleteById_RemovesTaskAndWritesToFile() {
        Task t1 = new Task(null, "T1", TaskStatus.TODO, LocalDateTime.now(), LocalDateTime.now());
        repository.save(t1);

        boolean deleted = repository.deleteById(1);
        assertTrue(deleted);

        List<Task> all = repository.findAll();
        assertTrue(all.isEmpty());

        boolean notDeleted = repository.deleteById(999);
        assertFalse(notDeleted);
    }
}
