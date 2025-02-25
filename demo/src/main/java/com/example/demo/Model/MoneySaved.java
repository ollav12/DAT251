package com.example.demo.Model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Embeddable
public class MoneySaved {

    private double totalMoneySaved;

    public MoneySaved() {

    }

    public double getTotalMoneySaved() {
        return this.totalMoneySaved;
    }
}
