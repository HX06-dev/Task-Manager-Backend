package com.hugo.taskmanager.service;

import com.hugo.taskmanager.dto.TaskCreateRequest;
import com.hugo.taskmanager.dto.TaskResponse;
import com.hugo.taskmanager.dto.TaskUpdateRequest;
import com.hugo.taskmanager.entity.Task;
import com.hugo.taskmanager.entity.User;
import com.hugo.taskmanager.exception.ResourceNotFoundException;
import com.hugo.taskmanager.repository.TaskRepository;
import com.hugo.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void createTask_savesTaskForExistingUser() {
        User user = user(1L, "owner");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Task saved = task(10L, "Write tests", "TODO", user);
        when(taskRepository.save(any(Task.class))).thenReturn(saved);

        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Write tests");
        request.setUserId(1L);

        TaskResponse result = taskService.createTask(request);

        assertEquals(10L, result.getId());
        assertEquals("Write tests", result.getTitle());
        assertEquals("owner", result.getUsername());
        assertEquals(1L, result.getUserId());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void createTask_throwsException_whenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Orphan task");
        request.setUserId(99L);

        assertThrows(ResourceNotFoundException.class, () -> taskService.createTask(request));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_updatesTaskAndUser() {
        User oldUser = user(1L, "old-owner");
        User newUser = user(2L, "new-owner");
        Task existing = task(10L, "Old title", "TODO", oldUser);
        when(taskRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newUser));
        when(taskRepository.save(existing)).thenReturn(existing);

        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Updated title");
        request.setStatus("DONE");
        request.setUserId(2L);

        TaskResponse result = taskService.updateTask(10L, request);

        assertEquals("Updated title", result.getTitle());
        assertEquals("DONE", result.getStatus());
        assertEquals("new-owner", result.getUsername());
        verify(taskRepository).save(existing);
    }

    @Test
    void updateTask_throwsException_whenTaskDoesNotExist() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.updateTask(99L, new TaskUpdateRequest()));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_deletesById() {
        taskService.deleteTask(10L);

        verify(taskRepository).deleteById(10L);
    }

    @Test
    void findById_returnsTask() {
        User user = user(1L, "owner");
        when(taskRepository.findById(10L)).thenReturn(Optional.of(task(10L, "Task", "IN_PROGRESS", user)));

        TaskResponse result = taskService.findById(10L);

        assertEquals("Task", result.getTitle());
        assertEquals("IN_PROGRESS", result.getStatus());
        assertEquals("owner", result.getUsername());
    }

    @Test
    void findAll_returnsMappedTasks() {
        User user = user(1L, "owner");
        when(taskRepository.findAll()).thenReturn(List.of(
                task(10L, "First", "TODO", user),
                task(11L, "Second", "DONE", user)));

        List<TaskResponse> result = taskService.findAll();

        assertEquals(2, result.size());
        assertEquals("First", result.get(0).getTitle());
        assertEquals("DONE", result.get(1).getStatus());
    }

    private static User user(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        return user;
    }

    private static Task task(Long id, String title, String status, User user) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setStatus(status);
        task.setUser(user);
        return task;
    }
}
