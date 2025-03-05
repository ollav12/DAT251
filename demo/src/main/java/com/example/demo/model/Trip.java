package com.example.demo.model;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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
    private User user;

    private String origin;
    private String destination;

    private String travelMode;

    private Double totalDistanceKm;
    private Double totalDurationSeconds;
    private Double totalEmissionsCO2eKg;
    private Double savedEmissionsCO2eKg;

    // TODO: add vehicle

    // TODO: only storing travel mode string for now
    @ManyToOne
    private TransportationMode transportationMode;

    @Embedded
    private MoneySaved moneySaved;

    // TODO: we should store trip alternatives, only storing saved against worst alternative for now

    public Trip() {}

    public Trip(
        User user,
        String origin,
        String destination,
        String travelMode,
        double totalDistanceKm,
        double totalDurationSeconds,
        double totalEmissionsCO2eKg,
        double savedEmissionsCO2eKg
    ) {
        this.user = user;
        this.origin = origin;
        this.destination = destination;
        this.travelMode = travelMode;
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
}
