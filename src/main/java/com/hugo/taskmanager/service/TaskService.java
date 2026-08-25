package com.hugo.taskmanager.service;

import com.hugo.taskmanager.dto.TaskCreateRequest;
import com.hugo.taskmanager.dto.TaskResponse;
import com.hugo.taskmanager.dto.TaskUpdateRequest;
import com.hugo.taskmanager.entity.Task;
import com.hugo.taskmanager.entity.User;
import com.hugo.taskmanager.exception.ResourceNotFoundException;
import com.hugo.taskmanager.repository.TaskRepository;
import com.hugo.taskmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskResponse createTask(TaskCreateRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setUser(user);
        return toResponse(taskRepository.save(task));
    }

    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));
        task.setTitle(request.getTitle());
        task.setStatus(request.getStatus());
        task.setUser(user);
        return toResponse(taskRepository.save(task));
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    public TaskResponse findById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return toResponse(task);
    }

    public List<TaskResponse> findAll() {
        List<Task> tasks = taskRepository.findAll();
        return tasks.stream().map(this::toResponse).toList();
    }

    private TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setUsername(task.getUser().getUsername());
        response.setTitle(task.getTitle());
        response.setStatus(task.getStatus());
        response.setCreatedAt(task.getCreatedAt());
        response.setUserId(task.getUser().getId());
        return response;
    }
}
