package com.example.demo.controller;

import com.example.demo.model.Challenge;
import com.example.demo.model.User;
import com.example.demo.model.ChallengeStatus.Status;
import com.example.demo.model.ChallengeStatus;
import com.example.demo.service.ChallengeService;
import com.example.demo.service.ChallengeStatusService;
import com.example.demo.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class ChallengeStatusController {

    private final ChallengeStatusService challengeStatusService;
    private final ChallengeService challengeService;
    private final UserService userService;

    public ChallengeStatusController(ChallengeStatusService challengeStatusService, UserService userService,
            ChallengeService challengeService) {
        this.challengeStatusService = challengeStatusService;
        this.userService = userService;
        this.challengeService = challengeService;
    }

    @PostMapping("/{userId}/challenges/{challengeId}/start")
    public ResponseEntity<ChallengeStatus> startChallenge(@PathVariable long userId,
            @PathVariable long challengeId) {
        User user = userService.getUser(userId);
        Challenge challenge = challengeService.getChallenge(challengeId);

        if (user == null || challenge == null) {
            return ResponseEntity.notFound().build();
        }

        ChallengeStatus challengeStatus = challengeStatusService.startChallenge(user.getId(), challenge);
        return new ResponseEntity<>(challengeStatus, HttpStatus.CREATED);
    }

    @PostMapping("/{userId}/challenges/{challengeId}/complete")
    public ResponseEntity<Map<String, Object>> completeChallenge(@PathVariable long userId,
            @PathVariable long challengeId) {
        ChallengeStatus challengeStatus = challengeStatusService.getChallenge(challengeId);
        Challenge challenge = challengeService.getChallenge(challengeId);

        if (challenge == null || challengeStatus.getStatus() == Status.COMPLETED) { // Check that challenge exists and
            return ResponseEntity.notFound().build();
        }
        challengeStatusService.completeChallenge(challengeStatus);

        Map<String, Object> response = new HashMap<>();
        response.put("challenge", challenge);
        response.put("pointsEarned", challenge.getRewardPoints());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/challenges")
    public ResponseEntity<List<ChallengeStatus>> getUserChallenges(@PathVariable long userId) {
        User user = userService.getUser(userId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(challengeStatusService.getUserChallenges(userId));
    }

    @GetMapping("/{userId}/challenges/completed")
    public ResponseEntity<List<ChallengeStatus>> getCompletedChallenges(@PathVariable long userId) {
        User user = userService.getUser(userId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        List<ChallengeStatus> startedChallenges = challengeStatusService.getCompletedChallenges(userId);
        List<Challenge> challenges = challengeService.getAllChallenges();
        System.out.println(startedChallenges);
        for (Challenge challenge : challenges) {

            for (ChallengeStatus startedChallenge : startedChallenges) {
                if (startedChallenge.getChallenge().getChallengeID() == challenge.getChallengeID()) {
                    LocalDateTime completedAt = startedChallenge.getCompletedAt();
                    int duration = challenge.getDuration();
                    LocalDateTime challengeResetDate = completedAt.plusDays(duration);
                    if (LocalDateTime.now().isAfter(challengeResetDate)) {
                        startedChallenge.setStatus(ChallengeStatus.Status.NOT_STARTED);
                        challengeStatusService.updateChallenge(startedChallenge.getChallengeStatusId(),
                                startedChallenge);
                    }
                }
            }
        }
        return ResponseEntity.ok(challengeStatusService.getCompletedChallenges(userId));

    }

    @GetMapping("/{userId}/challenges/started")
    public ResponseEntity<List<ChallengeStatus>> getStartedChallenges(@PathVariable long userId) {
        User user = userService.getUser(userId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        List<ChallengeStatus> startedChallenges = challengeStatusService.getStartedChallenges(userId);
        List<Challenge> challenges = challengeService.getAllChallenges();
        System.out.println(startedChallenges);
        for (Challenge challenge : challenges) { // Checks if new challenges are created and adds them to the user when
                                                 // user checks challenges tab
            boolean isStarted = startedChallenges.stream()
                    .anyMatch(status -> status.getChallenge().getChallengeID() == challenge.getChallengeID());
            if (!isStarted) {
                challengeStatusService.startChallenge(userId, challenge);
            }
        }
        return ResponseEntity.ok(challengeStatusService.getStartedChallenges(userId));
    }

    @GetMapping("/{userId}/challenges/{challengeId}")
    public ResponseEntity<ChallengeStatus> getUserChallenge(@PathVariable long userId,
            @PathVariable long challengeId) {
        ChallengeStatus userChallenge = challengeStatusService.getChallenge(challengeId);

        if (userChallenge == null || userChallenge.getUserID() != userId) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userChallenge);
    }
}