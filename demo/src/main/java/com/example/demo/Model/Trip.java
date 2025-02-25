package com.example.demo.Model;

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

    private String startAddress;
    private String endAddress;
    private Double distanceKM;

    @ManyToOne
    private TransportationMode transportationMode;

    @Embedded
    private MoneySaved moneySaved;

    public Trip() {}

    public Trip(
        User user,
        String startAddress,
        String endAddress,
        Double distanceKM,
        TransportationMode transportationMode,
        MoneySaved moneySaved
    ) {
        this.user = user;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.distanceKM = distanceKM;
        this.transportationMode = transportationMode;
        this.moneySaved = moneySaved;
    }

    public User getUser() {
        return this.user;
    }

    public MoneySaved getMoneySaved() {
        return this.moneySaved;
    }
}
