package com.sollo_script.weather_board_api_service.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.sollo_script.weather_board_api_service.dto.AirPollutionResponse;
import com.sollo_script.weather_board_api_service.dto.GeocodeResponse;
import com.sollo_script.weather_board_api_service.dto.OpenWeatherResponse;
import com.sollo_script.weather_board_api_service.entity.DailyLog;
import com.sollo_script.weather_board_api_service.exception.error.TooManyRequestsException;

// ===========================================
// MYSQL/JPA IMPORT - COMMENTED OUT
// ===========================================
// import com.sollo_script.weather_board_api_service.repository.DailyLogRepository;

// ===========================================
// JSON FILE STORAGE - ACTIVE
// ===========================================
import com.sollo_script.weather_board_api_service.repository.JsonDailyLogRepository;

import com.sollo_script.weather_board_api_service.service.WeatherService;

import tools.jackson.databind.ObjectMapper;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Value("${openweathermap.api.apikey}")
    private String internalApiKey;

    private final JsonDailyLogRepository dailyLogRepository;

    private final RestClient openWeatherRestClient;
    private final RestClient openWeatherTileRestClient;

    public WeatherServiceImpl(
            JsonDailyLogRepository apiLogRepository,

            @Qualifier("openWeatherMap") RestClient openWeatherClient,
            @Qualifier("openWeatherMapTile") RestClient openWeatherMapTileClient) {
        this.dailyLogRepository = apiLogRepository;
        this.openWeatherRestClient = openWeatherClient;
        this.openWeatherTileRestClient = openWeatherMapTileClient;
    }

    private DailyLog fetchDayLog() {
        var today = LocalDate.now();
        var apiName = "OpenWeather";

        return dailyLogRepository
                .findByApiNameAndUsageDate(apiName, today)
                .orElseGet(() -> {
                    var newLog = new DailyLog();
                    newLog.setApiName(apiName);
                    newLog.setUsageDate(today);
                    newLog.setCallCount(0L);
                    return newLog;
                });
    }

    @Override
    public OpenWeatherResponse getWeather(double lat, double lon, Optional<String> apiKey) {
        var currentLog = this.fetchDayLog();
        var currentCount = currentLog.getCallCount();

        if (apiKey.isEmpty() || apiKey.get().equals(internalApiKey)) {
            // Limit to 900 calls per day as requested
            if (currentCount >= 900) {
                throw new TooManyRequestsException("Daily API limit exceeded");
            }

            currentLog.setCallCount(currentCount + 1);
            dailyLogRepository.save(currentLog);
        }

        // Synchronous call to external API
        var openWeatherResponse = openWeatherRestClient.get()
                .uri(uriBuilder -> {
                    var finalUri = uriBuilder
                            .path("/data/3.0/onecall")
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("units", "metric")
                            .queryParam("exclude", "minutely,alerts")
                            .queryParam("appid", apiKey.orElse(internalApiKey))
                            .build();
                    // System.out.println("Calling URI: " + finalUri);
                    return finalUri;
                })
                .retrieve()
                .body(String.class);

        return new ObjectMapper().readValue(openWeatherResponse, OpenWeatherResponse.class);
    }

    @Override
    public List<GeocodeResponse> getGeocode(String location, Integer limit, Optional<String> apiKey) {
        System.out.println("Requesting Geocode for: " + location);
        var geoCodeResponse = openWeatherRestClient.get()
                .uri(uriBuilder -> {
                    var finalUri = uriBuilder
                            .path("/geo/1.0/direct")
                            .queryParam("q", location)
                            .queryParam("limit", limit)
                            .queryParam("appid", apiKey.orElse(internalApiKey))
                            .build();
                    System.out.println("Calling URI: " + finalUri);
                    return finalUri;
                })
                .retrieve()
                .body(new ParameterizedTypeReference<@NonNull List<GeocodeResponse>>() {
                });
        return geoCodeResponse;
    }

    @Override
    public AirPollutionResponse getAirPollution(double lat, double lon, Optional<String> apiKey) {
        return openWeatherRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/2.5/air_pollution")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", apiKey.orElse(internalApiKey))
                        .build())
                .retrieve()
                .body(AirPollutionResponse.class);
    }

    @Override
    public byte[] getMapLayerImage(String mapType, int z, int x, int y, Optional<String> apiKey) {
        var imageBytes = openWeatherTileRestClient.get()
                .uri(uriBuilder -> {
                    var finalUri = uriBuilder
                            .path("/{mapType}/{z}/{x}/{y}.png")
                            .queryParam("appid", apiKey.orElse(internalApiKey))
                            .build(mapType, z, x, y);
                    System.out.println("Calling Tile URI: " + finalUri);
                    return finalUri;
                })
                .retrieve()
                .body(byte[].class);

        return imageBytes;
    }
}
