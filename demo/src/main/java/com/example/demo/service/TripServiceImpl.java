package com.example.demo.service;

import com.example.demo.model.Trip;
import com.example.demo.model.User;
import com.example.demo.repository.TripRepository;
import com.example.demo.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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
            // NoSuchElementException("User not
            // found"));
            return tripRepository.sumTotalEmissionsByUser(user);
        } catch (Exception e) {
            throw new RuntimeException("Trips not found");
        }
    }

    public List<Trip> getAllTrips() {
        // TODO: filter trips by user
        return tripRepository.findAll();
    }

    public List<Trip> getAllTripsByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
        return tripRepository.findAllTripsByUser(user);
    }

    public void deleteUserEmissions(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("User not found"));
        tripRepository.deleteUserTrip(user);
    }

    public Trip updateTrip(Long tripId, Trip updatedTrip
    ) {
        Optional<Trip> optExistingTrip = tripRepository.findById(tripId);
        if (optExistingTrip.isEmpty()) {
            throw new RuntimeException("Trip not found");
        }
        Trip existingTrip = optExistingTrip.get();
        existingTrip.setTotalDistanceKm(updatedTrip.getTotalDistanceKm());
        existingTrip.setTotalDurationSeconds(updatedTrip.getTotalDurationSeconds());
        existingTrip.setTotalEmissionsCO2eKg(updatedTrip.getTotalEmissionsCO2eKg());
        existingTrip.setSavedEmissionsCO2eKg(updatedTrip.getSavedEmissionsCO2eKg());
        existingTrip.setOrigin(updatedTrip.getOrigin());
        existingTrip.setDestination(updatedTrip.getDestination());
        existingTrip.setTravelMode(updatedTrip.getTravelMode());
        existingTrip.setVehicle(updatedTrip.getVehicle());
        existingTrip.setTransportationMode(updatedTrip.getTransportationMode());
        existingTrip.setUser(updatedTrip.getUser());
        existingTrip.setMoneySaved(updatedTrip.getMoneySaved());
        return tripRepository.save(existingTrip);
    }


}




