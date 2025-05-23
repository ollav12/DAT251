package com.example.demo.controller;

import com.example.demo.model.Challenge;
import com.example.demo.model.User;
import com.example.demo.service.ChallengeService;
import com.example.demo.service.ChallengeStatusService;
import com.example.demo.service.UserServiceImpl;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserServiceImpl userService;
    private final ChallengeStatusService challengeStatusService;
    private final ChallengeService challengeService;

    public AuthController(UserServiceImpl userService, ChallengeService challengeService,
            ChallengeStatusService challengeStatusService) {
        this.userService = userService;
        this.challengeService = challengeService;
        this.challengeStatusService = challengeStatusService;
    }

    public record LoginResponse(
        Long userId,
        String message,
        String equippedBorder,
        String equippedProfilePicture
    ) {}

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
        @RequestBody User user
    ) {
        var newUser = userService.registerUser(user);
        return ResponseEntity.ok(
            Map.of(
                "message", "User registered successfully",
                "userId", newUser.getId()
            )
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
                    Map.of("error", e.getMessage()));
        }
    }
}
