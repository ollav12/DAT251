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

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @RequestBody User user) {
        userService.registerUser(user);
        List<Challenge> challenges = challengeService.getAllChallenges();
        for (Challenge challenge : challenges) {
            challengeStatusService.assignChallenge(user.getId(), challenge);
        }
        return ResponseEntity.ok(
                Map.of("message", "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            userService.loginUser(user.getUsername(), user.getPassword());
            long userId = userService.getUserId(user.getUsername());
            return ResponseEntity.ok(userId);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                    Map.of("error", e.getMessage()));
        }
    }
}
