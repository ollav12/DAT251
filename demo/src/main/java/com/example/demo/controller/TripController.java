package com.example.demo.controller;

import com.example.demo.model.Challenge;
import com.example.demo.model.ChallengeStatus;
import com.example.demo.model.Trip;
import com.example.demo.model.User;
import com.example.demo.model.Challenge.ChallengeType;
import com.example.demo.model.ChallengeStatus.Status;
import com.example.demo.service.ChallengeStatusService;
import com.example.demo.service.TransportService;
import com.example.demo.service.TripServiceImpl;
import com.example.demo.service.UserService;
import jakarta.websocket.server.PathParam;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jmx.support.MetricType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final UserService userService;
    private final TransportService transportService;
    private final TripServiceImpl tripService;

    @Autowired
    private ChallengeStatusService challengeStatusService;

    // private final MoneySavedImpl moneySavedService;

    public TripController(
            UserService userService,
            TransportService transportService,
            TripServiceImpl tripService) {
        this.userService = userService;
        this.transportService = transportService;
        this.tripService = tripService;
    }

    @GetMapping("")
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    @GetMapping("/{tripid}")
    public Trip getTrip(@PathVariable Long tripid) {
        return tripService.getTrip(tripid);
    }

    public record AddTripRequest(
            String origin,
            String destination,
            String mode,
            String vehicleId) {
    }

    @PostMapping("")
    public String addTrip(
            @PathParam(value = "userId") Long userId,
            @RequestBody AddTripRequest trip) {
        // TODO: get user from credentials
        User user = userService.getUser(userId);

        System.out.println(
                "Handling request for user: " +
                        user.getId() +
                        " with trip details: " +
                        trip.toString());

        Trip newTrip = transportService.addTrip(
                user,
                trip.origin,
                trip.destination,
                trip.mode,
                trip.vehicleId);

        List<ChallengeStatus> statuses = challengeStatusService.getUserChallenges(user.getId());

        for (ChallengeStatus status : statuses) {
            if (status.getStatus() == Status.COMPLETED) {
                continue;
            }
            Challenge challenge = status.getChallenge();

            // Start challenege if not started
            if (status.getStatus() == Status.NOT_STARTED) {
                status.setStatus(Status.IN_PROGRESS);
            }

            // Check if challenge is completed

            if (status.getChallenge().getChallengeType() == ChallengeType.METRIC) {
                status.setCurrentValue(status.getCurrentValue() + newTrip.getSavedEmissionsCO2eKg());
            } else if (status.getChallenge().getChallengeType() == ChallengeType.ACTION) {
                status.setCurrentValue(status.getCurrentValue() + 1);

            }
            if (status.getCurrentValue() >= challenge.getTargetValue()) {
                status.setCurrentValue(challenge.getTargetValue());
                status.setStatus(Status.COMPLETED);

                user.setPoints(user.getPoints() + challenge.getRewardPoints());
                userService.updateUser(user, userId);
            }

            challengeStatusService.updateChallenge(status.getChallengeStatusId(), status);

        }
        return "{\"status\": \"success\"}";
    }
}
