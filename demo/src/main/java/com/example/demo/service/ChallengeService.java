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

    /**
     * Get all challenges from database
     * 
     * @return List of challenges
     */
    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    /**
     * Get a challenge from database
     * 
     * @param id
     * @return challenge
     */
    public Challenge getChallenge(long id) {
        return challengeRepository.findById(id).orElseThrow(null);
    }

    /**
     * Store a challenge in database
     *
     * @param challenge
     * @return
     */
    public Challenge addChallenge(Challenge challenge) {
        challengeRepository.save(challenge);
        return challenge;
    }

    /**
     * Update challenge and save in database
     * 
     * @param id
     * @param challenge
     * @return Challenge
     */
    public Challenge updateChallenge(long id, Challenge challenge) {
        challenge.setChallengeID(id);
        return challengeRepository.save(challenge);
    }

    /**
     * Delte challenge if it exits
     * 
     * @param id
     * @return Boolean
     */
    public boolean deleteChallenge(long id) {
        if (challengeRepository.existsById(id)) {
            challengeRepository.deleteById(id);
            return true; // Successfully deleted
        }
        return false; // Challenge not found
    }
}
