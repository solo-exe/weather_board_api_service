package com.sollo_script.weather_board_api_service.exception.error;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;

    public BaseException(String message, HttpStatus status, String errorCode) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
}
