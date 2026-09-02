package com.example.taskmanagerapi.controller;

import com.example.taskmanagerapi.service.TaskService;
import com.example.taskmanagerapi.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public List<Task> getAllTasks(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            throw new RuntimeException("Unauthorized: JWT token required");
        }

        Long userId = taskService.getUserIdByUsername(username);
        return taskService.getAllTasksForUser(userId);
    }

    @GetMapping("/{taskId}")
    public Task getTaskById(@PathVariable Long taskId, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            throw new RuntimeException("Unauthorized: JWT token required");
        }

        Long userId = taskService.getUserIdByUsername(username);
        return taskService.getTaskById(taskId, userId);
    }

    @PostMapping
    public Task createTask(@RequestBody Task task, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            throw new RuntimeException("Unauthorized: JWT token required");
        }

        Long userId = taskService.getUserIdByUsername(username);
        return taskService.createTask(task, userId);
    }

    @PutMapping("/{taskId}")
    public Task updateTask(@PathVariable Long taskId, @RequestBody Task taskDetails, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            throw new RuntimeException("Unauthorized: JWT token required");
        }

        Long userId = taskService.getUserIdByUsername(username);
        return taskService.updateTask(taskId, taskDetails, userId);
    }

    @DeleteMapping("/{taskId}")
    public String deleteTask(@PathVariable Long taskId, HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (username == null) {
            throw new RuntimeException("Unauthorized: JWT token required");
        }

        Long userId = taskService.getUserIdByUsername(username);
        taskService.deleteTask(taskId, userId);
        return "Task deleted successfully with id: " + taskId;
    }
}