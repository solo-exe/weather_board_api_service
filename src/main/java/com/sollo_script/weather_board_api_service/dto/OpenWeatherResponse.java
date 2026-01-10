package com.sollo_script.weather_board_api_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenWeatherResponse(
                double lat,
                double lon,
                String timezone,
                @JsonProperty("timezone_offset") int timezoneOffset,
                Current current,
                List<Hourly> hourly,
                List<Daily> daily) {

        public record Hourly(
                        long dt,
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
                        List<Weather> weather,
                        double pop) {
        }

        public record Daily(
                        long dt,
                        long sunrise,
                        long sunset,
                        long moonrise,
                        long moonset,
                        @JsonProperty("moon_phase") double moonPhase,
                        String summary,
                        Temp temp,
                        @JsonProperty("feels_like") FeelsLike feelsLike,
                        int pressure,
                        int humidity,
                        @JsonProperty("dew_point") double dewPoint,
                        @JsonProperty("wind_speed") double windSpeed,
                        @JsonProperty("wind_deg") int windDeg,
                        @JsonProperty("wind_gust") double windGust,
                        List<Weather> weather,
                        int clouds,
                        double pop,
                        double rain,
                        double uvi) {

                public record Temp(
                                double day,
                                double min,
                                double max,
                                double night,
                                double eve,
                                double morn) {
                }

                public record FeelsLike(
                                double day,
                                double night,
                                double eve,
                                double morn) {
                }
        }

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