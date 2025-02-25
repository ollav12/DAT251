package com.example.demo.Service;

import com.example.demo.Model.Trip;
import com.example.demo.Repository.TripRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TripServiceImpl implements TripService {

    private TripRepository tripRepository;

    @Autowired
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
