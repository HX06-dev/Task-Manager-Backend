package com.hugo.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hugo.taskmanager.dto.TaskCreateRequest;
import com.hugo.taskmanager.dto.TaskResponse;
import com.hugo.taskmanager.dto.TaskUpdateRequest;
import com.hugo.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TaskController(taskService)).build();
    }

    @Test
    void getAllTasks_returnsTasks() throws Exception {
        when(taskService.findAll()).thenReturn(List.of(task(1L, "First")));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("First"));
    }

    @Test
    void getTaskById_returnsTask() throws Exception {
        when(taskService.findById(1L)).thenReturn(task(1L, "First"));

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("First"));
    }

    @Test
    void createTask_returnsCreatedTask() throws Exception {
        when(taskService.createTask(any(TaskCreateRequest.class))).thenReturn(task(1L, "New task"));
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("New task");
        request.setUserId(2L);

        mockMvc.perform(post("/tasks")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New task"));
    }

    @Test
    void updateTask_returnsUpdatedTask() throws Exception {
        when(taskService.updateTask(eq(1L), any(TaskUpdateRequest.class))).thenReturn(task(1L, "Updated"));
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Updated");
        request.setStatus("DONE");
        request.setUserId(2L);

        mockMvc.perform(put("/tasks/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void deleteTask_returnsSuccessAndDelegates() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isOk());

        verify(taskService).deleteTask(1L);
    }

    private static TaskResponse task(Long id, String title) {
        TaskResponse response = new TaskResponse();
        response.setId(id);
        response.setTitle(title);
        response.setStatus("TODO");
        response.setUserId(2L);
        response.setUsername("owner");
        return response;
    }
}
