package com.sollo_script.weather_board_api_service.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import com.sollo_script.weather_board_api_service.entity.DailyLog;
import com.sollo_script.weather_board_api_service.exception.error.InternalServerException;

import jakarta.annotation.PostConstruct;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Repository;

@Repository
public class JsonDailyLogRepository implements DailyLogRepositoryInterface {

    /**
     * Path to the JSON file where logs are stored.
     * The file will be created in the project root directory.
     */
    private static final String JSON_FILE_PATH = "data/daily_logs.json";

    /**
     * Maximum age of records in days. Records older than this will be removed.
     */
    private static final int MAX_RECORD_AGE_DAYS = 30;

    /**
     * From Jackson library to convert Java objects to JSON and vice versa.
     */
    private final ObjectMapper objectMapper;

    /**
     * Path object representing the JSON file location.
     * Path is part of java.nio.file and provides file system operations.
     */
    private final Path filePath;

    /**
     * Thread-safe counter for generating unique IDs.
     * AtomicLong ensures thread-safety when multiple requests happen
     * simultaneously.
     */
    private final AtomicLong idCounter;

    @PostConstruct
    public void init() {
        try {

            Path parentDir = filePath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            if (!Files.exists(filePath)) {
                Files.writeString(filePath, "[]");
            }

            List<DailyLog> existingLogs = readAllLogs();

            long maxId = existingLogs.stream()
                    .filter(log -> log.getId() != null).mapToLong(DailyLog::getId).max().orElse(0L);

            idCounter.set(maxId + 1);

            // Clean up old records on startup
            removeOldRecords();

        } catch (IOException e) {
            throw new RuntimeException("Failed to initialze JSON repository", e);
        }
    }

    /**
     * Fetch a list of DailyLogs by API name and usage date.
     * This is equivalent to: SELECT * FROM daily_calls WHERE api_name = ? AND
     * usage_date >= ?
     * 
     * @param apiName   The name of the API (e.g., "OpenWeather")
     * @param usageDate The date to search for
     * @return Optional containing the log if found, empty Optional otherwise
     */
    public Optional<DailyLog> findByApiNameAndUsageDate(String apiName, LocalDate usageDate) {

        List<DailyLog> logs = readAllLogs();
        return logs.stream()
                .filter(log -> log.getApiName().equals(apiName)
                        && (log.getUsageDate().isEqual(usageDate) || log.getUsageDate().isAfter(usageDate)))
                .findFirst();
    }

    /**
     * Fetch list of DailyLog by usage date.
     * 
     * @param today The date to search for
     * @return Optional containing the first matching log
     */
    public Optional<DailyLog> findOneByUsageDate(LocalDate today) {
        List<DailyLog> logs = readAllLogs();

        return logs.stream().filter(log -> log.getUsageDate().isEqual(today) || log.getUsageDate().isAfter(today))
                .findFirst();
    }

    /**
     * Find a single DailyLog by usage date.
     * 
     * @param today The date to search for
     * @return List of matching logs
     */
    public List<DailyLog> findByUsageDate(LocalDate today) {
        List<DailyLog> logs = readAllLogs();

        return logs.stream().filter(log -> log.getUsageDate().isEqual(today) || log.getUsageDate().isAfter(today))
                .toList();
    }

    public DailyLog save(DailyLog dailyLog) {
        try {
            List<DailyLog> logs = readAllLogs();

            if (dailyLog.getId() == null) {
                dailyLog.setId(idCounter.getAndIncrement());
                logs.add(dailyLog);
            } else {
                boolean found = false;
                for (int i = 0; i < logs.size(); i++) {
                    if (logs.get(i).getId().equals(dailyLog.getId())) {
                        logs.set(i, dailyLog);
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    logs.add(dailyLog);
                }
            }

            logs = removeOldRecordsFromList(logs);
            writeAllLogs(logs);

            return dailyLog;

        } catch (Exception e) {
            throw new InternalServerException("Failed to create log api call entry", e.toString());
        }
    }

    /**
     * Get all Daily log records
     * 
     * @return List of logs in the last 30 days
     */
    public List<DailyLog> findAll() {
        return readAllLogs();
    }

    public void deleteById(Long id) {
        List<DailyLog> logs = readAllLogs();

        logs.removeIf(log -> log.getId().equals(id));
        writeAllLogs(logs);
    }

    public JsonDailyLogRepository() {
        // Jackson 3.x has built-in Java 8 date/time support, no module registration
        // needed
        this.objectMapper = new ObjectMapper();

        // Set up the file path
        this.filePath = Paths.get(JSON_FILE_PATH);

        // Initialize ID counter to 0 (will be updated in init())
        this.idCounter = new AtomicLong(0);
    }

    private List<DailyLog> readAllLogs() {
        try {

            if (!Files.exists(filePath)) {
                return new ArrayList<>();
            }

            String content = Files.readString(filePath);

            if (content.isBlank()) {
                return new ArrayList<>();
            }

            return objectMapper.readValue(content, new TypeReference<List<DailyLog>>() {
            });

        } catch (Exception e) {
            System.err.println("Error reading daily logs: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void removeOldRecords() {
        List<DailyLog> logs = readAllLogs();
        List<DailyLog> filteredLogs = removeOldRecordsFromList(logs);

        if (filteredLogs.size() != logs.size()) {
            writeAllLogs(filteredLogs);
            System.out.println("Removed " + (logs.size() - filteredLogs.size()) + " old records from daily_logs.json");
        }
    }

    private List<DailyLog> removeOldRecordsFromList(List<DailyLog> logs) {
        // Calculate the cutoff date (30 days ago)x
        LocalDate cutOffDate = LocalDate.now().minusDays(MAX_RECORD_AGE_DAYS);

        // Keep only records where usageDate is after or equal to cutoffDate
        return logs.stream().filter(log -> {

            // isBefore() returns true if this date is before the specified date
            // We want to KEEP records that are NOT before the cutoff
            // So we negate: !log.getUsageDate().isBefore(cutoffDate)
            // Which means: keep if usageDate >= cutoffDate
            return !log.getUsageDate().isBefore(cutOffDate);
        }).toList();
    }

    private void writeAllLogs(List<DailyLog> logs) {
        try {
            // Serialize List<DailyLog> to JSON string
            String jsonContent = objectMapper.writeValueAsString(logs);

            // Write to file (overwrites existing content)
            Files.writeString(filePath, jsonContent);
        } catch (Exception e) {
            throw new InternalServerException("Failed to write daily logs to JSON file", JSON_FILE_PATH);
        }
    }

}
