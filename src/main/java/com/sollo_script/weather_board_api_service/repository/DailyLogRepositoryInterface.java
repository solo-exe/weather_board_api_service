package com.sollo_script.weather_board_api_service.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.sollo_script.weather_board_api_service.entity.DailyLog;

/**
 * Common interface for DailyLog repository implementations.
 * Allows switching between JPA (MySQL) and JSON file storage.
 */
public interface DailyLogRepositoryInterface {

    Optional<DailyLog> findOneByUsageDate(LocalDate today);

    List<DailyLog> findByUsageDate(LocalDate today);

    Optional<DailyLog> findByApiNameAndUsageDate(String apiName, LocalDate usageDate);

    DailyLog save(DailyLog dailyLog);

    List<DailyLog> findAll();

    void deleteById(Long id);
}