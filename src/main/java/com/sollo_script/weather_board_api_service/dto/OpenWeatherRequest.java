package com.sollo_script.weather_board_api_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpenWeatherRequest {
    private Double lat;
    private Double lon;
    private String q;
    private String units;
    private String exclude;
    private Integer limit;
    private String appid;

    public static OpenWeatherRequest weatherRequest(double lat, double lon, String apiKey) {
        return OpenWeatherRequest.builder()
                .lat(lat)
                .lon(lon)
                .units("imperial")
                .exclude("minutely,alerts")
                .appid(apiKey)
                .build();
    }

    public static OpenWeatherRequest geocodeRequest(String location, String apiKey) {
        return OpenWeatherRequest.builder()
                .q(location)
                .limit(1)
                .appid(apiKey)
                .build();
    }

    public static OpenWeatherRequest airPollutionRequest(double lat, double lon, String apiKey) {
        return OpenWeatherRequest.builder()
                .lat(lat)
                .lon(lon)
                .appid(apiKey)
                .build();
    }
}
