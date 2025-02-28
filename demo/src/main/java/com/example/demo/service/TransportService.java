package com.example.demo.service;

import com.example.demo.model.Trip;
import com.example.demo.model.User;
import com.example.demo.model.Vehicle;
import com.example.demo.model.VehicleType;
import com.example.demo.repository.TripRepository;
import com.example.demo.repository.VehicleRepository;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ZeroResultsException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TransitMode;
import com.google.maps.model.TravelMode;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TransportService {

    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;

    public TransportService(
        TripRepository tripRepository,
        VehicleRepository vehicleRepository
    ) {
        this.tripRepository = tripRepository;
        this.vehicleRepository = vehicleRepository;
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

    public void addVehicle(
        User user,
        String make,
        String model,
        int year,
        VehicleType type,
        double emissionsCO2ePerKm
    ) {
        var vehicle = new Vehicle(make, model, year, type, emissionsCO2ePerKm);
        vehicleRepository.save(vehicle);
    }

    public Vehicle getDefaultVehicle(User user) {
        var defaultVehicle = user.getDefaultVehicle();
        if (defaultVehicle == null) {
            throw new IllegalArgumentException("No default vehicle");
        }
        return defaultVehicle;
    }

    public List<Vehicle> getVehicles(User user) {
        return vehicleRepository.findByOwner(user);
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

        if (estimate == null) {
            throw new IllegalArgumentException("No estimate for selected mode");
        }
        if (carEstimate == null) {
            throw new IllegalArgumentException("No estimate for driving mode");
        }

        var totalCO2eSaved =
            carEstimate.getEmissionsCO2eKg() - estimate.getEmissionsCO2eKg();

        Trip trip = new Trip(
            user,
            origin,
            destination,
            selectedMode,
            estimate.getDistanceKm(),
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
        private double distanceKm;
        private double emissionsCO2eKg;

        public TripEstimate(
            Duration duration,
            double distanceKm,
            double emissionsCO2eKg
        ) {
            this.duration = duration;
            this.distanceKm = distanceKm;
            this.emissionsCO2eKg = emissionsCO2eKg;
        }

        public Duration getDuration() {
            return duration;
        }

        public double getDistanceKm() {
            return distanceKm;
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

    // All of these are intended to include:
    // - Amortized vehicle production emissions
    // - Fuel emissions
    // But not:
    // - Food emissions (e.g. from walking)
    // Public transport should be calculated per person:
    private final double emissionsPerKmWalking = 0.0;
    private final double emissionsPerKmBicycling = 0.0;
    private final double emissionsPerKmCar = 0.118;
    private final double emissionsPerPersonKmSkyssBybanen = 0.001;
    private final double emissionsPerPersonKmSkyssBus = 0.089;
    private final double emissionsPerPersonKmVyTrain = 0.005; // Source: Claude estimate

    private TripEstimate getRouteEstimate(DirectionsRoute route) {
        Duration totalDuration = Duration.ZERO;
        double totalDistanceMeters = 0.0;
        double totalEmissions = 0.0;

        for (DirectionsLeg leg : route.legs) {
            for (DirectionsStep step : leg.steps) {
                if (step.distance != null) {
                    totalDistanceMeters += step.distance.inMeters;
                }
                if (step.duration != null) {
                    totalDuration = totalDuration.plusSeconds(
                        step.duration.inSeconds
                    );
                }

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
                                        emissionsPerPersonKmSkyssBus;
                                    break;
                                case CABLE_CAR:
                                    break;
                                case TRAM:
                                    // E.g. Bybanen
                                    totalEmissions +=
                                        (step.distance.inMeters / 1000) *
                                        emissionsPerPersonKmSkyssBybanen;
                                    break;
                                case SUBWAY:
                                    break;
                                case RAIL:
                                    break;
                                case HEAVY_RAIL:
                                    // E.g. Vy
                                    totalEmissions +=
                                        (step.distance.inMeters / 1000) *
                                        emissionsPerPersonKmVyTrain;
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

        return new TripEstimate(
            totalDuration,
            totalDistanceMeters / 1000,
            totalEmissions
        );
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
            .mode(mode);
        if (mode == TravelMode.TRANSIT) {
            request = request.transitMode(
                TransitMode.BUS,
                TransitMode.SUBWAY,
                TransitMode.TRAM,
                TransitMode.RAIL,
                TransitMode.TRAIN
            );
        }

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
