package com.sollo_script.weather_board_api_service.service.sample;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * SAMPLE CODE: DO NOT USE IN PRODUCTION
 * 
 * This service demonstrates how to inject specific implementations of the same
 * bean type
 * using the @Qualifier annotation to disambiguate.
 */
@Service
public class MultipleClientServiceSample {

    private final RestClient primaryClient;
    private final RestClient secondaryClient;

    // We explicitly tell Spring which bean to use for each parameter
    public MultipleClientServiceSample(
            @Qualifier("primaryApiClient") RestClient primaryClient,
            @Qualifier("secondaryApiClient") RestClient secondaryClient) {
        this.primaryClient = primaryClient;
        this.secondaryClient = secondaryClient;
    }

    public void performActions() {
        // Uses https://api.primary-service.com
        primaryClient.get().uri("/users").retrieve();

        // Uses https://api.secondary-service.com
        secondaryClient.get().uri("/orders").retrieve();
    }
}
