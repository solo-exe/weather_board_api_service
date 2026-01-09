package com.sollo_script.weather_board_api_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ApiClientConfig {

    @Value("${openweathermap.api.base-url}")
    private String openWeatherMapbaseUrl;

    @Bean("openWeatherMap")
    public RestClient openWeatherClient() {
        return RestClient.builder()
                .baseUrl(this.openWeatherMapbaseUrl)
                .requestFactory(new SimpleClientHttpRequestFactory() {
                    {
                        setConnectTimeout(5000);
                        setReadTimeout(5000);
                    }
                })
                .build();
    }
}
