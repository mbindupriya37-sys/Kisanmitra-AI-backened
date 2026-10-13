package com.kisanmitra.service;

import com.kisanmitra.dto.RegisterRequest;
import com.kisanmitra.dto.UserResponse;
import com.kisanmitra.entity.User;
import com.kisanmitra.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.kisanmitra.dto.LoginRequest;

import com.kisanmitra.security.JwtUtils;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already registered");
        }

        if (request.getRole() == com.kisanmitra.entity.Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Public registration cannot grant ADMIN privileges.");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : com.kisanmitra.entity.Role.FARMER);

        User savedUser = userRepository.save(user);
        String token = jwtUtils.generateTokenFromEmail(savedUser.getEmail());

        return UserResponse.fromEntity(savedUser, token, "User registered successfully");
    }

    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        String token = jwtUtils.generateTokenFromEmail(user.getEmail());

        return UserResponse.fromEntity(user, token, "Login successful");
    }
}
