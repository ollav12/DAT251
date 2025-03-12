package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "challenge")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long challengeID;
    private String description;
    private int rewardPoints;
    private int duration; // hours

    public Challenge(long challengeID, String description, int rewardPoints, int duration) {
        this.challengeID = challengeID;
        this.description = description;
        this.rewardPoints = rewardPoints;
        this.duration = duration;
    }

    public long getChallengeID() {
        return challengeID;
    }

    public String getDescription() {
        return description;
    }

    public int getDuration() {
        return duration;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setChallengeID(long challengeID) {
        this.challengeID = challengeID;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
}
