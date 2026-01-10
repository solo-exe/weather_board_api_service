package com.sollo_script.weather_board_api_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenWeatherResponse(
                Double lat,
                Double lon,
                String timezone,
                @JsonProperty("timezone_offset") Integer timezoneOffset,
                Current current,
                List<Hourly> hourly,
                List<Daily> daily) {

        public record Current(
                        Long dt,
                        Long sunrise,
                        Long sunset,
                        Double temp,
                        @JsonProperty("feels_like") Double feelsLike,
                        Integer pressure,
                        Integer humidity,
                        @JsonProperty("dew_point") Double dewPoint,
                        Double uvi,
                        Integer clouds,
                        Integer visibility,
                        @JsonProperty("wind_speed") Double windSpeed,
                        @JsonProperty("wind_deg") Integer windDeg,
                        List<Weather> weather) {
        }

        public record Hourly(
                        Long dt,
                        Double temp,
                        @JsonProperty("feels_like") Double feelsLike,
                        Integer pressure,
                        Integer humidity,
                        @JsonProperty("dew_point") Double dewPoint,
                        Double uvi,
                        Integer clouds,
                        Integer visibility,
                        @JsonProperty("wind_speed") Double windSpeed,
                        @JsonProperty("wind_deg") Integer windDeg,
                        List<Weather> weather,
                        Double pop) {
        }

        public record Daily(
                        Long dt,
                        Long sunrise,
                        Long sunset,
                        Long moonrise,
                        Long moonset,
                        @JsonProperty("moon_phase") Double moonPhase,
                        String summary,
                        Temp temp,
                        @JsonProperty("feels_like") FeelsLike feelsLike,
                        Integer pressure,
                        Integer humidity,
                        @JsonProperty("dew_point") Double dewPoint,
                        @JsonProperty("wind_speed") Double windSpeed,
                        @JsonProperty("wind_deg") Integer windDeg,
                        @JsonProperty("wind_gust") Double windGust,
                        List<Weather> weather,
                        Integer clouds,
                        Double pop,
                        Double rain,
                        Double uvi) {

                public record Temp(
                                Double day,
                                Double min,
                                Double max,
                                Double night,
                                Double eve,
                                Double morn) {
                }

                public record FeelsLike(
                                Double day,
                                Double night,
                                Double eve,
                                Double morn) {
                }
        }

        public record Weather(
                        Integer id,
                        String main,
                        String description,
                        String icon) {
        }
}