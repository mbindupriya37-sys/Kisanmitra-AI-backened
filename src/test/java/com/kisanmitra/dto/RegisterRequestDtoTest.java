package com.kisanmitra.dto;

import com.kisanmitra.entity.Role;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RegisterRequestDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private RegisterRequest createValidRequest() {
        return new RegisterRequest(
                "Ramesh Kumar", "ramesh@kisanmitra.ai", "9876543210", "Password123", Role.FARMER
        );
    }

    @Test
    @DisplayName("Valid RegisterRequest passes validation")
    void validRegisterRequest_PassesValidation() {
        RegisterRequest req = createValidRequest();
        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertTrue(violations.isEmpty(), "Valid request must have no violations");
        assertTrue(req.isValidRole());
    }

    @Test
    @DisplayName("Invalid RegisterRequest when fullName is blank or too short")
    void invalidFullName_FailsValidation() {
        RegisterRequest req = createValidRequest();
        req.setFullName("R");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fullName")));
    }

    @Test
    @DisplayName("Invalid RegisterRequest when email format is invalid")
    void invalidEmailFormat_FailsValidation() {
        RegisterRequest req = createValidRequest();
        req.setEmail("not-an-email");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Invalid RegisterRequest when phone number is invalid for Indian mobile numbers")
    void invalidPhoneNumber_FailsValidation() {
        RegisterRequest req = createValidRequest();
        req.setPhone("1234567890"); // Starts with 1 instead of 6-9

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("phone")));

        req.setPhone("98765"); // Too short
        violations = validator.validate(req);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Invalid RegisterRequest when password is less than 8 characters")
    void shortPassword_FailsValidation() {
        RegisterRequest req = createValidRequest();
        req.setPassword("short");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Invalid RegisterRequest when role is ADMIN")
    void adminRole_FailsCustomValidation() {
        RegisterRequest req = createValidRequest();
        req.setRole(Role.ADMIN);

        assertFalse(req.isValidRole(), "Role ADMIN should not be valid for public registration");
    }
}
