package com.sollo_script.weather_board_api_service.service;

import java.util.List;
import java.util.Optional;

import com.sollo_script.weather_board_api_service.dto.AirPollutionResponse;
import com.sollo_script.weather_board_api_service.dto.GeocodeResponse;
import com.sollo_script.weather_board_api_service.dto.OpenWeatherResponse;

public interface WeatherService {
    /**
     * Fetches weather data for a specific location.
     * This method is subject to a daily call limit.
     */
    OpenWeatherResponse getWeather(double lat, double lon, Optional<String> apiKey);

    /**
     * Fetches geocoding data for a location name.
     */
    List<GeocodeResponse> getGeocode(String location, Integer limit, Optional<String> apiKey);

    /**
     * Fetches air pollution data for a specific location.
     */
    AirPollutionResponse getAirPollution(double lat, double lon, Optional<String> apiKey);

    /**
     * Fetches map layer tile image for a specific map type and tile coordinates.
     * Returns the image as a byte array.
     */
    byte[] getMapLayerImage(String mapType, int z, int x, int y, Optional<String> apiKey);
}
