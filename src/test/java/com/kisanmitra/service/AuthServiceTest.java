package com.kisanmitra.service;

import com.kisanmitra.dto.LoginRequest;
import com.kisanmitra.dto.RegisterRequest;
import com.kisanmitra.dto.UserResponse;
import com.kisanmitra.entity.Role;
import com.kisanmitra.entity.User;
import com.kisanmitra.repository.UserRepository;
import com.kisanmitra.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final JwtUtils jwtUtils = new JwtUtils() {
        @Override
        public String generateTokenFromEmail(String email) {
            return "mock-jwt-token";
        }
    };

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtUtils);
    }

    @Test
    @DisplayName("register should succeed for valid farmer details, hash password, and return JWT token")
    void register_Success_Farmer() {
        RegisterRequest req = new RegisterRequest(
                "Ramesh Kumar", "ramesh@kisanmitra.ai", "9876543210", "Password123", Role.FARMER
        );

        when(userRepository.findByEmail("ramesh@kisanmitra.ai")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Password123")).thenReturn("encodedPasswordHash");

        User savedMockUser = new User();
        savedMockUser.setId(1L);
        savedMockUser.setFullName("Ramesh Kumar");
        savedMockUser.setEmail("ramesh@kisanmitra.ai");
        savedMockUser.setPhone("9876543210");
        savedMockUser.setPassword("encodedPasswordHash");
        savedMockUser.setRole(Role.FARMER);

        when(userRepository.save(any(User.class))).thenReturn(savedMockUser);

        UserResponse response = authService.register(req);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Ramesh Kumar", response.getFullName());
        assertEquals("ramesh@kisanmitra.ai", response.getEmail());
        assertEquals(Role.FARMER, response.getRole());
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("User registered successfully", response.getMessage());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());
        User capturedUser = captor.getValue();
        assertEquals("encodedPasswordHash", capturedUser.getPassword());
        assertEquals(Role.FARMER, capturedUser.getRole());
    }

    @Test
    @DisplayName("register should succeed for valid expert role")
    void register_Success_Expert() {
        RegisterRequest req = new RegisterRequest(
                "Dr. Swaminathan", "expert@kisanmitra.ai", "9123456789", "SecurePass123", Role.EXPERT
        );

        when(userRepository.findByEmail("expert@kisanmitra.ai")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("SecurePass123")).thenReturn("encodedExpertHash");

        User savedMockUser = new User();
        savedMockUser.setId(2L);
        savedMockUser.setFullName("Dr. Swaminathan");
        savedMockUser.setEmail("expert@kisanmitra.ai");
        savedMockUser.setPhone("9123456789");
        savedMockUser.setPassword("encodedExpertHash");
        savedMockUser.setRole(Role.EXPERT);

        when(userRepository.save(any(User.class))).thenReturn(savedMockUser);

        UserResponse response = authService.register(req);

        assertNotNull(response);
        assertEquals(Role.EXPERT, response.getRole());
        assertEquals("mock-jwt-token", response.getToken());
    }

    @Test
    @DisplayName("register should throw 409 CONFLICT when email is already registered")
    void register_DuplicateEmail_ThrowsConflict() {
        RegisterRequest req = new RegisterRequest(
                "Duplicate User", "existing@kisanmitra.ai", "9876543210", "Password123", Role.FARMER
        );

        when(userRepository.findByEmail("existing@kisanmitra.ai")).thenReturn(Optional.of(new User()));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.register(req));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Email is already registered", ex.getReason());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("register should throw 403 FORBIDDEN when attempting to register as ADMIN")
    void register_AdminRole_ThrowsForbidden() {
        RegisterRequest req = new RegisterRequest(
                "Admin Wannabe", "admin@kisanmitra.ai", "9876543210", "Password123", Role.ADMIN
        );

        when(userRepository.findByEmail("admin@kisanmitra.ai")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.register(req));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        assertTrue(ex.getReason().contains("ADMIN"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("login should succeed for valid credentials and return JWT token")
    void login_Success() {
        LoginRequest req = new LoginRequest("farmer@kisanmitra.ai", "Password123");

        User mockUser = new User();
        mockUser.setId(10L);
        mockUser.setEmail("farmer@kisanmitra.ai");
        mockUser.setPassword("encodedHash");
        mockUser.setRole(Role.FARMER);

        when(userRepository.findByEmail("farmer@kisanmitra.ai")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("Password123", "encodedHash")).thenReturn(true);

        UserResponse response = authService.login(req);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    @DisplayName("login should throw 401 UNAUTHORIZED on nonexistent email")
    void login_NonexistentEmail_ThrowsUnauthorized() {
        LoginRequest req = new LoginRequest("nonexistent@kisanmitra.ai", "Password123");

        when(userRepository.findByEmail("nonexistent@kisanmitra.ai")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(req));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    @Test
    @DisplayName("login should throw 401 UNAUTHORIZED on incorrect password")
    void login_IncorrectPassword_ThrowsUnauthorized() {
        LoginRequest req = new LoginRequest("farmer@kisanmitra.ai", "WrongPassword");

        User mockUser = new User();
        mockUser.setPassword("correctHash");

        when(userRepository.findByEmail("farmer@kisanmitra.ai")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("WrongPassword", "correctHash")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> authService.login(req));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }
}
