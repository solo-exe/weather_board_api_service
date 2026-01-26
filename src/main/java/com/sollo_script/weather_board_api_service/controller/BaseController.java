package com.sollo_script.weather_board_api_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sollo_script.weather_board_api_service.dto.ApiResponse;

@Controller
@RequestMapping()
public class BaseController {

    @GetMapping("/health")
    @ResponseBody
    public ResponseEntity<ApiResponse<String>> checkHealth() {
        return ResponseEntity.ok(ApiResponse.success("Success"));
    }
}
