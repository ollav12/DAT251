package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "challenge")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long challengeID;

    private String challengeTitle;
    private String description;
    private int rewardPoints;
    private int duration; // days

    @Enumerated(EnumType.STRING)
    private ChallengeType challengeType;

    private double targetValue;
    private String metricUnit;

    public enum ChallengeType {
        METRIC,
        ACTION
    }

    public Challenge(String challengeTitle, String description, int rewardPoints, int duration, ChallengeType type, double targetValue, String metricUnit) {
        this.challengeTitle = challengeTitle;
        this.description = description;
        this.rewardPoints = rewardPoints;
        this.duration = duration;
        this.challengeType = type;
        this.targetValue = targetValue;
        this.metricUnit = metricUnit;
    }

    public Challenge() {

    }

    public long getChallengeID() {
        return challengeID;
    }

    public void setChallengeID(long challengeID) {
        this.challengeID = challengeID;
    }

    public String getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public ChallengeType getChallengeType() {
        return challengeType;
    }

    public void setChallengeType(ChallengeType challengeType) {
        this.challengeType = challengeType;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    public String getMetricUnit() {
        return metricUnit;
    }

    public void setMetricUnit(String metricUnit) {
        this.metricUnit = metricUnit;
    }

}
