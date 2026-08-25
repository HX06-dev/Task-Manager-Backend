package com.hugo.taskmanager.service;

import com.hugo.taskmanager.dto.RegisterRequest;
import com.hugo.taskmanager.dto.UserResponse;
import com.hugo.taskmanager.entity.User;
import com.hugo.taskmanager.exception.DuplicateResourceException;
import com.hugo.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register_savesNewUser_whenUsernameIsAvailable() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setEmail("new@example.com");
        savedUser.setPassword("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse result = userService.register(request);

        assertEquals("newuser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_throwsException_whenUsernameAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("new@example.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        DuplicateResourceException exception = assertThrows(DuplicateResourceException.class, () -> { userService.register(request); } );

        String expectedMessage = "Username already taken";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}