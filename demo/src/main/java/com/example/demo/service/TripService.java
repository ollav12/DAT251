package com.example.demo.service;

import com.example.demo.model.Trip;

import java.util.List;


public interface TripService {
    Trip registerTrip(Trip trip);

    Trip getTrip(long id);

    Double getUserTotalEmission(long userId);

    List<Trip> getAllTrips();

    List<Trip> getAllTripsByUser(Long userId);

    void deleteUserEmissions(long userId);
}
