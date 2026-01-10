package com.sollo_script.weather_board_api_service.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sollo_script.weather_board_api_service.dto.AirPollutionResponse;
import com.sollo_script.weather_board_api_service.dto.ApiResponse;
import com.sollo_script.weather_board_api_service.dto.GeocodeResponse;
import com.sollo_script.weather_board_api_service.dto.OpenWeatherResponse;
import com.sollo_script.weather_board_api_service.service.WeatherService;

@Controller
@RequestMapping("/openweather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    @ResponseBody
    public ResponseEntity<ApiResponse<OpenWeatherResponse>> getWeather(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam Optional<String> apiKey) {
        return ResponseEntity.ok(ApiResponse.success(weatherService.getWeather(lat, lon, apiKey)));
    }

    @GetMapping("/geocode")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<GeocodeResponse>>> getGeocode(
            @RequestParam String location,
            @RequestParam Optional<String> apiKey) {
        return ResponseEntity.ok(ApiResponse.success(weatherService.getGeocode(location, apiKey)));
    }

    @GetMapping("/air_pollution")
    @ResponseBody
    public ResponseEntity<ApiResponse<AirPollutionResponse>> getAirPollution(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam Optional<String> apiKey) {
        return ResponseEntity.ok(ApiResponse.success(weatherService.getAirPollution(lat, lon, apiKey)));
    }
}