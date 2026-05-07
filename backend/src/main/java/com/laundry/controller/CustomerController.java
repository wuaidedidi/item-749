package com.laundry.controller;

import com.laundry.entity.Customer;
import com.laundry.service.CustomerService;
import com.laundry.util.PageResult;
import com.laundry.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Customer Management Controller
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    /**
     * Get customer list
     */
    @GetMapping
    public Result<PageResult<Customer>> getList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<Customer> result = customerService.getList(keyword, page, pageSize);
        return Result.success(result);
    }

    /**
     * Get all customers (for dropdown)
     */
    @GetMapping("/all")
    public Result<List<Customer>> getAll() {
        List<Customer> list = customerService.getAllList();
        return Result.success(list);
    }

    /**
     * Get customer by ID
     */
    @GetMapping("/{id}")
    public Result<Customer> getById(@PathVariable Long id) {
        Customer customer = customerService.getById(id);
        if (customer == null) {
            return Result.notFound("Customer not found");
        }
        return Result.success(customer);
    }

    /**
     * Create new customer
     */
    @PostMapping
    public Result<Customer> create(@Valid @RequestBody Customer customer) {
        Customer created = customerService.create(customer);
        return Result.success("Customer created successfully", created);
    }

    /**
     * Update customer
     */
    @PutMapping("/{id}")
    public Result<Customer> update(@PathVariable Long id, @RequestBody Customer customer) {
        customer.setId(id);
        Customer updated = customerService.update(customer);
        return Result.success("Customer updated successfully", updated);
    }

    /**
     * Delete customer
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return Result.success("Customer deleted successfully", null);
    }
}
