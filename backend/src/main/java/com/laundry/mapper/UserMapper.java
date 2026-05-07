package com.laundry.mapper;

import com.laundry.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * User Mapper Interface
 */
public interface UserMapper {
    
    /**
     * Find user by ID
     */
    User findById(@Param("id") Long id);

    /**
     * Find user by username
     */
    User findByUsername(@Param("username") String username);

    /**
     * Find all users with optional filters
     */
    List<User> findAll(@Param("keyword") String keyword, 
                       @Param("role") Integer role,
                       @Param("status") Integer status,
                       @Param("offset") int offset, 
                       @Param("limit") int limit);

    /**
     * Count users with filters
     */
    long count(@Param("keyword") String keyword,
               @Param("role") Integer role,
               @Param("status") Integer status);

    /**
     * Insert new user
     */
    int insert(User user);

    /**
     * Update user info
     */
    int update(User user);

    /**
     * Update password
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * Update status
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * Delete user by ID
     */
    int deleteById(@Param("id") Long id);

    /**
     * Check if username exists
     */
    boolean existsByUsername(@Param("username") String username);
}
