package com.example.taskmanagerapi;

import com.example.taskmanagerapi.entity.Task;
import com.example.taskmanagerapi.entity.User;
import com.example.taskmanagerapi.repository.TaskRepository;
import com.example.taskmanagerapi.repository.UserRepository;
import com.example.taskmanagerapi.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private User user;

    @BeforeEach
    public void setUp() {
        // Create test user
        user = new User("john_doe", "password123", "john@example.com");
        user.setUserId(1L);

        // Create test task
        task = new Task("Buy groceries", "Milk, eggs, bread", "PENDING", 1L);
        task.setTaskId(1L);
        task.setUserId(1L);
    }

    @Test
    public void testCreateTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        Task createdTask = taskService.createTask(task, 1L);

        assertNotNull(createdTask);
        assertEquals("Buy groceries", createdTask.getTitle());
        assertEquals(1L, createdTask.getUserId());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    public void testGetTaskById_UserOwnsTask() {
        when(taskRepository.findByTaskIdAndUserId(1L, 1L)).thenReturn(Optional.of(task));

        Task foundTask = taskService.getTaskById(1L, 1L);

        assertNotNull(foundTask);
        assertEquals(1L, foundTask.getTaskId());
        verify(taskRepository, times(1)).findByTaskIdAndUserId(1L, 1L);
    }

    @Test
    public void testGetTaskById_UserDoesNotOwnTask() {
        when(taskRepository.findByTaskIdAndUserId(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.getTaskById(1L, 2L));
    }

    @Test
    public void testUpdateTask() {
        Task updatedDetails = new Task("Buy groceries and cook", "Updated description", "IN_PROGRESS", 1L);

        when(taskRepository.findByTaskIdAndUserId(1L, 1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskService.updateTask(1L, updatedDetails, 1L);

        verify(taskRepository, times(1)).findByTaskIdAndUserId(1L, 1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    public void testDeleteTask() {
        when(taskRepository.findByTaskIdAndUserId(1L, 1L)).thenReturn(Optional.of(task));

        taskService.deleteTask(1L, 1L);

        verify(taskRepository, times(1)).findByTaskIdAndUserId(1L, 1L);
        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    public void testGetUserIdByUsername() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));

        Long userId = taskService.getUserIdByUsername("john_doe");

        assertEquals(1L, userId);
        verify(userRepository, times(1)).findByUsername("john_doe");
    }
}