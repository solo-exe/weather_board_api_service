package com.sollo_script.weather_board_api_service.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sollo_script.weather_board_api_service.entity.DailyLog;

public interface DailyLogRepository extends JpaRepository<DailyLog, Long> {
    Optional<DailyLog> findOneByUsageDate(LocalDate today);

    List<DailyLog> findByUsageDate(LocalDate today);

    Optional<DailyLog> findByApiNameAndUsageDate(String apiName, LocalDate usageDate);
}