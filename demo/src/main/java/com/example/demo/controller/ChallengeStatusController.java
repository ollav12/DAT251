package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.ChallengeStatus;
import com.example.demo.service.ChallengeStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class ChallengeStatusController {

    private final ChallengeStatusService challengeStatusService;

    public ChallengeStatusController(ChallengeStatusService challengeStatusService) {
        this.challengeStatusService = challengeStatusService;
    }

    /**
     * Get all user challenges
     * 
     * @param userId
     * @return List of Challenge status
     */
    @GetMapping("/{userId}/challenges")
    public ResponseEntity<List<ChallengeStatus>> getAllChallenges(@PathVariable long userId) {
        return ResponseEntity.ok(challengeStatusService.getAllUserChallenges(userId));
    }

    /**
     * Get a user challenge
     * 
     * @param userId
     * @param challengeId
     * @return userchallenge
     */
    @GetMapping("/{userId}/challenges/{challengeId}")
    public ResponseEntity<ChallengeStatus> getUserChallenge(@PathVariable long userId,
            @PathVariable long challengeId) {
        ChallengeStatus userChallenge = challengeStatusService.getChallenge(challengeId);

        if (userChallenge == null || userChallenge.getUserID() != userId) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userChallenge);
    }

    /**
     * Start a challenge
     * 
     * @param userId
     * @param challengeId
     * @return Challenge id
     */
    @PostMapping("/{userId}/challenges/{challengeId}/start")
    public ResponseEntity<ChallengeStatus> startChallenge(@PathVariable long userId,
            @PathVariable long challengeId) {
        ChallengeStatus challengeStatus = challengeStatusService.startChallenge(userId, challengeId);
        return new ResponseEntity<>(challengeStatus, HttpStatus.CREATED);
    }

    /**
     * Complete a challenge
     * 
     * @param userId
     * @param challengeId
     * @return Updated user
     */
    @PostMapping("/{userId}/challenges/{challengeId}/complete")
    public ResponseEntity<User> completeChallenge(@PathVariable long userId, @PathVariable long challengeId) {
        User updatedUser = challengeStatusService.completeChallenge(userId, challengeId);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Track a user challenge
     * 
     * @param userId
     * @param challengeId
     * @param metricUpdate
     * @return Status message
     */
    @PostMapping("/users/{userId}/challenges/{challengeId}/track]")
    public ResponseEntity<?> trackUserChallenge(
            @PathVariable Long userId,
            @PathVariable Long challengeId,
            @RequestBody MetricUpdate metricUpdate) {
        ChallengeStatus status = challengeStatusService.trackUserChallenge(userId, challengeId, metricUpdate);
        return ResponseEntity.ok().body(Map.of(
                "message", "Challenge Tracked",
                "currentValue", status.getCurrentValue(),
                "status", status.getStatus().toString()));
    }

    /**
     * MetricUpdate DTO
     */
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