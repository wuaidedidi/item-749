package com.laundry.service;

import com.laundry.entity.Customer;
import com.laundry.util.PageResult;

import java.util.List;

/**
 * Customer Service Interface
 */
public interface CustomerService {

    /**
     * Get customer by ID
     */
    Customer getById(Long id);

    /**
     * Get customer by phone
     */
    Customer getByPhone(String phone);

    /**
     * Get paginated customer list
     */
    PageResult<Customer> getList(String keyword, int page, int pageSize);

    /**
     * Get all customers (for dropdowns)
     */
    List<Customer> getAllList();

    /**
     * Create new customer
     */
    Customer create(Customer customer);

    /**
     * Update customer
     */
    Customer update(Customer customer);

    /**
     * Delete customer
     */
    void delete(Long customerId);

    /**
     * Count total customers
     */
    long count();
}
