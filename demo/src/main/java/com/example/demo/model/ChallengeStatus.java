package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "challenge_status")
public class ChallengeStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long challengeStatusId;

    private long userID;
    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    public enum Status {
        NOT_STARTED,
        STARTED,
        COMPLETED
    }

    public ChallengeStatus(long userID, Challenge challenge) {
        this.userID = userID;
        this.challenge = challenge;
        this.status = Status.NOT_STARTED;
    }

    public ChallengeStatus() {

    }

    public long getUserID() {
        return userID;
    }

    public void setUser(long userID) {
        this.userID = userID;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public long getChallengeStatusId() {
        return challengeStatusId;
    }

    public void setChallengeStatusId(long challengeStatusId) {
        this.challengeStatusId = challengeStatusId;
    }
}
