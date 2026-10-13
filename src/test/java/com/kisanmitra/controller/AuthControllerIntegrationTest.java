package com.kisanmitra.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kisanmitra.dto.LoginRequest;
import com.kisanmitra.dto.RegisterRequest;
import com.kisanmitra.entity.Role;
import com.kisanmitra.entity.User;
import com.kisanmitra.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("1. Register: Successfully registers new farmer with hashed password and returns JWT token")
    void testRegisterFarmerSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "Ramesh Kumar",
                "ramesh.test.integration@kisanmitra.in",
                "9876543210",
                "StrongPassword123",
                Role.FARMER
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fullName").value("Ramesh Kumar"))
                .andExpect(jsonPath("$.email").value("ramesh.test.integration@kisanmitra.in"))
                .andExpect(jsonPath("$.role").value("FARMER"))
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty());

        User savedUser = userRepository.findByEmail("ramesh.test.integration@kisanmitra.in").orElse(null);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getPassword()).isNotEqualTo("StrongPassword123");
        assertThat(passwordEncoder.matches("StrongPassword123", savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("2. Register: Rejects duplicate email registration with 409 Conflict")
    void testRegisterDuplicateEmailReturnsConflict() throws Exception {
        User existing = new User();
        existing.setFullName("Existing Farmer");
        existing.setEmail("existing.farmer@kisanmitra.in");
        existing.setPhone("9123456780");
        existing.setPassword(passwordEncoder.encode("ExistingPassword123"));
        existing.setRole(Role.FARMER);
        userRepository.saveAndFlush(existing);

        RegisterRequest duplicateRequest = new RegisterRequest(
                "Duplicate Farmer",
                "existing.farmer@kisanmitra.in",
                "9988776655",
                "NewPassword123",
                Role.FARMER
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("3. Register: Rejects invalid fields (invalid phone format, short password) with 400 Bad Request")
    void testRegisterValidationFailureReturnsBadRequest() throws Exception {
        RegisterRequest invalidRequest = new RegisterRequest(
                "A", // too short
                "not-an-email",
                "12345", // invalid phone pattern
                "short", // too short password (<8)
                Role.FARMER
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("4. Register: Rejects unauthorized ADMIN privilege escalation via public registration")
    void testRegisterAdminPrivilegeEscalationRejected() throws Exception {
        RegisterRequest adminEscalationRequest = new RegisterRequest(
                "Attacker User",
                "attacker@kisanmitra.in",
                "9876500000",
                "Password123",
                Role.ADMIN
        );

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminEscalationRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("5. Login: Authenticates existing user with valid credentials and returns JWT token")
    void testLoginSuccess() throws Exception {
        User user = new User();
        user.setFullName("Suresh Patel");
        user.setEmail("suresh.login.test@kisanmitra.in");
        user.setPhone("9812345678");
        user.setPassword(passwordEncoder.encode("CorrectPassword123"));
        user.setRole(Role.FARMER);
        userRepository.saveAndFlush(user);

        LoginRequest loginRequest = new LoginRequest("suresh.login.test@kisanmitra.in", "CorrectPassword123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value("suresh.login.test@kisanmitra.in"))
                .andExpect(jsonPath("$.role").value("FARMER"))
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    @DisplayName("6. Login: Rejects incorrect password with 401 Unauthorized")
    void testLoginIncorrectPasswordReturnsUnauthorized() throws Exception {
        User user = new User();
        user.setFullName("Sunita Devi");
        user.setEmail("sunita.login.test@kisanmitra.in");
        user.setPhone("9876512345");
        user.setPassword(passwordEncoder.encode("CorrectPassword123"));
        user.setRole(Role.FARMER);
        userRepository.saveAndFlush(user);

        LoginRequest wrongPasswordRequest = new LoginRequest("sunita.login.test@kisanmitra.in", "WrongPassword999");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("7. Login: Rejects non-existent email with 401 Unauthorized")
    void testLoginNonExistentEmailReturnsUnauthorized() throws Exception {
        LoginRequest unknownUserRequest = new LoginRequest("unknown.farmer@kisanmitra.in", "AnyPassword123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unknownUserRequest)))
                .andExpect(status().isUnauthorized());
    }
}
