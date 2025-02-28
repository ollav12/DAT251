package com.example.demo.service;

import com.example.demo.model.Trip;
import com.example.demo.repository.TripRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    public TripServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public Trip registerTrip(Trip trip) {
        if (tripRepository.findByUser(trip.getUser()) != null) {
            throw new RuntimeException("User already has a trip");
        }

        return tripRepository.save(trip);
    }

    @Override
    public Trip getTrip(long id) {
        try {
            return tripRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Trip not found");
        }
    }

    public List<Trip> getAllTrips() {
        // TODO: filter trips by user
        return tripRepository.findAll();
    }
}
