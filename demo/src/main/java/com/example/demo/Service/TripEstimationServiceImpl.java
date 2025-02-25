package com.example.demo.Service;

import com.example.demo.Model.TransportationMode;
import com.example.demo.Repository.TransportationModeRepository;
import com.google.gson.Gson;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class TripEstimationServiceImpl {

    private final TransportationModeRepository transportationModeRepository;
    private final RestTemplate restTemplate;

    public TripEstimationServiceImpl(TransportationModeRepository transportationModeRepository, RestTemplate restTemplate) {
        this.transportationModeRepository = transportationModeRepository;
        this.restTemplate = restTemplate;
    }

  /*  public double estimateCO2(String origin, String destination, Long modeId) {
        TransportationMode mode = transportationModeRepository.findById(modeId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid mode id"));

        String apiKey = "apikey";
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origin + "&destinations=" + destination + "&key=" + apiKey;

        var response = restTemplate.getForObject(url, String.class);
        var rows = (List<Map<String, Object>>) new Gson().fromJson(response, Map.class).get("rows");
        var elements = (List<Map<String, Object>>) rows.get(0).get("elements");
        var distanceInfo = (Map<String, Object>) elements.get(0).get("distance");
        double distanceKM = ((Number) distanceInfo.get("value")).doubleValue() / 1000;

        return distanceKM * mode.getCo2PerKilometer();

    }*/
}
