package com.example.demo.Service;

import com.example.demo.Model.Trip;

public interface TripService {
    Trip registerTrip(Trip trip);

    Trip getTrip(long id);
}
