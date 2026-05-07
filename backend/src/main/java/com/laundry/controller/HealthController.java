package com.laundry.controller;

import com.laundry.util.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health Check Controller
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("Service is running");
    }
}
