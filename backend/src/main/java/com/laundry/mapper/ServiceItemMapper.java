package com.laundry.mapper;

import com.laundry.entity.ServiceItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Service Item Mapper Interface
 */
public interface ServiceItemMapper {

    /**
     * Find service by ID
     */
    ServiceItem findById(@Param("id") Long id);

    /**
     * Find all services with optional filters
     */
    List<ServiceItem> findAll(@Param("keyword") String keyword,
                              @Param("category") String category,
                              @Param("status") Integer status,
                              @Param("offset") int offset,
                              @Param("limit") int limit);

    /**
     * Get all active services (no pagination)
     */
    List<ServiceItem> findAllActive();

    /**
     * Count services with filters
     */
    long count(@Param("keyword") String keyword,
               @Param("category") String category,
               @Param("status") Integer status);

    /**
     * Insert new service
     */
    int insert(ServiceItem serviceItem);

    /**
     * Update service
     */
    int update(ServiceItem serviceItem);

    /**
     * Update status
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * Delete service by ID
     */
    int deleteById(@Param("id") Long id);

    /**
     * Get distinct categories
     */
    List<String> findAllCategories();
}
