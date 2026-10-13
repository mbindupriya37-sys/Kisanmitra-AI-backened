package com.kisanmitra.controller;

import com.kisanmitra.dto.UserResponse;
import com.kisanmitra.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        UserResponse response = UserResponse.fromEntity(userDetails.getUser(), "Profile retrieved successfully");
        return ResponseEntity.ok(response);
    }
}
