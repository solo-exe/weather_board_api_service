package com.sollo_script.weather_board_api_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// Using Java 16+ Records for immutable data carriers.
// Minimal boilerplate and built-in toString, hashCode, equals.
public record AirPollutionResponse(
        Coord coord,
        List<Measurement> list) {
    public record Coord(double lon, double lat) {
    }

    public record Measurement(
            long dt,
            Main main,
            Components components) {
    }

    public record Main(int aqi) {
    }

    public record Components(
            double co,
            double no,
            double no2,
            double o3,
            double so2,
            @JsonProperty("pm2_5") double pm2_5,
            double pm10,
            double nh3) {
    }
}
