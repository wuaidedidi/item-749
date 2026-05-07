package com.laundry.controller;

import com.laundry.entity.ServiceItem;
import com.laundry.service.ServiceItemService;
import com.laundry.util.PageResult;
import com.laundry.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Service Item Management Controller
 */
@RestController
@RequestMapping("/api/services")
public class ServiceItemController {
    private static final Logger logger = LoggerFactory.getLogger(ServiceItemController.class);

    @Autowired
    private ServiceItemService serviceItemService;

    /**
     * Get service list
     */
    @GetMapping
    public Result<PageResult<ServiceItem>> getList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<ServiceItem> result = serviceItemService.getList(keyword, category, status, page, pageSize);
        return Result.success(result);
    }

    /**
     * Get all active services (for dropdown)
     */
    @GetMapping("/active")
    public Result<List<ServiceItem>> getActive() {
        List<ServiceItem> list = serviceItemService.getAllActive();
        return Result.success(list);
    }

    /**
     * Get all categories
     */
    @GetMapping("/categories")
    public Result<List<String>> getCategories() {
        List<String> categories = serviceItemService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * Get service by ID
     */
    @GetMapping("/{id}")
    public Result<ServiceItem> getById(@PathVariable Long id) {
        ServiceItem service = serviceItemService.getById(id);
        if (service == null) {
            return Result.notFound("Service not found");
        }
        return Result.success(service);
    }

    /**
     * Create new service
     */
    @PostMapping
    public Result<ServiceItem> create(@Valid @RequestBody ServiceItem service) {
        ServiceItem created = serviceItemService.create(service);
        return Result.success("Service created successfully", created);
    }

    /**
     * Update service
     */
    @PutMapping("/{id}")
    public Result<ServiceItem> update(@PathVariable Long id, @RequestBody ServiceItem service) {
        service.setId(id);
        ServiceItem updated = serviceItemService.update(service);
        return Result.success("Service updated successfully", updated);
    }

    /**
     * Update service status
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        serviceItemService.updateStatus(id, status);
        return Result.success("Status updated successfully", null);
    }

    /**
     * Delete service
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        serviceItemService.delete(id);
        return Result.success("Service deleted successfully", null);
    }
}
