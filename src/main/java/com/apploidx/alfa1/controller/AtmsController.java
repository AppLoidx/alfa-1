package com.apploidx.alfa1.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Arthur Kupriyanov on 27.06.2020
 */
@RequestMapping("/atms")
@RestController
public class AtmsController {

    private final RestTemplate restTemplate;

    public AtmsController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<Response> getData(@PathVariable("deviceId") int deviceId) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("x-ibm-client-id", "49db3088-5d30-472a-88c4-17c2a815ebce");
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
        ApiArmsResponse response = restTemplate.exchange("https://apiws.alfabank.ru/alfabank/alfadevportal/atm-service/atms", HttpMethod.GET, httpEntity, ApiArmsResponse.class).getBody();

        Optional<ArmApiResponse> armApiResponse = response.getData().getAtms()
                .stream().filter(a -> a.getDeviceId() == deviceId)
                .findFirst();

        if (armApiResponse.isEmpty()) {
            return ResponseEntity.status(404).body(new Error("atm not found"));
        } else {
            ArmApiResponse res = armApiResponse.get();
            return ResponseEntity.ok(new ArmResponse(res.getDeviceId(),
                    res.getCoordinates().get("latitude"),
                    res.getCoordinates().get("longitude"),
                    res.getAddress().get("city"),
                    res.getAddress().get("location"),
                    Boolean.parseBoolean(res.getServices().get("payments"))));
        }
    }

    @Data@AllArgsConstructor
    static class ArmResponse implements Response {
        int deviceId;
        double latitude;
        double longitude;
        String city;
        String location;
        boolean payments;
    }

    @Data
    static class ApiArmsResponse {
        AtmList data;
    }

    @Data
    static class AtmList {
        List<ArmApiResponse> atms;
    }

    @Data
    static class ArmApiResponse implements Response {
        int deviceId;
        Map<String, Double> coordinates;
        Map<String, String> address;
        Map<String, String> services;
    }
    @Data@AllArgsConstructor
    static class Error implements Response {
        String status;
    }
}
