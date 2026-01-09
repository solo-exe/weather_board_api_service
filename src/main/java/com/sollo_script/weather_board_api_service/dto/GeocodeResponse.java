package com.sollo_script.weather_board_api_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

// Using Java 16+ Records.
public record GeocodeResponse(
        String name,
        @JsonProperty("local_names") java.util.Map<String, String> localNames,
        double lat,
        double lon,
        String country,
        String state) {
}
