package com.sollo_script.weather_board_api_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ApiClientConfig {

    @Bean("openWeatherMap")
    public RestClient openWeatherClient() {
        return RestClient.builder()
                .baseUrl("https://api.openweathermap.org")
                .requestFactory(new SimpleClientHttpRequestFactory() {
                    {
                        setConnectTimeout(10000);
                        setReadTimeout(10000);
                    }
                })
                .build();
    }

    @Bean("openWeatherMapTile")
    public RestClient openWeatherMapTileClient() {
        return RestClient.builder()
                .baseUrl("https://tile.openweathermap.org/map")
                .requestFactory(new SimpleClientHttpRequestFactory() {
                    {
                        setConnectTimeout(10000);
                        setReadTimeout(10000);
                    }
                })
                .build();
    }

    @Bean
    @org.springframework.context.annotation.Scope("prototype")
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
