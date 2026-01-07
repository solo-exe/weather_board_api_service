package com.sollo_script.weather_board_api_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ApiClientConfig {

    @Bean("openWeatherMap")
    public RestClient openWeatherClient () {
        return RestClient.builder()
                .baseUrl("https://api.openweathermap.org")
                .requestFactory(new org.springframework.http.client.SimpleClientHttpRequestFactory() {{
                    setConnectTimeout(5000);
                    setReadTimeout(5000);
                }})
                .build();
    }
}
