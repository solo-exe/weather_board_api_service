package com.sollo_script.weather_board_api_service.dto;

import java.time.LocalDate;

public record DailyLogDto(
                Long id,
                String apiName,
                LocalDate usageDate,
                Long callCount) {
}
