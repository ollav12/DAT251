package com.example.demo.service;

import com.example.demo.model.Trip;
import com.example.demo.model.User;
import com.example.demo.model.Vehicle;
import com.example.demo.model.VehicleType;
import com.example.demo.repository.TripRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VehicleRepository;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PlaceAutocompleteRequest;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ZeroResultsException;
import com.google.maps.model.AutocompletePrediction;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.TransitMode;
import com.google.maps.model.TravelMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class TransportService {

    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final VehicleRepository vehicleRepository;

    public TransportService(
        UserRepository userRepository,
        TripRepository tripRepository,
        VehicleRepository vehicleRepository
    ) {
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public enum LeaderboardMetric {
        TOTAL_EMISSIONS,
        TOTAL_SAVED_EMISSIONS,
        TOTAL_DURATION_SECONDS,
        TOTAL_DISTANCE_KILOMETERS,
        AVERAGE_CO2E_PER_KILOMETER,
    }

    public enum LeaderboardPeriod {
        LIFETIME,
        PAST_YEAR,
        PAST_MONTH,
        PAST_WEEK,
    }

    public record Leaderboard(
        LeaderboardMetric metric,
        LeaderboardPeriod period,
        List<UserRepository.LeaderboardRow> rows
    ) {}

    public Leaderboard getLeaderboard(
        LeaderboardMetric metric,
        LeaderboardPeriod period
    ) {
        if (metric == null) {
            metric = LeaderboardMetric.TOTAL_EMISSIONS;
        }
        if (period == null) {
            period = LeaderboardPeriod.LIFETIME;
        }
        LocalDateTime since = LocalDateTime.now();
        switch (period) {
            case LIFETIME:
                // TODO: ugly
                since = since.minusYears(100);
                break;
            case PAST_YEAR:
                since = since.minusYears(1);
                break;
            case PAST_MONTH:
                since = since.minusMonths(1);
                break;
            case PAST_WEEK:
                since = since.minusWeeks(1);
                break;
        }
        // Date since = Date.valueOf(periodAgo);

        List<UserRepository.LeaderboardRow> rows = null;
        switch (metric) {
            case TOTAL_EMISSIONS:
                rows = userRepository.getLeaderboardTotalEmissions(since);
                break;
            case TOTAL_DISTANCE_KILOMETERS:
                rows = userRepository.getLeaderboardTotalDistance(since);
                break;
            case TOTAL_DURATION_SECONDS:
                rows = userRepository.getLeaderboardTotalDuration(since);
                break;
            case AVERAGE_CO2E_PER_KILOMETER:
                rows = userRepository.getLeaderboardAverageEmissionsPerKm(
                    since
                );
                break;
            case TOTAL_SAVED_EMISSIONS:
                rows = userRepository.getLeaderboardTotalSavedEmissions(since);
                break;
        }
        return new Leaderboard(metric, period, rows);
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

    public List<String> getAddressAutocomplete(
        String query,
        UUID sessionToken
    ) {
        var results = this.getAddressAutocompleteResults(query, sessionToken);
        return results;
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
        vehicle.setOwner(user);
        vehicleRepository.save(vehicle);
    }

    public Vehicle getDefaultVehicle(User user) {
        var defaultVehicle = user.getDefaultVehicle();
        if (defaultVehicle == null) {
            throw new IllegalArgumentException("No default vehicle");
        }
        return defaultVehicle;
    }

    public void setDefaultVehicle(User user, String vehicleId) {
        var vehicleIdLong = Long.parseLong(vehicleId);
        var vehicle = vehicleRepository.findById(vehicleIdLong).orElseThrow();
        user.setDefaultVehicle(vehicle);
        userRepository.save(user);
    }

    public List<Vehicle> getVehicles(User user) {
        return vehicleRepository.findByOwner(user);
    }

    public void deleteVehicle(User user, String vehicleId) {
        var vehicleIdLong = Long.parseLong(vehicleId);
        var vehicle = vehicleRepository.findById(vehicleIdLong).orElseThrow();
        if (vehicle.isDefault()) {
            throw new IllegalArgumentException("Cannot delete default vehicle");
        }
        vehicleRepository.delete(vehicle);
    }

    public void addTrip(
        User user,
        String origin,
        String destination,
        String selectedMode,
        String selectedVehicleId
    ) {
        String mode = null;
        Vehicle vehicle = null;
        if (selectedMode != null && selectedVehicleId != null) {
            throw new IllegalArgumentException(
                "Cannot select both mode and vehicle"
            );
        } else if (selectedMode == null && selectedVehicleId == null) {
            throw new IllegalArgumentException(
                "Must select either mode or vehicle"
            );
        } else if (selectedMode != null) {
            mode = selectedMode;
        } else if (selectedVehicleId != null) {
            vehicle = vehicleRepository
                .findById(Long.parseLong(selectedVehicleId))
                .orElseThrow();
            mode = vehicle.getType().toTravelMode().toString().toLowerCase();
        }

        // Get estimate for all alternative transport modes.
        // We do this so that we can compare to calculate savings.
        // TODO: use actual vehicle data
        var results = getTripEstimate(origin, destination, vehicle);

        // TODO: we cannot compare costs between alternative modes yet
        var alternatives = results.getAlternatives();
        var estimate = alternatives.get(mode);
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
            mode,
            vehicle,
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
        String destination,
        Vehicle vehicle
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
                Vehicle estimationVehicle = null;
                if (vehicle != null) {
                    if (vehicle.getType().toTravelMode() == mode) {
                        estimationVehicle = vehicle;
                    }
                }

                TripEstimate currentRoute = getRouteEstimate(
                    route,
                    estimationVehicle
                );
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

    private TripEstimate getRouteEstimate(
        DirectionsRoute route,
        Vehicle vehicle
    ) {
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
                        if (
                            vehicle != null &&
                            vehicle.getType().toTravelMode() ==
                            TravelMode.BICYCLING
                        ) {
                            totalEmissions +=
                                (step.distance.inMeters / 1000) *
                                // TODO: clean up units
                                (vehicle.getEmissionsCO2ePerKm() / 1000);
                            break;
                        }
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
                        if (
                            vehicle != null &&
                            vehicle.getType().toTravelMode() ==
                            TravelMode.DRIVING
                        ) {
                            totalEmissions +=
                                (step.distance.inMeters / 1000) *
                                // TODO: clean up units
                                (vehicle.getEmissionsCO2ePerKm() / 1000);
                            break;
                        }
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

    @Cacheable(value = "address", key = "#query")
    private List<String> getAddressAutocompleteResults(
        String query,
        UUID sessionToken
    ) {
        GeoApiContext context = new GeoApiContext.Builder()
            .apiKey(this.googleMapsApiKey)
            .build();

        PlaceAutocompleteRequest.SessionToken token =
            new PlaceAutocompleteRequest.SessionToken(sessionToken);

        PlaceAutocompleteRequest request = PlacesApi.placeAutocomplete(
            context,
            query,
            token
        );

        AutocompletePrediction[] result = null;
        try {
            result = request.await();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(
                "Failed to fetch address autocomplete",
                e
            );
        } finally {
            context.shutdown();
        }

        List<String> addresses = new ArrayList<>();
        for (AutocompletePrediction prediction : result) {
            // TODO: not sure if this is the optimal value to use
            addresses.add(prediction.description);
        }

        return addresses;
    }
}
