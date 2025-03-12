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

@Entity
@Table(name = "completed_challenge")
public class CompletedChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long challengeID;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userID;
    private int completedAt; // Should be date type

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        NOT_STARTED,
        STARTED,
        COMPLETED
    }

    public CompletedChallenge(long challengeID, User userID, int completedAt, Status status) {
        this.challengeID = challengeID;
        this.userID = userID;
        this.completedAt = completedAt;
        this.status = status;
    }

    public long getChallengeID() {
        return challengeID;
    }

    public int getCompletedAt() {
        return completedAt;
    }

    public User getUserID() {
        return userID;
    }

    public void setChallengeID(long challengeID) {
        this.challengeID = challengeID;
    }

    public void setCompletedAt(int completedAt) {
        this.completedAt = completedAt;
    }

    public void setUserID(User userID) {
        this.userID = userID;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
