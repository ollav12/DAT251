package com.example.demo.Controller;

import com.example.demo.Service.TransportService;
import com.example.demo.Service.TransportService.TripEstimateResults;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transport")
public class TransportController {

    @Autowired
    private TransportService transportService;

    public TransportController() {}

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
}
