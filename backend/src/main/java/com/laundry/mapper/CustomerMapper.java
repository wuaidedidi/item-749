package com.laundry.mapper;

import com.laundry.entity.Customer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Customer Mapper Interface
 */
public interface CustomerMapper {

    /**
     * Find customer by ID
     */
    Customer findById(@Param("id") Long id);

    /**
     * Find customer by phone
     */
    Customer findByPhone(@Param("phone") String phone);

    /**
     * Find all customers with optional filters
     */
    List<Customer> findAll(@Param("keyword") String keyword,
                           @Param("offset") int offset,
                           @Param("limit") int limit);

    /**
     * Get all customers (no pagination)
     */
    List<Customer> findAllList();

    /**
     * Count customers with filters
     */
    long count(@Param("keyword") String keyword);

    /**
     * Insert new customer
     */
    int insert(Customer customer);

    /**
     * Update customer
     */
    int update(Customer customer);

    /**
     * Delete customer by ID
     */
    int deleteById(@Param("id") Long id);

    /**
     * Check if phone exists
     */
    boolean existsByPhone(@Param("phone") String phone);

    /**
     * Check if phone exists for other customer
     */
    boolean existsByPhoneAndNotId(@Param("phone") String phone, @Param("id") Long id);
}
