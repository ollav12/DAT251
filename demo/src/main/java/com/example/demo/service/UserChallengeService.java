package com.example.demo.service;

import com.example.demo.model.Challenge;
import com.example.demo.model.User;
import com.example.demo.model.UserChallenge;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import com.example.demo.repository.UserChallengeRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserChallengeService {

    private final UserChallengeRepository userChallengeRepo;

    private final UserRepository userRepo;
    private final UserChallengeRepository userChallengeRepository;

    public UserChallengeService(UserChallengeRepository challengeCompletedRepository, UserRepository userRepo, UserChallengeRepository userChallengeRepository) {
        this.userChallengeRepo = challengeCompletedRepository;
        this.userRepo = userRepo;
        this.userChallengeRepository = userChallengeRepository;
    }

    public UserChallenge startChallenge(User user, Challenge Challenge) {
        UserChallenge userChallenge = new UserChallenge(user, Challenge);
        userChallengeRepo.save(userChallenge);
        return userChallenge;
    }

    public UserChallenge completeChallenge(UserChallenge userChallenge) {
        userChallenge.setStatus(UserChallenge.Status.COMPLETED);
        userChallenge.setCompletedAt(LocalDateTime.now());
        userChallenge.getUser().setPoints(userChallenge.getUser().getPoints() + userChallenge.getChallenge().getRewardPoints());
        userRepo.save(userChallenge.getUser());
        return userChallengeRepository.save(userChallenge);
    }

    public List<UserChallenge> getUserChallenges(User user) {
        return userChallengeRepo.findByUser(user);
    }

    public List<UserChallenge> getCompletedChallenges(User user) {
        return userChallengeRepo.findByUserAndStatus(user, UserChallenge.Status.COMPLETED);
    }

    public List<UserChallenge> getStartedChallenges(User user) {
        return userChallengeRepo.findByUserAndStatus(user, UserChallenge.Status.STARTED);
    }

    public List<UserChallenge> getNotStartedChallenges(User user) {
        return userChallengeRepo.findByUserAndStatus(user, UserChallenge.Status.NOT_STARTED);
    }

    public void deleteChallenge(long id) {
        userChallengeRepo.deleteById(id);
    }

    public void updateChallenge(UserChallenge userChallenge) {
        userChallengeRepo.save(userChallenge);
    }

    public UserChallenge getChallenge(long id) {
        return userChallengeRepo.findById(id).orElse(null);
    }
}
