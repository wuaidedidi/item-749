package com.laundry.mapper;

import com.laundry.entity.OrderDetail;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Order Detail Mapper Interface
 */
public interface OrderDetailMapper {

    /**
     * Find detail by ID
     */
    OrderDetail findById(@Param("id") Long id);

    /**
     * Find all details by order ID
     */
    List<OrderDetail> findByOrderId(@Param("orderId") Long orderId);

    /**
     * Insert new detail
     */
    int insert(OrderDetail detail);

    /**
     * Batch insert details
     */
    int batchInsert(@Param("list") List<OrderDetail> details);

    /**
     * Update detail
     */
    int update(OrderDetail detail);

    /**
     * Delete detail by ID
     */
    int deleteById(@Param("id") Long id);

    /**
     * Delete all details by order ID
     */
    int deleteByOrderId(@Param("orderId") Long orderId);

    /**
     * Count order details by service ID
     */
    long countByServiceId(@Param("serviceId") Long serviceId);
}
