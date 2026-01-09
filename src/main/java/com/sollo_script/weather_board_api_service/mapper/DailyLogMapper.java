package com.sollo_script.weather_board_api_service.mapper;

import com.sollo_script.weather_board_api_service.dto.DailyLogDto;
import com.sollo_script.weather_board_api_service.entity.DailyLog;

public class DailyLogMapper {

    public static DailyLog mapToDailyLog(DailyLogDto dailyLogDto) {
        return new DailyLog(
                dailyLogDto.id(),
                dailyLogDto.apiName(),
                dailyLogDto.usageDate(),
                dailyLogDto.callCount());
    }

    public static DailyLogDto mapToDailyLogDto(DailyLog dailyLog) {
        return new DailyLogDto(
                dailyLog.getId(),
                dailyLog.getApiName(),
                dailyLog.getUsageDate(),
                dailyLog.getCallCount());
    }
}