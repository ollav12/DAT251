package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.repository.ChallengeCompletedRepository;

@Service
public class CompletedChallengeService {

    private final ChallengeCompletedRepository challengeCompletedRepository;

    public CompletedChallengeService(ChallengeCompletedRepository challengeCompletedRepository) {
        this.challengeCompletedRepository = challengeCompletedRepository;
    }
}
