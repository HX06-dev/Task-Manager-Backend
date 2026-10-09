package com.hugo.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hugo.taskmanager.dto.UserCreateRequest;
import com.hugo.taskmanager.dto.UserResponse;
import com.hugo.taskmanager.dto.UserUpdateRequest;
import com.hugo.taskmanager.service.UserService;
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
class UserControllerTest {

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService)).build();
    }

    @Test
    void getAllUsers_returnsUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(user(1L, "one")));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("one"));
    }

    @Test
    void getUserById_returnsUser() throws Exception {
        when(userService.findById(1L)).thenReturn(user(1L, "one"));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("one"));
    }

    @Test
    void createUser_returnsCreatedUser() throws Exception {
        when(userService.createUser(any(UserCreateRequest.class))).thenReturn(user(1L, "one"));
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("one");
        request.setEmail("one@example.com");

        mockMvc.perform(post("/users")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("one"));
    }

    @Test
    void updateUser_returnsUpdatedUser() throws Exception {
        when(userService.updateUser(eq(1L), any(UserUpdateRequest.class))).thenReturn(user(1L, "updated"));
        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("updated");
        request.setEmail("updated@example.com");

        mockMvc.perform(put("/users/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated"));
    }

    @Test
    void deleteUser_returnsSuccessAndDelegates() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }

    private static UserResponse user(Long id, String username) {
        UserResponse response = new UserResponse();
        response.setId(id);
        response.setUsername(username);
        response.setEmail(username + "@example.com");
        return response;
    }
}
