package com.example.demo.controller;

import com.example.demo.model.Challenge;
import com.example.demo.model.ChallengeStatus;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ChallengeService;
import com.example.demo.service.ChallengeStatusService;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final ChallengeStatusService challengeStatusService;
    private final UserService userService;
    private final UserRepository userRepository;

    public ChallengeController(ChallengeService challengeService, ChallengeStatusService challengeStatusService, UserService userService, UserRepository userRepository) {
        this.challengeService = challengeService;
        this.challengeStatusService = challengeStatusService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Challenge> createChallenge(@RequestBody Challenge challenge) {
        challengeService.addChallenge(challenge);
        return new ResponseEntity<>(challenge, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Challenge>> getAllChallenges() {
        return ResponseEntity.ok(challengeService.getAllChallenges());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Challenge> getChallengeById(@PathVariable long id) {
        Challenge challenge = challengeService.getChallenge(id);
        if (challenge != null) {
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Challenge> updateChallenge(@PathVariable long id, @RequestBody Challenge challenge) {
        challenge.setChallengeID(id);
        challengeService.updateChallenge(challenge);
        return ResponseEntity.ok(challenge);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable long id) {
        challengeService.deleteChallenge(id);
        return ResponseEntity.noContent().build();
    }

    // New endpoint for recording user navigation
    @PostMapping("/users/{userId}/navigation")
    public ResponseEntity<Void> recordNavigation(@PathVariable long userId, @RequestBody NavigationRequest request) {
        challengeService.recordNavigation(userId, request.getRoute());
        return ResponseEntity.ok().build();
    }

    // New endpoint for updating metrics
    @PostMapping("/users/{userId}/metrics")
    public ResponseEntity<Void> updateMetric(@PathVariable long userId, @RequestBody MetricUpdate update) {
        challengeService.updateMetricProgress(userId, update.getMetricType(), update.getValue());
        return ResponseEntity.ok().build();
    }
    

    // Request DTOs
    public static class NavigationRequest {
        private String route;

        public String getRoute() {
            return route;
        }

        public void setRoute(String route) {
            this.route = route;
        }
    }

    public static class MetricUpdate {
        private String metricType;
        private double value;

        public MetricUpdate(String metricType, double value) {
            this.metricType = metricType;
            this.value = value;
        }

        public String getMetricType() {
            return metricType;
        }

        public void setMetricType(String metricType) {
            this.metricType = metricType;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }
}