package com.sollo_script.weather_board_api_service.service.sample;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * SAMPLE CODE: DO NOT USE IN PRODUCTION
 * 
 * This service demonstrates how to generate RestClients dynamically at runtime.
 * Instead of injecting a pre-built RestClient, we inject the Builder.
 */
@Service
public class DynamicClientServiceSample {

    private final RestClient.Builder restClientBuilder;

    // Inject the Builder, which is a factory for creating Clients
    public DynamicClientServiceSample(RestClient.Builder restClientBuilder) {
        this.restClientBuilder = restClientBuilder;
    }

    /**
     * Creates a temporary client for a specific Base URL and executes a request.
     */
    public void callDynamicHost(String dynamicBaseUrl, String endpoint) {

        // Build a new client specifically for this request/host
        RestClient dynamicClient = restClientBuilder
                .baseUrl(dynamicBaseUrl)
                .build();

        // Execute request
        dynamicClient.get()
                .uri(endpoint)
                .retrieve()
                .toBodilessEntity();
    }
}
