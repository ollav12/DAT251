package com.example.demo.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "transportation_mode")
public class TransportationMode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String transportationModeName;

    @Column(nullable = false)
    private double co2PerKilometer; // in grams of CO2 per kilometer

    public TransportationMode() {
    }

    public TransportationMode(String transportationModeName, double co2PerKilometer) {
        this.transportationModeName = transportationModeName;
        this.co2PerKilometer = co2PerKilometer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransportationModeName() {
        return transportationModeName;
    }

    public void setTransportationModeName(String transportationModeName) {
        this.transportationModeName = transportationModeName;
    }

    public double getCo2PerKilometer() {
        return co2PerKilometer;
    }

    public void setCo2PerKilometer(double co2PerKilometer) {
        this.co2PerKilometer = co2PerKilometer;
    }

    @Override
    public String toString() {
        return "TransportationMode{" +
                "id=" + id +
                ", modeName='" + transportationModeName + '\'' +
                ", co2PerKilometer=" + co2PerKilometer +
                '}';
    }
}
