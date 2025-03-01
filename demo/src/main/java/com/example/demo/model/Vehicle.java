package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @JsonIgnore
    private User owner;

    private String make;
    private String model;
    private int year;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleType type;

    private double emissionsCO2ePerKm;

    public Vehicle() {}

    public Vehicle(
        String make,
        String model,
        int year,
        VehicleType type,
        double emissionsCO2ePerKm
    ) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.type = type;
        this.emissionsCO2ePerKm = emissionsCO2ePerKm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }

    public VehicleType getType() {
        return type;
    }

    public double getEmissionsCO2ePerKm() {
        return emissionsCO2ePerKm;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setType(VehicleType type) {
        this.type = type;
    }

    public void setEmissionsCO2ePerKm(double emissionsCO2ePerKm) {
        this.emissionsCO2ePerKm = emissionsCO2ePerKm;
    }

    @Transient
    public boolean isDefault() {
        if (owner == null) {
            return false;
        }
        var defaultVehicle = owner.getDefaultVehicle();
        if (defaultVehicle == null) {
            return false;
        }
        return defaultVehicle.getId().equals(this.id);
    }
}
