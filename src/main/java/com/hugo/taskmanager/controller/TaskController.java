package com.hugo.taskmanager.controller;

import com.hugo.taskmanager.dto.TaskCreateRequest;
import com.hugo.taskmanager.dto.TaskResponse;
import com.hugo.taskmanager.dto.TaskUpdateRequest;
import com.hugo.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) { this.taskService = taskService; }

    @GetMapping
    public List<TaskResponse> getAllTasks() { return taskService.findAll(); }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable Long id) { return taskService.findById(id); }

    @PostMapping
    public TaskResponse createTask(@Valid @RequestBody TaskCreateRequest request) {
        return taskService.createTask(request);
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(@Valid @RequestBody TaskUpdateRequest request, @PathVariable Long id) { return taskService.updateTask(id, request); }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) { taskService.deleteTask(id); }
}
