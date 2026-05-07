package com.laundry.service;

import com.laundry.entity.ServiceItem;
import com.laundry.util.PageResult;

import java.util.List;

/**
 * Service Item Service Interface
 */
public interface ServiceItemService {

    /**
     * Get service by ID
     */
    ServiceItem getById(Long id);

    /**
     * Get paginated service list
     */
    PageResult<ServiceItem> getList(String keyword, String category, Integer status, int page, int pageSize);

    /**
     * Get all active services (for dropdowns)
     */
    List<ServiceItem> getAllActive();

    /**
     * Get all categories
     */
    List<String> getAllCategories();

    /**
     * Create new service
     */
    ServiceItem create(ServiceItem service);

    /**
     * Update service
     */
    ServiceItem update(ServiceItem service);

    /**
     * Update service status
     */
    void updateStatus(Long serviceId, Integer status);

    /**
     * Delete service
     */
    void delete(Long serviceId);

    /**
     * Count active services
     */
    long countActive();
}
