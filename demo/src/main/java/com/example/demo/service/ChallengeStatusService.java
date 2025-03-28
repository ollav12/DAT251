package com.example.demo.service;

import com.example.demo.model.Challenge;
import com.example.demo.model.User;
import com.example.demo.model.ChallengeStatus.Status;
import com.example.demo.model.ChallengeStatus;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.example.demo.repository.ChallengeStatusRepository;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChallengeStatusService {

    private final UserRepository userRepo;
    private final ChallengeStatusRepository challengeStatusRepo;

    public ChallengeStatusService(ChallengeStatusRepository challengeStatusRepo, UserRepository userRepo) {
        this.userRepo = userRepo;
        this.challengeStatusRepo = challengeStatusRepo;
    }

    public ChallengeStatus assignChallenge(long userID, Challenge challenge) {
        ChallengeStatus challengeStatus = new ChallengeStatus();
        challengeStatus.setUserID(userID);
        challengeStatus.setChallenge(challenge);
        challengeStatus.setStatus(ChallengeStatus.Status.NOT_STARTED);
        challengeStatus.setStartedAt(LocalDateTime.now());
        challengeStatusRepo.save(challengeStatus);
        return challengeStatus;
    }

    /*
     * public ChallengeStatus completeChallenge(ChallengeStatus challengeStatus) {
     * challengeStatus.setStatus(ChallengeStatus.Status.COMPLETED);
     * challengeStatus.setCompletedAt(LocalDateTime.now());
     * User user = userRepo.findById(challengeStatus.getUserID()).orElseThrow();
     * user.setPoints(user.getPoints() +
     * challengeStatus.getChallenge().getRewardPoints());
     * userRepo.save(user);
     * return challengeStatusRepo.save(challengeStatus);
     * }
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
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setPoints(user.getPoints() + challenge.getRewardPoints());
        return userRepo.save(user);
    }

    public List<ChallengeStatus> getUserChallenges(long userID) {
        return challengeStatusRepo.findByUserID(userID);
    }

    public List<ChallengeStatus> getCompletedChallenges(long userID) {
        return challengeStatusRepo.findByUserIDAndStatus(userID, ChallengeStatus.Status.COMPLETED);
    }

    public List<ChallengeStatus> getStartedChallenges(long userID) {
        return challengeStatusRepo.findByUserIDAndStatus(userID, ChallengeStatus.Status.IN_PROGRESS);
    }

    public List<ChallengeStatus> getNotStartedChallenges(long userID) {
        return challengeStatusRepo.findByUserIDAndStatus(userID, ChallengeStatus.Status.NOT_STARTED);
    }

    public void deleteChallenge(long id) {
        challengeStatusRepo.deleteById(id);
    }

    public void updateChallenge(long challengeStatusId, ChallengeStatus status) {
        challengeStatusRepo.save(status);
    }

    public ChallengeStatus getChallenge(long id) {
        return challengeStatusRepo.findById(id).orElse(null);
    }

    public ChallengeStatus getChallengeStatus(long userId, long challengeId) {
        return challengeStatusRepo.findByUserIDAndChallenge_ChallengeID(userId, challengeId);
    }

}
