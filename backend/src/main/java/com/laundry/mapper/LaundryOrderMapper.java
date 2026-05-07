package com.laundry.mapper;

import com.laundry.entity.LaundryOrder;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Laundry Order Mapper Interface
 */
public interface LaundryOrderMapper {

    /**
     * Find order by ID
     */
    LaundryOrder findById(@Param("id") Long id);

    /**
     * Find order by ID with details
     */
    LaundryOrder findByIdWithDetails(@Param("id") Long id);

    /**
     * Find order by order number
     */
    LaundryOrder findByOrderNo(@Param("orderNo") String orderNo);

    /**
     * Find all orders with optional filters
     */
    List<LaundryOrder> findAll(@Param("keyword") String keyword,
                               @Param("status") Integer status,
                               @Param("customerId") Long customerId,
                               @Param("startDate") String startDate,
                               @Param("endDate") String endDate,
                               @Param("offset") int offset,
                               @Param("limit") int limit);

    /**
     * Find recent orders
     */
    List<LaundryOrder> findRecent(@Param("limit") int limit);

    /**
     * Count orders with filters
     */
    long count(@Param("keyword") String keyword,
               @Param("status") Integer status,
               @Param("customerId") Long customerId,
               @Param("startDate") String startDate,
               @Param("endDate") String endDate);

    /**
     * Count orders by status
     */
    long countByStatus(@Param("status") Integer status);

    /**
     * Insert new order
     */
    int insert(LaundryOrder order);

    /**
     * Update order
     */
    int update(LaundryOrder order);

    /**
     * Update order status
     */
    int updateStatus(@Param("id") Long id, 
                     @Param("status") Integer status,
                     @Param("completeTime") LocalDateTime completeTime,
                     @Param("deliverTime") LocalDateTime deliverTime);

    /**
     * Update total amount
     */
    int updateTotalAmount(@Param("id") Long id, @Param("totalAmount") BigDecimal totalAmount);

    /**
     * Delete order by ID
     */
    int deleteById(@Param("id") Long id);

    /**
     * Get revenue for period
     */
    BigDecimal getRevenue(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * Get next order number sequence
     */
    String getNextOrderNo(@Param("prefix") String prefix);
}
