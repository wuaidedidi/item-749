package com.laundry.controller;

import com.laundry.dto.LoginRequest;
import com.laundry.dto.LoginResponse;
import com.laundry.dto.RegisterRequest;
import com.laundry.entity.User;
import com.laundry.service.UserService;
import com.laundry.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Authentication Controller
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    /**
     * User login
     */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Login request received for user: {}", request.getUsername());
        LoginResponse response = userService.login(request);
        return Result.success("Login successful", response);
    }

    /**
     * User registration
     */
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Registration request received for user: {}", request.getUsername());
        User user = userService.register(request);
        return Result.success("Registration successful", user);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("OK");
    }
}
