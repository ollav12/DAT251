package com.example.demo.controller;

import com.example.demo.model.Challenge;
import com.example.demo.model.ChallengeStatus;
import com.example.demo.model.User;
import com.example.demo.repository.ChallengeStatusRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ChallengeService;
import com.example.demo.service.ChallengeStatusService;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/challenges")
public class ChallengeController {

    private final ChallengeService challengeService;
    private final ChallengeStatusService challengeStatusService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ChallengeStatusRepository challengeStatusRepository;

    public ChallengeController(ChallengeService challengeService, ChallengeStatusService challengeStatusService, UserService userService, UserRepository userRepository, ChallengeStatusRepository challengeStatusRepository) {
        this.challengeService = challengeService;
        this.challengeStatusService = challengeStatusService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.challengeStatusRepository = challengeStatusRepository;
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

    @PostMapping("/users/{userId}/challenges/{challengeId}/metrics")
    public ResponseEntity<?> updateMetricsForChallenge(
            @PathVariable Long userId,
            @PathVariable Long challengeId,
            @RequestBody MetricUpdate metricUpdate) {

        // Find the specific challenge status
        ChallengeStatus status = challengeStatusRepository.findByUserIDAndChallenge_ChallengeID(
                userId, challengeId);

        if (status == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Challenge status not found"));
        }

        Challenge challenge = status.getChallenge();

        // Verify this challenge matches the metric type
        if (challenge.getChallengeType() != Challenge.ChallengeType.METRIC ||
                !challenge.getMetricUnit().equals(metricUpdate.getMetricType())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Metric type doesn't match this challenge"));
        }

        // Update status to IN_PROGRESS if it was NOT_STARTED
        if (status.getStatus() == ChallengeStatus.Status.NOT_STARTED) {
            status.setStatus(ChallengeStatus.Status.IN_PROGRESS);
        }

        // Update metric value
        double newValue = status.getCurrentValue() + metricUpdate.getValue();
        status.setCurrentValue(newValue);

        // Check if challenge is completed
        if (newValue >= challenge.getTargetValue() &&
                status.getStatus() != ChallengeStatus.Status.COMPLETED) {

            checkChallengeCompleted(userId, status, challenge);
        }

        challengeStatusRepository.save(status);

        return ResponseEntity.ok().body(Map.of(
                "message", "Metrics updated successfully",
                "currentValue", status.getCurrentValue(),
                "status", status.getStatus().toString()));
    }

    @PostMapping("/users/{userId}/challenges/{challengeId}/actions")
    public ResponseEntity<?> recordAction(@PathVariable Long userId, @PathVariable Long challengeId) {
        ChallengeStatus status = challengeStatusRepository.findByUserIDAndChallenge_ChallengeID(userId, challengeId);

        if (status == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Challenge status not found"));
        }

        Challenge challenge = status.getChallenge();
        if (challenge.getChallengeType() != Challenge.ChallengeType.ACTION) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Not an ACTION challenge"));
        }

        // Update status to IN_PROGRESS if it was NOT_STARTED
        if (status.getStatus() == ChallengeStatus.Status.NOT_STARTED) {
            status.setStatus(ChallengeStatus.Status.IN_PROGRESS);
        }

        // Increment action count
        double newValue = status.getCurrentValue() + 1;
        status.setCurrentValue(newValue);

        // Check if challenge is completed
        if (newValue >= challenge.getTargetValue() &&
                status.getStatus() != ChallengeStatus.Status.COMPLETED) {
            checkChallengeCompleted(userId, status, challenge);
        }

        challengeStatusRepository.save(status);
        return ResponseEntity.ok().body(Map.of(
                "message", "Action recorded",
                "currentActions", status.getCurrentValue(),
                "status", status.getStatus().toString()));
    }

    private void checkChallengeCompleted(Long userId, ChallengeStatus status, Challenge challenge) {
        status.setStatus(ChallengeStatus.Status.COMPLETED);
        status.setCompletedAt(LocalDateTime.now());

        // Award points
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setPoints(user.getPoints() + challenge.getRewardPoints());
            userRepository.save(user);
        }
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