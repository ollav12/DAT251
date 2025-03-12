package com.example.demo.controller;

import com.example.demo.model.Challenge;
import com.example.demo.model.User;
import com.example.demo.model.UserChallenge;
import com.example.demo.service.ChallengeService;
import com.example.demo.service.UserChallengeService;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserChallengeController {

    private final UserChallengeService userChallengeService;
    private final UserService userService;
    private final ChallengeService challengeService;

    public UserChallengeController(UserChallengeService userChallengeService,
                                  UserService userService,
                                  ChallengeService challengeService) {
        this.userChallengeService = userChallengeService;
        this.userService = userService;
        this.challengeService = challengeService;
    }

    @PostMapping("/{userId}/challenges/{challengeId}/start")
    public ResponseEntity<UserChallenge> startChallenge(@PathVariable long userId,
                                                      @PathVariable long challengeId) {
        User user = userService.getUser(userId);
        Challenge challenge = challengeService.getChallenge(challengeId);

        if (user == null || challenge == null) {
            return ResponseEntity.notFound().build();
        }

        UserChallenge userChallenge = userChallengeService.startChallenge(user, challenge);
        return new ResponseEntity<>(userChallenge, HttpStatus.CREATED);
    }

    @PostMapping("/{userId}/challenges/{challengeId}/complete")
    public ResponseEntity<Map<String, Object>> completeChallenge(@PathVariable long userId,
                                                               @PathVariable long challengeId) {
        UserChallenge userChallenge = userChallengeService.getChallenge(challengeId);

        if (userChallenge == null || userChallenge.getUser().getId() != userId) {
            return ResponseEntity.notFound().build();
        }

        UserChallenge completedChallenge = userChallengeService.completeChallenge(userChallenge);

        Map<String, Object> response = new HashMap<>();
        response.put("challenge", completedChallenge);
        response.put("pointsEarned", completedChallenge.getChallenge().getRewardPoints());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/challenges")
    public ResponseEntity<List<UserChallenge>> getUserChallenges(@PathVariable long userId) {
        User user = userService.getUser(userId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userChallengeService.getUserChallenges(user));
    }

    @GetMapping("/{userId}/challenges/completed")
    public ResponseEntity<List<UserChallenge>> getCompletedChallenges(@PathVariable long userId) {
        User user = userService.getUser(userId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userChallengeService.getCompletedChallenges(user));
    }

    @GetMapping("/{userId}/challenges/started")
    public ResponseEntity<List<UserChallenge>> getStartedChallenges(@PathVariable long userId) {
        User user = userService.getUser(userId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userChallengeService.getStartedChallenges(user));
    }

    @GetMapping("/{userId}/challenges/{challengeId}")
    public ResponseEntity<UserChallenge> getUserChallenge(@PathVariable long userId,
                                                        @PathVariable long challengeId) {
        UserChallenge userChallenge = userChallengeService.getChallenge(challengeId);

        if (userChallenge == null || userChallenge.getUser().getId() != userId) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userChallenge);
    }
}