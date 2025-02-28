package com.example.demo.model;

import com.google.maps.model.TravelMode;

public enum VehicleType {
    BICYCLE,
    ELECTRIC_BIKE,
    ELECTRIC_SCOOTER,
    CAR,
    MOTORCYCLE,
    ELECTRIC_CAR;

    // public String fuelType()

    public static VehicleType fromString(String type) {
        switch (type) {
            case "BICYCLE":
                return BICYCLE;
            case "ELECTRIC_BIKE":
                return ELECTRIC_BIKE;
            case "ELECTRIC_SCOOTER":
                return ELECTRIC_SCOOTER;
            case "CAR":
                return CAR;
            case "MOTORCYCLE":
                return MOTORCYCLE;
            case "ELECTRIC_CAR":
                return ELECTRIC_CAR;
            default:
                throw new IllegalArgumentException(
                    "Invalid vehicle type: " + type
                );
        }
    }

    public TravelMode toTravelMode() {
        switch (this) {
            case BICYCLE:
                return TravelMode.BICYCLING;
            case ELECTRIC_BIKE:
                return TravelMode.BICYCLING;
            case ELECTRIC_SCOOTER:
                return TravelMode.BICYCLING;
            case CAR:
                return TravelMode.DRIVING;
            case ELECTRIC_CAR:
                return TravelMode.DRIVING;
            case MOTORCYCLE:
                return TravelMode.DRIVING;
            default:
                throw new IllegalArgumentException(
                    "Invalid vehicle type: " + this.toString()
                );
        }
    }
}
