package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.repository.ChallengeRepository;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public ChallengeService(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

}
