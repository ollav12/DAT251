package com.example.demo.service;

import com.example.demo.model.Challenge;
import com.example.demo.model.User;
import com.example.demo.model.ChallengeStatus;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import com.example.demo.repository.ChallengeStatusRepository;

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

    public ChallengeStatus startChallenge(long userID, Challenge Challenge) {
        ChallengeStatus challengeStatus = new ChallengeStatus(userID, Challenge);
        challengeStatusRepo.save(challengeStatus);
        return challengeStatus;
    }

    public ChallengeStatus completeChallenge(ChallengeStatus challengeStatus) {
        challengeStatus.setStatus(ChallengeStatus.Status.COMPLETED);
        challengeStatus.setCompletedAt(LocalDateTime.now());
        User user = userRepo.findById(challengeStatus.getUserID()).orElseThrow();
        user.setPoints(user.getPoints() + challengeStatus.getChallenge().getRewardPoints());
        userRepo.save(user);
        return challengeStatusRepo.save(challengeStatus);
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

    public void updateChallenge(long challengeStatusId, ChallengeStatus challengeStatus) {
        ChallengeStatus temp = challengeStatusRepo.findById(challengeStatusId).orElseThrow();
        temp.setCompletedAt(challengeStatus.getCompletedAt());
        temp.setStartedAt(challengeStatus.getStartedAt());
        temp.setStatus(challengeStatus.getStatus());
        challengeStatusRepo.save(temp);
    }

    public ChallengeStatus getChallenge(long id) {
        return challengeStatusRepo.findById(id).orElse(null);
    }
}
