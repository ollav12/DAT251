package com.example.demo.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Model.Trip;
//import com.example.demo.Service.MoneySavedImpl;
import com.example.demo.Service.TripServiceImpl;

import okhttp3.Request;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final TripServiceImpl tripService;
    // private final MoneySavedImpl moneySavedService;

    public TripController(TripServiceImpl tripService) {
        this.tripService = tripService;
        // this.moneySavedService = moneySavedService;
    }

    @GetMapping("")
    public String getAllTrips() {
        return "Trips";
    }

    @GetMapping("/{tripid}")
    public Trip getTrip(@PathVariable Long tripid) {
        return tripService.getTrip(tripid);
    }

    @PostMapping("/register")
    public String registerTrip(@RequestBody Trip trip) {
        tripService.registerTrip(trip);
        // moneySavedService.registerMoneySaved(trip.getMoneySaved());
        return "Trip registered successfully";
    }
}
