package com.example.demo.Model;

import jakarta.persistence.Embeddable;

@Embeddable
public class MoneySaved {

    private double totalMoneySaved;

    public MoneySaved() {}

    public double getTotalMoneySaved() {
        return this.totalMoneySaved;
    }
}
