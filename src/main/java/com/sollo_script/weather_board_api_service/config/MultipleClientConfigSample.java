package com.sollo_script.weather_board_api_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * SAMPLE CODE: DO NOT USE IN PRODUCTION
 * 
 * This configuration class demonstrates how to define multiple beans of the
 * same type (RestClient)
 * but with different configurations (Base URLs), accessible via specific bean
 * names.
 */
@Configuration
public class MultipleClientConfigSample {

    // Bean 1: Identifiable by the name "primaryApiClient"
    @Bean("primaryApiClient")
    public RestClient primaryClient() {
        return RestClient.builder()
                .baseUrl("https://api.primary-service.com")
                .build();
    }

    // Bean 2: Identifiable by the name "secondaryApiClient"
    @Bean("secondaryApiClient")
    public RestClient secondaryClient() {
        return RestClient.builder()
                .baseUrl("https://api.secondary-service.com")
                .build();
    }

    // Expose the Builder as a Prototype Bean since RestClient.Builder is not
    // automatically initialized and managed by Spring and is not thread-safe

    // @Scope("prototype"): This is CRITICAL. It means "give me a new builder every
    // time someone asks." This prevents different parts of server app from
    // accidentally sharing the same builder and overwriting each other's Base URLs
    // or headers.

    // @Bean
    // @org.springframework.context.annotation.Scope("prototype")
    // public RestClient.Builder restClientBuilder() {
    // return RestClient.builder();
    // }
}
