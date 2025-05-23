package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.ChallengeStatus;

import java.util.List;

@Repository
public interface ChallengeStatusRepository extends JpaRepository<ChallengeStatus, Long> {
    List<ChallengeStatus> findByUserID(long userID);

    List<ChallengeStatus> findByUserIDAndStatus(long userID, ChallengeStatus.Status status);

    ChallengeStatus findByUserIDAndChallenge_ChallengeID(long userID, long challengeID);
}
