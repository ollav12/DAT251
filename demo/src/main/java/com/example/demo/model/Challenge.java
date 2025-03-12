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
    private String challengeTitle;
    private String description;
    private int rewardPoints;
    private int duration; // hours

    public Challenge(String ChallengeTitle, String description, int rewardPoints, int duration) {
        this.challengeTitle = ChallengeTitle;
        this.description = description;
        this.rewardPoints = rewardPoints;
        this.duration = duration;
    }

    public Challenge() {

    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
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
