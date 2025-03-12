package com.example.demo.service;

import com.example.demo.model.Challenge;
import org.springframework.stereotype.Service;

import com.example.demo.repository.ChallengeRepository;

import java.util.List;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public ChallengeService(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
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

}
