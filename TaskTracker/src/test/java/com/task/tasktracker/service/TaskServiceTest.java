package com.task.tasktracker.service;

import com.task.tasktracker.exception.TaskNotFoundException;
import com.task.tasktracker.model.Task;
import com.task.tasktracker.model.TaskStatus;
import com.task.tasktracker.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addTask_Success() {
        String desc = "Test Task";
        Task mockTask = new Task(1, desc, TaskStatus.TODO, LocalDateTime.now(), LocalDateTime.now());
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);

        Task result = taskService.addTask(desc);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(desc, result.getDescription());
        assertEquals(TaskStatus.TODO, result.getStatus());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void addTask_EmptyDescription_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> taskService.addTask(""));
        assertThrows(IllegalArgumentException.class, () -> taskService.addTask("   "));
        assertThrows(IllegalArgumentException.class, () -> taskService.addTask(null));
    }

    @Test
    void updateTaskDescription_Success() {
        int id = 1;
        String oldDesc = "Old Desc";
        String newDesc = "New Desc";
        LocalDateTime created = LocalDateTime.now().minusHours(1);
        Task existing = new Task(id, oldDesc, TaskStatus.TODO, created, created);

        when(taskRepository.findById(id)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateTaskDescription(id, newDesc);

        assertNotNull(result);
        assertEquals(newDesc, result.getDescription());
        assertTrue(result.getUpdatedAt().isAfter(created));
        verify(taskRepository, times(1)).save(existing);
    }

    @Test
    void updateTaskDescription_NotFound_ThrowsException() {
        when(taskRepository.findById(999)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTaskDescription(999, "New Desc"));
    }

    @Test
    void deleteTask_Success() {
        int id = 1;
        when(taskRepository.deleteById(id)).thenReturn(true);

        boolean result = taskService.deleteTask(id);

        assertTrue(result);
        verify(taskRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteTask_NotFound_ThrowsException() {
        int id = 999;
        when(taskRepository.deleteById(id)).thenReturn(false);

        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(id));
    }

    @Test
    void updateTaskStatus_Success() {
        int id = 1;
        Task existing = new Task(id, "Test", TaskStatus.TODO, LocalDateTime.now(), LocalDateTime.now());

        when(taskRepository.findById(id)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateTaskStatus(id, TaskStatus.IN_PROGRESS);

        assertNotNull(result);
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        verify(taskRepository, times(1)).save(existing);
    }

    @Test
    void getAllTasks_Success() {
        List<Task> mockTasks = Arrays.asList(
                new Task(1, "Task 1", TaskStatus.TODO, LocalDateTime.now(), LocalDateTime.now()),
                new Task(2, "Task 2", TaskStatus.DONE, LocalDateTime.now(), LocalDateTime.now())
        );
        when(taskRepository.findAll()).thenReturn(mockTasks);

        List<Task> result = taskService.getAllTasks();

        assertEquals(2, result.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTasksByStatus_Success() {
        List<Task> mockTasks = Arrays.asList(
                new Task(1, "Task 1", TaskStatus.TODO, LocalDateTime.now(), LocalDateTime.now()),
                new Task(2, "Task 2", TaskStatus.DONE, LocalDateTime.now(), LocalDateTime.now()),
                new Task(3, "Task 3", TaskStatus.TODO, LocalDateTime.now(), LocalDateTime.now())
        );
        when(taskRepository.findAll()).thenReturn(mockTasks);

        List<Task> result = taskService.getTasksByStatus(TaskStatus.TODO);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(t -> t.getStatus() == TaskStatus.TODO));
    }
}
