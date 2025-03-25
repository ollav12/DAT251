package com.example.demo.service;

import com.example.demo.model.Challenge;
import com.example.demo.model.ChallengeStatus;
import com.example.demo.repository.ChallengeStatusRepository;
import org.springframework.stereotype.Service;

import com.example.demo.repository.ChallengeRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    private final ChallengeStatusRepository challengeStatusRepository;

    private final UserService userService;

    public ChallengeService(ChallengeRepository challengeRepository, ChallengeStatusRepository challengeStatusRepository, UserService userService) {
        this.challengeRepository = challengeRepository;
        this.challengeStatusRepository = challengeStatusRepository;
        this.userService = userService;
    }

    public void addChallenge(Challenge challenge) {
        challengeRepository.save(challenge);
    }

    public void deleteChallenge(long id) {
        challengeRepository.deleteById(id);
    }

    public void updateChallenge(Challenge challenge) {
        challengeRepository.save(challenge);
    }

    public Challenge getChallenge(long id) {
        return challengeRepository.findById(id).orElse(null);
    }

    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    public Challenge createChallenge(Challenge challenge) {
        // Set default values based on challenge type
        switch (challenge.getChallengeType()) {
            case METRIC:
                if (challenge.getTargetValue() <= 0) {
                    throw new IllegalArgumentException("Metric challenges require a positive target value");
                }
                if (challenge.getMetricUnit() == null) {
                    throw new IllegalArgumentException("Metric challenges require a unit of measurement");
                }
                break;
            case ACTION:
                if (challenge.getTargetValue() <= 0) {
                    throw new IllegalArgumentException("Action challenges require a positive target value");
                }
                break;
        }
        return challenge;
    }

    public void updateMetricProgress(long userId, String metricType, double value) {
        List<ChallengeStatus> statuses = challengeStatusRepository.findByUserIDAndStatus(userId, ChallengeStatus.Status.IN_PROGRESS);

        for (ChallengeStatus status : statuses) {
            Challenge challenge = status.getChallenge();
            if (challenge.getChallengeType() == Challenge.ChallengeType.METRIC &&
                metricType.equals(challenge.getMetricUnit())) {

                // Update progress
                status.setCurrentValue(status.getCurrentValue() + value);

                // Add checkpoint
                if (status.getCheckpoints() == null) {
                    status.setCheckpoints(new ArrayList<>());
                }
                status.getCheckpoints().add(LocalDateTime.now());

                // Check for completion
                if (status.getCurrentValue() >= challenge.getTargetValue()) {
                    status.setStatus(ChallengeStatus.Status.COMPLETED);
                    status.setCompletedAt(LocalDateTime.now());

                    // Update user points
                    userService.updateUserPoints(userId, challenge.getRewardPoints());
                }

                challengeStatusRepository.save(status);
            }
        }
    }
}
