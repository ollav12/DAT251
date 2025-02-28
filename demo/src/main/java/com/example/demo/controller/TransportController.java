package com.example.demo.controller;

import com.example.demo.model.Vehicle;
import com.example.demo.model.VehicleType;
import com.example.demo.service.TransportService;
import com.example.demo.service.TransportService.Statistics;
import com.example.demo.service.TransportService.TripEstimateResults;
import com.example.demo.service.UserService;
import jakarta.websocket.server.PathParam;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transport")
public class TransportController {

    private final UserService userService;
    private final TransportService transportService;

    public TransportController(
        UserService userService,
        TransportService transportService
    ) {
        this.userService = userService;
        this.transportService = transportService;
    }

    @GetMapping(
        value = "/statistics",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Statistics> getStatistics(
        @PathParam(value = "userId") Long userId
    ) {
        var user = userService.getUser(userId);
        var statistics = transportService.getStatistics(user);
        return ResponseEntity.ok().body(statistics);
    }

    @GetMapping(
        value = "/tripestimate",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TripEstimateResults> getAlternatives(
        @PathParam("origin") String origin,
        @PathParam("destination") String destination
    ) {
        TripEstimateResults results = transportService.getTripEstimate(
            origin,
            destination
        );
        return ResponseEntity.ok().body(results);
    }

    @GetMapping(
        value = "/vehicles",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Vehicle>> getVehicles() {
        var user = userService.getUser(1);
        var vehicles = transportService.getVehicles(user);
        return ResponseEntity.ok().body(vehicles);
    }

    public record AddVehicleRequest(
        String vehicleType,
        String make,
        String model,
        int year,
        double emissionsCO2ePerKm
    ) {}

    @PostMapping(value = "/vehicles")
    public void addVehicle(@RequestBody AddVehicleRequest request) {
        // TODO: get from credentials
        var user = userService.getUser(1);
        VehicleType vehicleType = VehicleType.fromString(request.vehicleType);
        transportService.addVehicle(
            user,
            request.make,
            request.model,
            request.year,
            vehicleType,
            request.emissionsCO2ePerKm
        );
    }
}
