package com.example.demo.service;

import com.example.demo.model.Trip;
import com.example.demo.model.User;
import com.example.demo.repository.TripRepository;
import com.example.demo.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

@Service
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    private final UserRepository userRepository;

    public TripServiceImpl(TripRepository tripRepository, UserRepository userRepository) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
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

    public Double getUserTotalEmission(long userId) {
        try {
            User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
            return tripRepository.sumTotalEmissionsByUser(user);
        } catch (Exception e) {
            throw new RuntimeException("Trips not found");
        }
    }

    public List<Trip> getAllTrips() {
        // TODO: filter trips by user
        return tripRepository.findAll();
    }
}
