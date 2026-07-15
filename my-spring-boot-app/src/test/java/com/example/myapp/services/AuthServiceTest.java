package com.example.myapp.services;

import com.example.myapp.models.User;
import com.example.myapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("admin");
        mockUser.setPassword("$2a$10$hashedpassword");
    }

    @Test
    void authenticate_shouldReturnUser_whenCredentialsAreCorrect() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("admin123", "$2a$10$hashedpassword")).thenReturn(true);

        User result = authService.authenticate("admin", "admin123");

        assertNotNull(result);
        assertEquals("admin", result.getUsername());
        verify(userRepository).findByUsername("admin");
        verify(passwordEncoder).matches("admin123", "$2a$10$hashedpassword");
    }

    @Test
    void authenticate_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("nobody")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.authenticate("nobody", "any"));
        assertEquals("用户名或密码错误", ex.getMessage());
    }

    @Test
    void authenticate_shouldThrowException_whenPasswordMismatch() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrong", "$2a$10$hashedpassword")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> authService.authenticate("admin", "wrong"));
        assertEquals("用户名或密码错误", ex.getMessage());
    }
}