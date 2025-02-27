package com.example.demo.service;

import com.example.demo.model.Trip;
import com.example.demo.model.User;
import com.example.demo.repository.TripRepository;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ZeroResultsException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TravelMode;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TransportService {

    private final TripRepository tripRepository;

    public TransportService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public record Statistics(
        // Trip
        int totalTrips,
        double totalDistanceKm,
        double totalDurationSeconds,
        // Emissions
        double totalEmissionsCO2eKg,
        double totalEmissionsSavingsCO2eKg,
        // Financial
        double totalSavingsNOK,
        double totalCostNOK
    ) {}

    public Statistics getStatistics(User user) {
        return new Statistics(
            // Transport
            tripRepository.countByUser(user),
            tripRepository.sumTotalDistanceKmByUser(user),
            tripRepository.sumTotalDurationSecondsByUser(user),
            // Emissions
            tripRepository.sumTotalEmissionsByUser(user),
            tripRepository.sumSavedEmissionsByUser(user),
            // Financial
            // TODO: calculate
            0.0,
            0.0
        );
    }

    public void addTrip(
        User user,
        String origin,
        String destination,
        String selectedMode
    ) {
        // Get estimate for all alternative transport modes.
        // We do this so that we can compare to calculate savings.
        var results = getTripEstimate(origin, destination);

        // TODO: we cannot compare costs between alternative modes yet
        var alternatives = results.getAlternatives();
        var estimate = alternatives.get(selectedMode);
        var carEstimate = alternatives.get("driving");

        var totalCO2eSaved =
            carEstimate.getEmissionsCO2eKg() - estimate.getEmissionsCO2eKg();

        Trip trip = new Trip(
            user,
            origin,
            destination,
            selectedMode,
            estimate.getDistance(),
            estimate.getDuration().getSeconds(),
            estimate.getEmissionsCO2eKg(),
            totalCO2eSaved
        );

        System.out.println("adding trip");
        tripRepository.save(trip);
    }

    public class TripEstimateResults {

        private Map<String, TripEstimate> alternatives;

        public TripEstimateResults() {
            this.alternatives = new HashMap<>();
        }

        public void addAlternative(String mode, TripEstimate estimate) {
            alternatives.put(mode, estimate);
        }

        public Map<String, TripEstimate> getAlternatives() {
            return alternatives;
        }
    }

    public class TripEstimate {

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

    public TripEstimateResults getTripEstimate(
        String origin,
        String destination
    ) {
        TripEstimateResults results = new TripEstimateResults();
        TravelMode[] modes = {
            TravelMode.WALKING,
            TravelMode.BICYCLING,
            TravelMode.TRANSIT,
            TravelMode.DRIVING,
        };
        for (TravelMode mode : modes) {
            DirectionsResult result = getDirections(origin, destination, mode);
            if (result == null) {
                continue;
            }
            if (result.routes.length == 0) {
                continue;
            }

            // Find and select best route
            TripEstimate bestRoute = null;
            for (DirectionsRoute route : result.routes) {
                TripEstimate currentRoute = getRouteEstimate(route);
                if (
                    bestRoute == null ||
                    currentRoute.getEmissionsCO2eKg() <
                    bestRoute.getEmissionsCO2eKg()
                ) {
                    bestRoute = currentRoute;
                }
            }
            if (bestRoute == null) {
                continue;
            }
            System.out.println("add alternative" + bestRoute.toString());
            results.addAlternative(mode.name().toLowerCase(), bestRoute);
        }

        return results;
    }

    private final double emissionsPerKmWalking = 0.0;
    private final double emissionsPerKmBicycling = 0.0;
    private final double emissionsPerKmCar = 0.118;
    private final double emissionsPerKmSkyssBybanen = 0.001;
    private final double emissionsPerKmSkyssBus = 0.089;

    private TripEstimate getRouteEstimate(DirectionsRoute route) {
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
                            (step.distance.inMeters / 1000) *
                            emissionsPerKmWalking;
                        break;
                    case BICYCLING:
                        totalEmissions +=
                            (step.distance.inMeters / 1000) *
                            emissionsPerKmBicycling;
                        break;
                    case TRANSIT:
                        if (step.transitDetails != null) {
                            switch (step.transitDetails.line.vehicle.type) {
                                case BUS:
                                    // TODO: check line operator
                                    totalEmissions +=
                                        (step.distance.inMeters / 1000) *
                                        emissionsPerKmSkyssBus;
                                    break;
                                case CABLE_CAR:
                                    break;
                                case TRAM:
                                    // E.g. Bybanen
                                    totalEmissions +=
                                        (step.distance.inMeters / 1000) *
                                        emissionsPerKmSkyssBybanen;
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
                                case COMMUTER_TRAIN:
                                    break;
                                case GONDOLA_LIFT:
                                    break;
                                case HIGH_SPEED_TRAIN:
                                    break;
                                case INTERCITY_BUS:
                                    break;
                                case LONG_DISTANCE_TRAIN:
                                    break;
                                case METRO_RAIL:
                                    break;
                                case MONORAIL:
                                    break;
                                case SHARE_TAXI:
                                    break;
                                case TROLLEYBUS:
                                    break;
                                case OTHER:
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
                        // TODO: personal vehicles
                        totalEmissions +=
                            (step.distance.inMeters / 1000) * emissionsPerKmCar;
                        break;
                    case UNKNOWN:
                        break;
                    default:
                        System.out.println("Unknown");
                        break;
                }
            }
        }

        return new TripEstimate(totalDuration, totalDistance, totalEmissions);
    }

    private final String googleMapsApiKey = System.getenv(
        "GOOGLE_MAPS_API_KEY"
    );

    @Cacheable(
        value = "directions",
        key = "#origin + '-' + #destination + '-' + #mode"
    )
    private DirectionsResult getDirections(
        String origin,
        String destination,
        TravelMode mode
    ) {
        GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(this.googleMapsApiKey)
            .build();

        DirectionsApiRequest request = DirectionsApi.newRequest(context)
            .origin(origin)
            .destination(destination)
            // .alternatives(true);
            // .transitRoutingPreference(TransitRoutingPreference.LESS_WALKING)
            // .transitMode(TransitMode.BUS, TransitMode.SUBWAY, TransitMode.RAIL)
            .mode(mode);

        DirectionsResult result = null;
        try {
            System.out.println("Requesting directions for mode: " + mode);
            result = request.await();
            System.out.println("Received directions for mode: " + mode);
        } catch (ZeroResultsException e) {
            System.out.println("No results found for mode: " + mode);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to fetch directions", e);
        } finally {
            context.shutdown();
        }

        return result;
    }
}
