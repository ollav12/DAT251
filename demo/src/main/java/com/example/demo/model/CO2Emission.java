package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "co2emission")
public class CO2Emission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    private double TotalEmission;

    public CO2Emission() {}

    public CO2Emission(long Id, double TotalEmission) {
        this.Id = Id;
        this.TotalEmission = TotalEmission;
    }

    public long getId() {
        return Id;
    }

    public void setId(long Id) {
        this.Id = Id;
    }

    public double getTotalEmission() {
        return TotalEmission;
    }

    public void setTotalEmission(double TotalEmission) {
        this.TotalEmission = TotalEmission;
    }

    @Override
    public String toString() {
        return (
            "CO2Emission{" +
            "Id=" +
            Id +
            ", TotalEmission=" +
            TotalEmission +
            '}'
        );
    }
}
