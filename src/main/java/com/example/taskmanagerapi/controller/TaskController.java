package com.example.taskmanagerapi.controller;

import com.example.taskmanagerapi.entity.Task;
import com.example.taskmanagerapi.entity.User;
import com.example.taskmanagerapi.repository.TaskRepository;
import com.example.taskmanagerapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    // GET all tasks for authenticated user
    @GetMapping
    public List<Task> getAllTasks(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            throw new RuntimeException("Unauthorized: JWT token required");
        }

        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Return only THIS user's tasks
        return taskRepository.findByUserId(user.getUserId());
    }

    // GET task by ID (only if user owns it)
    @GetMapping("/{taskId}")
    public Task getTaskById(@PathVariable Long taskId, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            throw new RuntimeException("Unauthorized: JWT token required");
        }

        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find task and verify user owns it
        return taskRepository.findByTaskIdAndUserId(taskId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));
    }

    // CREATE new task
    @PostMapping
    public Task createTask(@RequestBody Task task, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            throw new RuntimeException("Unauthorized: JWT token required");
        }

        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Set userId for this task
        task.setUserId(user.getUserId());

        if (task.getCreatedDate() == null) {
            task.setCreatedDate(java.time.LocalDateTime.now());
        }

        return taskRepository.save(task);
    }

    // UPDATE task (only if user owns it)
    @PutMapping("/{taskId}")
    public Task updateTask(@PathVariable Long taskId, @RequestBody Task taskDetails, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            throw new RuntimeException("Unauthorized: JWT token required");
        }

        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find task and verify user owns it
        Task task = taskRepository.findByTaskIdAndUserId(taskId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        // Update task fields
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setDueDate(taskDetails.getDueDate());

        return taskRepository.save(task);
    }

    // DELETE task (only if user owns it)
    @DeleteMapping("/{taskId}")
    public String deleteTask(@PathVariable Long taskId, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            throw new RuntimeException("Unauthorized: JWT token required");
        }

        // Find user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find task and verify user owns it
        Task task = taskRepository.findByTaskIdAndUserId(taskId, user.getUserId())
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        taskRepository.delete(task);
        return "Task deleted successfully with id: " + taskId;
    }
}