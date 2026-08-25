package com.hugo.taskmanager.controller;

import com.hugo.taskmanager.dto.LoginRequest;
import com.hugo.taskmanager.dto.LoginResponse;
import com.hugo.taskmanager.dto.RegisterRequest;
import com.hugo.taskmanager.dto.UserResponse;
import com.hugo.taskmanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserResponse register (@Valid @RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        String token = userService.login(request);
        return new LoginResponse(token);
    }
}
