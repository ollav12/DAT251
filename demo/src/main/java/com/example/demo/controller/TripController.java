package com.example.demo.controller;

import com.example.demo.model.Trip;
import com.example.demo.model.User;
import com.example.demo.service.TransportService;
import com.example.demo.service.TripServiceImpl;
import com.example.demo.service.UserService;
import jakarta.websocket.server.PathParam;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final UserService userService;
    private final TransportService transportService;
    private final TripServiceImpl tripService;

    // private final MoneySavedImpl moneySavedService;

    public TripController(
        UserService userService,
        TransportService transportService,
        TripServiceImpl tripService
    ) {
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
        String vehicleId
    ) {}

    @PostMapping("")
    public String addTrip(
        @PathParam(value = "userId") Long userId,
        @RequestBody AddTripRequest trip
    ) {
        // TODO: get user from credentials
        User user = userService.getUser(userId);

        System.out.println(
            "Handling request for user: " +
            user.getId() +
            " with trip details: " +
            trip.toString()
        );

        transportService.addTrip(
            user,
            trip.origin,
            trip.destination,
            trip.mode,
            trip.vehicleId
        );

        return "{\"status\": \"success\"}";
    }
}
