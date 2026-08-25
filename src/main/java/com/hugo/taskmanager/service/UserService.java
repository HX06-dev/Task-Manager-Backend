package com.hugo.taskmanager.service;

import com.hugo.taskmanager.dto.*;
import com.hugo.taskmanager.entity.User;
import com.hugo.taskmanager.exception.DuplicateResourceException;
import com.hugo.taskmanager.exception.InvalidCredentialsException;
import com.hugo.taskmanager.exception.ResourceNotFoundException;
import com.hugo.taskmanager.repository.UserRepository;
import com.hugo.taskmanager.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public UserResponse createUser(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        return toResponse(userRepository.save(user));
    }

    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User target = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        target.setEmail(request.getEmail());
        target.setUsername(request.getUsername());
        return toResponse(userRepository.save(target));
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return toResponse(user);
    }

    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::toResponse).toList();
    }

    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taken");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        User registered = userRepository.save(user);
        return toResponse(registered);
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return jwtUtil.generateToken(request.getUsername());
        }
        throw new InvalidCredentialsException("Invalid username or password");
    }
}
