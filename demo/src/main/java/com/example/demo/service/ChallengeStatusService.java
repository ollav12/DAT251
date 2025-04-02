package com.example.demo.service;

import com.example.demo.controller.ChallengeStatusController.MetricUpdate;
import com.example.demo.model.Challenge;
import com.example.demo.model.User;
import com.example.demo.model.ChallengeStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.demo.repository.ChallengeStatusRepository;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChallengeStatusService {

    private final UserServiceImpl userService;
    private final ChallengeStatusRepository challengeStatusRepo;
    private final ChallengeService challengeService;

    public ChallengeStatusService(ChallengeStatusRepository challengeStatusRepo, UserServiceImpl userService,
            ChallengeService challengeService) {
        this.challengeStatusRepo = challengeStatusRepo;
        this.userService = userService;
        this.challengeService = challengeService;
    }

    /**
     * Get all user challenges
     * 
     * @param userID
     * @return
     */
    public List<ChallengeStatus> getAllUserChallenges(long userID) {
        return challengeStatusRepo.findByUserID(userID);
    }

    /**
     * Get a user challenge
     * 
     * @param id
     * @return
     */
    public ChallengeStatus getChallenge(long id) {
        return challengeStatusRepo.findById(id).orElse(null);
    }

    /**
     * Start a challenge by creating new challenge status and save to database
     * 
     * @param userID
     * @param challengeId
     * @return
     */
    public ChallengeStatus startChallenge(long userID, long challengeId) {
        Challenge challenge = challengeService.getChallenge(challengeId);
        ChallengeStatus challengeStatus = new ChallengeStatus();
        challengeStatus.setUserID(userID);
        challengeStatus.setChallenge(challenge);
        challengeStatus.setStatus(ChallengeStatus.Status.NOT_STARTED);
        challengeStatus.setStartedAt(LocalDateTime.now());
        challengeStatusRepo.save(challengeStatus);
        return challengeStatus;
    }

    /**
     * Complete a challenge if requirements are met
     * 
     * @param userId
     * @param challengeId
     * @return
     */
    public User completeChallenge(long userId, long challengeId) {
        // Find the challenge status
        ChallengeStatus status = challengeStatusRepo.findByUserIDAndChallenge_ChallengeID(
                userId, challengeId);

        if (status == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Challenge status not found for user " + userId + " and challenge " + challengeId);
        }

        Challenge challenge = status.getChallenge();
        Challenge.ChallengeType challengeType = challenge.getChallengeType();

        // Check if the challenge metrics are fulfilled based on challenge type
        boolean metricsComplete = false;

        switch (challengeType) {
            case METRIC:
                // For metric challenges, check if current value meets or exceeds target
                metricsComplete = status.getCurrentValue() >= challenge.getTargetValue();
                break;
            case ACTION:
                // For action challenges, check if required actions are performed
                metricsComplete = status.getCurrentValue() >= challenge.getTargetValue() ||
                        status.getStatus() == ChallengeStatus.Status.IN_PROGRESS;
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Unknown challenge type: " + challengeType);
        }

        if (!metricsComplete) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Challenge metrics not yet complete for challenge " + challengeId);
        }

        // Mark as completed and set completion time
        status.setStatus(ChallengeStatus.Status.COMPLETED);
        status.setCompletedAt(LocalDateTime.now());
        challengeStatusRepo.save(status);

        // Award points to user
        User user = userService.getUser(userId);
        user.setPoints(user.getPoints() + challenge.getRewardPoints());
        userService.updateUser(user, userId);
        return user;
    }

    /**
     * Track a user challenge
     * 
     * @param userId
     * @param challengeId
     * @param metricUpdate
     * @return
     */
    public ChallengeStatus trackUserChallenge(Long userId, Long challengeId, MetricUpdate metricUpdate) {
        ChallengeStatus status = challengeStatusRepo.findByUserIDAndChallenge_ChallengeID(
                userId, challengeId);

        Challenge challenge = status.getChallenge();
        if (status.getStatus() == ChallengeStatus.Status.NOT_STARTED) {
            status.setStatus(ChallengeStatus.Status.IN_PROGRESS);
        }
        double newValue = status.getCurrentValue() + metricUpdate.getValue();
        status.setCurrentValue(newValue);

        if (newValue >= challenge.getTargetValue() &&
                status.getStatus() != ChallengeStatus.Status.COMPLETED) {

            // checkChallengeCompleted(userId, status, challenge);
        }

        return challengeStatusRepo.save(status);
    }

    /**
     * Delete a user challenge
     * 
     * @param id
     */
    public void deleteChallenge(long id) {
        challengeStatusRepo.deleteById(id);
    }

    /**
     * Update a user challenge
     * 
     * @param challengeStatusId
     * @param status
     */
    public void updateChallenge(long challengeStatusId, ChallengeStatus status) {
        challengeStatusRepo.save(status);
    }
}
