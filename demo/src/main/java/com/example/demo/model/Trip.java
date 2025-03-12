package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "trips")
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JsonIgnore
    private User user;

    private String origin;
    private String destination;

    private String travelMode;

    private Double totalDistanceKm;
    private Double totalDurationSeconds;
    private Double totalEmissionsCO2eKg;
    private Double savedEmissionsCO2eKg;

    @ManyToOne
    private Vehicle vehicle;

    // TODO: only storing travel mode string for now
    @ManyToOne
    private TransportationMode transportationMode;

    @Embedded
    private MoneySaved moneySaved;

    // TODO: we should store trip alternatives, only storing saved against worst
    // alternative for now

    public Trip() {
    }

    public Trip(
            User user,
            String origin,
            String destination,
            String travelMode,
            Vehicle vehicle,
            double totalDistanceKm,
            double totalDurationSeconds,
            double totalEmissionsCO2eKg,
            double savedEmissionsCO2eKg) {
        this.user = user;
        this.origin = origin;
        this.destination = destination;
        this.travelMode = travelMode;
        this.vehicle = vehicle;
        this.totalDistanceKm = totalDistanceKm;
        this.totalDurationSeconds = totalDurationSeconds;
        this.totalEmissionsCO2eKg = totalEmissionsCO2eKg;
        this.savedEmissionsCO2eKg = savedEmissionsCO2eKg;
    }

    public User getUser() {
        return this.user;
    }

    public long getId() {
        return id;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public Double getTotalDurationSeconds() {
        return totalDurationSeconds;
    }

    public Double getTotalEmissionsCO2eKg() {
        return totalEmissionsCO2eKg;
    }

    public Double getSavedEmissionsCO2eKg() {
        return savedEmissionsCO2eKg;
    }

    public TransportationMode getTransportationMode() {
        return transportationMode;
    }

    public MoneySaved getMoneySaved() {
        return this.moneySaved;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setTravelMode(String travelMode) {
        this.travelMode = travelMode;
    }

    public void setTotalDistanceKm(Double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    public void setTotalDurationSeconds(Double totalDurationSeconds) {
        this.totalDurationSeconds = totalDurationSeconds;
    }

    public void setTotalEmissionsCO2eKg(Double totalEmissionsCO2eKg) {
        this.totalEmissionsCO2eKg = totalEmissionsCO2eKg;
    }

    public void setSavedEmissionsCO2eKg(Double savedEmissionsCO2eKg) {
        this.savedEmissionsCO2eKg = savedEmissionsCO2eKg;
    }

    public void setTransportationMode(TransportationMode transportationMode) {
        this.transportationMode = transportationMode;
    }

    public void setMoneySaved(MoneySaved moneySaved) {
        this.moneySaved = moneySaved;
    }

}
