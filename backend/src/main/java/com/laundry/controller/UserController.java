package com.laundry.controller;

import com.laundry.dto.PasswordChangeRequest;
import com.laundry.entity.User;
import com.laundry.service.UserService;
import com.laundry.util.PageResult;
import com.laundry.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * User Management Controller
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    public Result<User> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        return Result.success(user);
    }

    /**
     * Update current user profile
     */
    @PutMapping("/profile")
    public Result<User> updateProfile(HttpServletRequest request, @RequestBody User user) {
        Long userId = (Long) request.getAttribute("userId");
        User updated = userService.updateProfile(userId, user);
        return Result.success("Profile updated successfully", updated);
    }

    /**
     * Change password
     */
    @PutMapping("/password")
    public Result<Void> changePassword(HttpServletRequest request, 
                                       @Valid @RequestBody PasswordChangeRequest passwordRequest) {
        Long userId = (Long) request.getAttribute("userId");
        userService.changePassword(userId, passwordRequest);
        return Result.success("Password changed successfully", null);
    }

    /**
     * Get user list (admin only)
     */
    @GetMapping
    public Result<PageResult<User>> getList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<User> result = userService.getList(keyword, role, status, page, pageSize);
        return Result.success(result);
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.notFound("User not found");
        }
        return Result.success(user);
    }

    /**
     * Create new user (admin only)
     */
    @PostMapping
    public Result<User> create(@Valid @RequestBody User user) {
        User created = userService.create(user);
        return Result.success("User created successfully", created);
    }

    /**
     * Update user (admin only)
     */
    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        User updated = userService.update(user);
        return Result.success("User updated successfully", updated);
    }

    /**
     * Update user status (admin only)
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        userService.updateStatus(id, status);
        return Result.success("Status updated successfully", null);
    }

    /**
     * Reset user password (admin only)
     */
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newPassword = body.get("password");
        if (newPassword == null || newPassword.length() < 6) {
            return Result.badRequest("Password must be at least 6 characters");
        }
        userService.resetPassword(id, newPassword);
        return Result.success("Password reset successfully", null);
    }

    /**
     * Delete user (admin only)
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success("User deleted successfully", null);
    }
}
