package com.hugo.taskmanager.service;

import com.hugo.taskmanager.dto.RegisterRequest;
import com.hugo.taskmanager.dto.UserResponse;
import com.hugo.taskmanager.dto.LoginRequest;
import com.hugo.taskmanager.dto.UserCreateRequest;
import com.hugo.taskmanager.dto.UserUpdateRequest;
import com.hugo.taskmanager.entity.User;
import com.hugo.taskmanager.exception.DuplicateResourceException;
import com.hugo.taskmanager.exception.InvalidCredentialsException;
import com.hugo.taskmanager.exception.ResourceNotFoundException;
import com.hugo.taskmanager.repository.UserRepository;
import com.hugo.taskmanager.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

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

    @Test
    void createUser_savesAndReturnsUser() {
        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("created");
        request.setEmail("created@example.com");

        User saved = new User();
        saved.setId(2L);
        saved.setUsername("created");
        saved.setEmail("created@example.com");
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserResponse result = userService.createUser(request);

        assertEquals(2L, result.getId());
        assertEquals("created", result.getUsername());
        assertEquals("created@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_updatesExistingUser() {
        User existing = new User();
        existing.setId(1L);
        existing.setUsername("old");
        existing.setEmail("old@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("updated");
        request.setEmail("updated@example.com");

        UserResponse result = userService.updateUser(1L, request);

        assertEquals("updated", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_throwsException_whenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(99L, new UserUpdateRequest()));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_deletesById() {
        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void findById_returnsUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("found");
        user.setEmail("found@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = userService.findById(1L);

        assertEquals("found", result.getUsername());
        assertEquals("found@example.com", result.getEmail());
    }

    @Test
    void findAll_returnsMappedUsers() {
        User first = new User();
        first.setId(1L);
        first.setUsername("first");
        first.setEmail("first@example.com");
        User second = new User();
        second.setId(2L);
        second.setUsername("second");
        second.setEmail("second@example.com");
        when(userRepository.findAll()).thenReturn(List.of(first, second));

        List<UserResponse> result = userService.findAll();

        assertEquals(2, result.size());
        assertEquals("first", result.get(0).getUsername());
        assertEquals("second", result.get(1).getUsername());
    }

    @Test
    void login_returnsToken_whenCredentialsAreValid() {
        User user = new User();
        user.setUsername("loginuser");
        user.setPassword("hashed");
        when(userRepository.findByUsername("loginuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken("loginuser")).thenReturn("token");

        LoginRequest request = new LoginRequest();
        request.setUsername("loginuser");
        request.setPassword("password123");

        assertEquals("token", userService.login(request));
    }

    @Test
    void login_throwsException_whenPasswordIsInvalid() {
        User user = new User();
        user.setUsername("loginuser");
        user.setPassword("hashed");
        when(userRepository.findByUsername("loginuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "hashed")).thenReturn(false);

        LoginRequest request = new LoginRequest();
        request.setUsername("loginuser");
        request.setPassword("wrongpass");

        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
        verify(jwtUtil, never()).generateToken(anyString());
    }
}