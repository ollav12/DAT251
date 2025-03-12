package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.TripServiceImpl;

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

}
