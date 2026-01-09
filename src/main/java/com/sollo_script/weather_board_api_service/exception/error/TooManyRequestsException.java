package com.sollo_script.weather_board_api_service.exception.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
public class TooManyRequestsException extends BaseException {

    public TooManyRequestsException(String message) {
        super(message, HttpStatus.TOO_MANY_REQUESTS, "TOO_MANY_REQUESTS");
    }

    public TooManyRequestsException(String message, String errorCode) {
        super(message, HttpStatus.TOO_MANY_REQUESTS, errorCode);
    }
}
