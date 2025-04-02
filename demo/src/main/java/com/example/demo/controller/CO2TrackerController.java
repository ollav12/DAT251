package com.example.demo.controller;

import com.example.demo.model.Trip;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.service.TripServiceImpl;

import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;


@RestController
@RequestMapping("/users")
public class CO2TrackerController {

    private final TripServiceImpl tripService;

    public CO2TrackerController(TripServiceImpl tripService) {
        this.tripService = tripService;
    }

    @GetMapping("/{id}/emission")
    public ResponseEntity<Double> getUserEmissions(@PathVariable long id) {
        try {
            Double emission = tripService.getUserTotalEmission(id);
            return ResponseEntity.ok().body(emission);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @DeleteMapping("/{id}/emission")
    public ResponseEntity<Void> deleteUserEmissions(@PathVariable long id) {
        try {
            tripService.deleteUserEmissions(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/{id}/trips")
    public ResponseEntity<List<Trip>> getUserTrips(@PathVariable long id) {
        try {
            return ResponseEntity.ok().body(tripService.getAllTripsByUser(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


}
