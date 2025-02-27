package com.example.demo.service;

import com.example.demo.model.Trip;

public interface TripService {
    Trip registerTrip(Trip trip);

    Trip getTrip(long id);
}
