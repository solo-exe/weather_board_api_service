package com.sollo_script.weather_board_api_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private String status;
    private int code;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .status("OK")
                .code(HttpStatus.OK.value())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(int code, T data) {
        return ApiResponse.<T>builder()
                .status("ERROR")
                .code(code)
                .data(data)
                .build();
    }
}
