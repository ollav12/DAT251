package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserServiceImpl;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserServiceImpl userService;

    public AuthController(UserServiceImpl userService) {
        this.userService = userService;
    }

    public record LoginResponse(
        Long userId,
        String message,
        String equippedBorder,
        String equippedProfilePicture
    ) {}

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
        @RequestBody User user
    ) {
        userService.registerUser(user);
        return ResponseEntity.ok(
            Map.of("message", "User registered successfully")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            User loggedInUser = userService.loginUser(
                user.getUsername(),
                user.getPassword()
            );
            return ResponseEntity.ok(
                new LoginResponse(
                    loggedInUser.getId(),
                    "User logged in successfully",
                    loggedInUser.getEquippedBorder() != null
                        ? loggedInUser.getEquippedBorder().getImage()
                        : "",
                    loggedInUser.getEquippedProfilePicture() != null
                        ? loggedInUser.getEquippedProfilePicture().getImage()
                        : ""
                )
            );
        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                Map.of("error", e.getMessage())
            );
        }
    }
}
