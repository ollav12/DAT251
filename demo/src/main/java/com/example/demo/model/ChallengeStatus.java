package com.example.demo.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

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

    private double currentValue;
    private int actionsCompleted;
    private int currentStreak;
    private LocalDateTime lastActionDate;

    @ElementCollection
    private List<LocalDateTime> checkpoints;

    public enum Status {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }

    public ChallengeStatus(long userID, Challenge challenge, Status status, LocalDateTime startedAt,
            LocalDateTime completedAt, double currentValue, int actionsCompleted, int currentStreak,
            LocalDateTime lastActionDate, List<LocalDateTime> checkpoints) {
        this.userID = userID;
        this.challenge = challenge;
        this.status = status;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.currentValue = currentValue;
        this.actionsCompleted = actionsCompleted;
        this.currentStreak = currentStreak;
        this.lastActionDate = lastActionDate;
        this.checkpoints = checkpoints;
    }

    public ChallengeStatus() {

    }

    public long getChallengeStatusId() {
        return challengeStatusId;
    }

    public void setChallengeStatusId(long challengeStatusId) {
        this.challengeStatusId = challengeStatusId;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
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

    public double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public int getActionsCompleted() {
        return actionsCompleted;
    }

    public void setActionsCompleted(int actionsCompleted) {
        this.actionsCompleted = actionsCompleted;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public LocalDateTime getLastActionDate() {
        return lastActionDate;
    }

    public void setLastActionDate(LocalDateTime lastActionDate) {
        this.lastActionDate = lastActionDate;
    }

    public List<LocalDateTime> getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoints(List<LocalDateTime> checkpoints) {
        this.checkpoints = checkpoints;
    }
}
