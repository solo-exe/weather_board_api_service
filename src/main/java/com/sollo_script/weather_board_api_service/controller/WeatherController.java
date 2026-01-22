package com.sollo_script.weather_board_api_service.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sollo_script.weather_board_api_service.dto.AirPollutionResponse;
import com.sollo_script.weather_board_api_service.dto.ApiResponse;
import com.sollo_script.weather_board_api_service.exception.error.TooManyRequestsException;
import jakarta.servlet.http.HttpServletRequest;
import io.github.bucket4j.Bucket;
import com.sollo_script.weather_board_api_service.dto.GeocodeResponse;
import com.sollo_script.weather_board_api_service.dto.OpenWeatherResponse;
import com.sollo_script.weather_board_api_service.service.RateLimitService;
import com.sollo_script.weather_board_api_service.service.WeatherService;

@Controller
@RequestMapping("/openweather")
public class WeatherController {

    private final WeatherService weatherService;
    private final RateLimitService rateLimitService;

    public WeatherController(WeatherService weatherService, RateLimitService rateLimitService) {
        this.weatherService = weatherService;
        this.rateLimitService = rateLimitService;
    }

    @GetMapping("/weather")
    @ResponseBody
    public ResponseEntity<ApiResponse<OpenWeatherResponse>> getWeather(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam Optional<String> apiKey,
            HttpServletRequest request) {
        Bucket bucket = rateLimitService.resolveBucket(request.getRemoteAddr());
        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(ApiResponse.success(weatherService.getWeather(lat, lon, apiKey)));
        } else {
            throw new TooManyRequestsException("Rate limit of 2 requests per second exceeded");
        }
    }

    @GetMapping("/geocode")
    @ResponseBody
    public ResponseEntity<ApiResponse<List<GeocodeResponse>>> getGeocode(
            @RequestParam String location,
            @RequestParam Integer limit,
            @RequestParam Optional<String> apiKey) {
        return ResponseEntity.ok(ApiResponse.success(weatherService.getGeocode(location, limit, apiKey)));
    }

    @GetMapping("/air_pollution")
    @ResponseBody
    public ResponseEntity<ApiResponse<AirPollutionResponse>> getAirPollution(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam Optional<String> apiKey) {
        return ResponseEntity.ok(ApiResponse.success(weatherService.getAirPollution(lat, lon, apiKey)));
    }

    @GetMapping(value = "/map_layer/{mapType}/{z}/{x}/{y}", produces = "image/png")
    @ResponseBody
    public ResponseEntity<byte[]> getMapLayer(
            @PathVariable String mapType,
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y,
            @RequestParam Optional<String> apiKey) {

        byte[] imageBytes = weatherService.getMapLayerImage(mapType, z, x, y, apiKey);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .contentLength(imageBytes.length)
                .body(imageBytes);
    }
}