package com.sollo_script.weather_board_api_service.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
import com.sollo_script.weather_board_api_service.repository.DailyLogRepository;
import com.sollo_script.weather_board_api_service.service.WeatherService;

import tools.jackson.databind.ObjectMapper;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Value("${openweathermap.api.apikey}")
    private String internalApiKey;

    private final DailyLogRepository dailyLogRepository;

    private final RestClient restClient;

    public WeatherServiceImpl(
            DailyLogRepository apiLogRepository,
            @Qualifier("openWeatherMap") RestClient openWeatherClient) {
        this.dailyLogRepository = apiLogRepository;
        this.restClient = openWeatherClient;
    }

    private DailyLog fetchDayLog() {
        // Using Java 10+ var for checking local variable type inference
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

        if (!apiKey.isPresent() || apiKey.get().equals(internalApiKey)) {
            // Limit to 900 calls per day as requested
            if (currentCount >= 900) {
                throw new TooManyRequestsException("Daily API limit exceeded");
            }
        }

        // Synchronous call to external API
        var response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/3.0/onecall")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("units", "imperial")
                        .queryParam("exclude", "minutely,alerts")
                        .queryParam("appid", apiKey.orElse(internalApiKey))
                        .build())
                .retrieve()
                .body(String.class);

        // System.out.println("DEBUG API RESPONSE: " + response);

        // Increment count only after successful call
        currentLog.setCallCount(currentCount + 1);
        dailyLogRepository.save(currentLog);

        return new ObjectMapper().readValue(response, OpenWeatherResponse.class);
    }

    @Override
    public List<GeocodeResponse> getGeocode(String location, Optional<String> apiKey) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/geo/1.0/direct")
                        .queryParam("q", location)
                        .queryParam("limit", 1)
                        .queryParam("appid", apiKey.orElse(internalApiKey))
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<GeocodeResponse>>() {
                });
    }

    @Override
    public AirPollutionResponse getAirPollution(double lat, double lon, Optional<String> apiKey) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/data/2.5/air_pollution")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", apiKey.orElse(internalApiKey))
                        .build())
                .retrieve()
                .body(AirPollutionResponse.class);
    }
}
