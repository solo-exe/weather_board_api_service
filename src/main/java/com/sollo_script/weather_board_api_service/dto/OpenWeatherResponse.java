package com.sollo_script.weather_board_api_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record OpenWeatherResponse(
        double lat,
        double lon,
        String timezone,
        @JsonProperty("timezone_offset") int timezoneOffset,
        Current current) {

    public record Current(
            long dt,
            long sunrise,
            long sunset,
            double temp,
            @JsonProperty("feels_like") double feelsLike,
            int pressure,
            int humidity,
            @JsonProperty("dew_point") double dewPoint,
            double uvi,
            int clouds,
            int visibility,
            @JsonProperty("wind_speed") double windSpeed,
            @JsonProperty("wind_deg") int windDeg,
            List<Weather> weather) {
    }

    public record Weather(
            int id,
            String main,
            String description,
            String icon) {
    }
}