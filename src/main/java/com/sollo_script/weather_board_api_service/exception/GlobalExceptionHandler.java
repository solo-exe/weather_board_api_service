package com.sollo_script.weather_board_api_service.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.sollo_script.weather_board_api_service.dto.ApiResponse;
import com.sollo_script.weather_board_api_service.exception.error.BaseException;

import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

        /**
         * Creates HTTP headers with Content-Type set to application/json.
         * This ensures error responses are always returned as JSON,
         * even when the original request expected a different content type (e.g.,
         * image/png).
         */
        private HttpHeaders createJsonHeaders() {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
        }

        // Handle Specific Exceptions (BaseException and its subclasses)
        @ExceptionHandler(BaseException.class)
        public ResponseEntity<ApiResponse<ErrorDetails>> handleBaseException(
                        BaseException exception,
                        WebRequest webRequest) {
                ErrorDetails errorDetails = new ErrorDetails(
                                LocalDateTime.now(),
                                exception.getMessage(),
                                webRequest.getDescription(false),
                                exception.getErrorCode());

                return new ResponseEntity<>(
                                ApiResponse.error(exception.getStatus().value(), errorDetails),
                                createJsonHeaders(),
                                exception.getStatus());
        }

        // Handle Validation Exceptions
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<ErrorDetails>> handleValidationExceptions(
                        MethodArgumentNotValidException ex,
                        WebRequest webRequest) {

                StringBuilder errors = new StringBuilder();
                ex.getBindingResult().getAllErrors().forEach(error -> {
                        String errorMessage = error.getDefaultMessage();
                        errors.append(errorMessage).append("; ");
                });

                ErrorDetails errorDetails = new ErrorDetails(
                                LocalDateTime.now(),
                                "Validation Failed: " + errors.toString(),
                                webRequest.getDescription(false),
                                "VALIDATION_ERROR");

                return new ResponseEntity<>(
                                ApiResponse.error(HttpStatus.BAD_REQUEST.value(), errorDetails),
                                createJsonHeaders(),
                                HttpStatus.BAD_REQUEST);
        }

        // Handle Generic Exceptions
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<ErrorDetails>> handleGenericException(
                        Exception exception,
                        WebRequest webRequest) {
                ErrorDetails errorDetails = new ErrorDetails(
                                LocalDateTime.now(),
                                exception.getMessage(),
                                webRequest.getDescription(false),
                                "INTERNAL_SERVER_ERROR");

                return new ResponseEntity<>(
                                ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorDetails),
                                createJsonHeaders(),
                                HttpStatus.INTERNAL_SERVER_ERROR);
        }
}
