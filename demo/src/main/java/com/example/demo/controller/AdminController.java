package com.example.demo.controller;

import com.example.demo.repository.TripRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

    // TODO: only use services
    private final UserRepository userRepository;
    private final TripRepository tripRepository;

    public AdminController(
        UserRepository userRepository,
        TripRepository tripRepository
    ) {
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
    }

    public record Statistics(
        long totalUsers,
        long totalTrips,
        double totalTripDistance,
        double totalEmissionsCO2eKg
    ) {}

    @GetMapping("/statistics")
    public ResponseEntity<Statistics> getStatistics() {
        // TODO: validate user is admin
        var totalTripDistance = tripRepository.sumTotalDistanceKm();
        var totalEmissionsCO2eKg = tripRepository.sumTotalEmissionsCO2eKg();
        return ResponseEntity.ok(
            new Statistics(
                userRepository.count(),
                tripRepository.count(),
                totalTripDistance,
                totalEmissionsCO2eKg
            )
        );
    }
}
