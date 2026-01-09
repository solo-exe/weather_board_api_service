package com.sollo_script.weather_board_api_service.exception.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerException extends BaseException {

    public InternalServerException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");
    }

    public InternalServerException(String message, String errorCode) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR, errorCode);
    }
}
