package com.laundry.service;

import com.laundry.dto.LoginRequest;
import com.laundry.dto.LoginResponse;
import com.laundry.dto.PasswordChangeRequest;
import com.laundry.dto.RegisterRequest;
import com.laundry.entity.User;
import com.laundry.util.PageResult;

/**
 * User Service Interface
 */
public interface UserService {

    /**
     * User login
     */
    LoginResponse login(LoginRequest request);

    /**
     * User registration
     */
    User register(RegisterRequest request);

    /**
     * Get user by ID
     */
    User getById(Long id);

    /**
     * Get user by username
     */
    User getByUsername(String username);

    /**
     * Get paginated user list
     */
    PageResult<User> getList(String keyword, Integer role, Integer status, int page, int pageSize);

    /**
     * Create new user
     */
    User create(User user);

    /**
     * Update user info
     */
    User update(User user);

    /**
     * Update user profile (self)
     */
    User updateProfile(Long userId, User user);

    /**
     * Change password
     */
    void changePassword(Long userId, PasswordChangeRequest request);

    /**
     * Reset password (admin only)
     */
    void resetPassword(Long userId, String newPassword);

    /**
     * Update user status
     */
    void updateStatus(Long userId, Integer status);

    /**
     * Delete user
     */
    void delete(Long userId);
}
