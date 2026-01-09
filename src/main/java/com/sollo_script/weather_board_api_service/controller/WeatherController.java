package com.sollo_script.weather_board_api_service.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sollo_script.weather_board_api_service.dto.AirPollutionResponse;
import com.sollo_script.weather_board_api_service.dto.GeocodeResponse;
import com.sollo_script.weather_board_api_service.dto.OpenWeatherResponse;
import com.sollo_script.weather_board_api_service.service.WeatherService;

@Controller
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * Maps to getWeather in jsApiSample.ts
     */
    @GetMapping("/weather")
    @ResponseBody
    public ResponseEntity<OpenWeatherResponse> getWeather(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam Optional<String> apiKey) {
        return ResponseEntity.ok(weatherService.getWeather(lat, lon, apiKey));
    }

    /**
     * Maps to getGeocode in jsApiSample.ts
     */
    @GetMapping("/geocode")
    @ResponseBody
    public ResponseEntity<List<GeocodeResponse>> getGeocode(
            @RequestParam String location,
            @RequestParam Optional<String> apiKey) {
        return ResponseEntity.ok(weatherService.getGeocode(location, apiKey));
    }

    /**
     * Maps to getAirPollution in jsApiSample.ts
     */
    @GetMapping("/air_pollution")
    @ResponseBody
    public ResponseEntity<AirPollutionResponse> getAirPollution(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam Optional<String> apiKey) {
        return ResponseEntity.ok(weatherService.getAirPollution(lat, lon, apiKey));
    }
}