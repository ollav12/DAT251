package com.example.demo.Controller;

import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TravelMode;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transport")
public class TransportController {

    private final String googleMapsApiKey = "TODO";

    public TransportController() {}

    private class TripEstimateResults {

        private Map<String, TripEstimate> tripEstimates;

        public TripEstimateResults() {
            this.tripEstimates = new HashMap<>();
        }

        public void addEstimate(String mode, TripEstimate estimate) {
            tripEstimates.put(mode, estimate);
        }

        public Map<String, TripEstimate> getEstimates() {
            return tripEstimates;
        }
    }

    private class TripEstimate {

        private Duration duration;
        private double distance;
        private double emissionsCO2eKg;

        public TripEstimate(
            Duration duration,
            double distance,
            double emissionsCO2eKg
        ) {
            this.duration = duration;
            this.distance = distance;
            this.emissionsCO2eKg = emissionsCO2eKg;
        }

        public Duration getDuration() {
            return duration;
        }

        public double getDistance() {
            return distance;
        }

        public double getEmissionsCO2eKg() {
            return emissionsCO2eKg;
        }
    }

    @GetMapping(
        value = "/tripestimate",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TripEstimateResults> getAlternatives() {
        GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(this.googleMapsApiKey)
            .build();

        String origin = "1600 Amphitheatre Parkway, Mountain View, CA";
        String destination = "1 Infinite Loop, Cupertino, CA";

        // Call Directions API once per travel mode
        TravelMode[] modes = {
            TravelMode.DRIVING,
            TravelMode.WALKING,
            TravelMode.BICYCLING,
            TravelMode.TRANSIT,
        };
        TripEstimateResults results = new TripEstimateResults();
        for (TravelMode mode : modes) {
            DirectionsApiRequest request = DirectionsApi.newRequest(context)
                .origin(origin)
                .destination(destination)
                // .alternatives(true);
                // .transitRoutingPreference(TransitRoutingPreference.LESS_WALKING)
                // .transitMode(TransitMode.BUS, TransitMode.SUBWAY, TransitMode.RAIL)
                .mode(mode);

            DirectionsResult result;
            try {
                result = request.await();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to fetch directions", e);
            } finally {
                context.shutdown();
            }

            TripEstimate bestRoute = null;
            for (DirectionsRoute route : result.routes) {
                Duration totalDuration = Duration.ZERO;
                double totalDistance = 0.0;
                double totalEmissions = 0.0;

                for (DirectionsLeg leg : route.legs) {
                    if (leg.duration != null) {
                        totalDuration = totalDuration.plusSeconds(
                            leg.duration.inSeconds
                        );
                    }
                    if (leg.distance != null) {
                        totalDistance += leg.distance.inMeters;
                    }

                    for (DirectionsStep step : leg.steps) {
                        switch (step.travelMode) {
                            case WALKING:
                                totalEmissions +=
                                    (step.distance.inMeters / 1000) * 0.0;
                                break;
                            case BICYCLING:
                                totalEmissions +=
                                    (step.distance.inMeters / 1000) * 0.0;
                                break;
                            case TRANSIT:
                                if (step.transitDetails != null) {
                                    switch (
                                        step.transitDetails.line.vehicle.type
                                    ) {
                                        case BUS:
                                            totalEmissions +=
                                                (step.distance.inMeters /
                                                    1000) *
                                                0.089;
                                            break;
                                        case CABLE_CAR:
                                            break;
                                        case TRAM:
                                            // E.g. Bybanen
                                            totalEmissions +=
                                                (step.distance.inMeters /
                                                    1000) *
                                                0.001;
                                            break;
                                        case SUBWAY:
                                            break;
                                        case RAIL:
                                            break;
                                        case HEAVY_RAIL:
                                            break;
                                        case FERRY:
                                            break;
                                        case FUNICULAR:
                                            break;
                                        default:
                                            break;
                                    }
                                } else {
                                    // Fallback
                                    totalEmissions +=
                                        (step.distance.inMeters / 1000) * 0.0;
                                }
                                break;
                            case DRIVING:
                                totalEmissions +=
                                    (step.distance.inMeters / 1000) * 0.118;
                                break;
                            default:
                                System.out.println("Unknown");
                                break;
                        }
                    }
                }

                if (
                    bestRoute == null ||
                    totalEmissions < bestRoute.getEmissionsCO2eKg()
                ) {
                    bestRoute = new TripEstimate(
                        totalDuration,
                        totalDistance,
                        totalEmissions
                    );
                }
            }

            results.addEstimate(mode.name().toLowerCase(), bestRoute);
        }

        return ResponseEntity.ok().body(results);
    }
}
