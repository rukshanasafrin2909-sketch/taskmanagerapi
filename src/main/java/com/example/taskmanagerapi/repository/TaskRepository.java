package com.example.taskmanagerapi.repository;

import com.example.taskmanagerapi.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    //Find all tasks for specific user
    List<Task> findByUserId(Long userId);

    //Find a specific task by taskId and userId
    Optional<Task> findByTaskIdAndUserId(Long taskId, Long userId);
}
