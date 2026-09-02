package com.example.taskmanagerapi.service;

import com.example.taskmanagerapi.entity.Task;
import com.example.taskmanagerapi.entity.User;
import com.example.taskmanagerapi.repository.TaskRepository;
import com.example.taskmanagerapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all tasks for a user
    public List<Task> getAllTasksForUser(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    // Get task by ID if user owns it
    public Task getTaskById(Long taskId, Long userId) {
        return taskRepository.findByTaskIdAndUserId(taskId, userId)
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));
    }

    // Create task for user
    public Task createTask(Task task, Long userId) {
        task.setUserId(userId);
        if (task.getCreatedDate() == null) {
            task.setCreatedDate(LocalDateTime.now());
        }
        return taskRepository.save(task);
    }

    // Update task if user owns it
    public Task updateTask(Long taskId, Task taskDetails, Long userId) {
        Task task = getTaskById(taskId, userId);
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setDueDate(taskDetails.getDueDate());
        return taskRepository.save(task);
    }

    // Delete task if user owns it
    public void deleteTask(Long taskId, Long userId) {
        Task task = getTaskById(taskId, userId);
        taskRepository.delete(task);
    }

    // Helper: Get user ID from username
    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(User::getUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}