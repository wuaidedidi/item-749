package com.laundry.service;

import com.laundry.dto.DashboardStats;
import com.laundry.dto.OrderCreateRequest;
import com.laundry.entity.LaundryOrder;
import com.laundry.entity.OrderDetail;
import com.laundry.util.PageResult;

import java.util.List;

/**
 * Order Service Interface
 */
public interface OrderService {

    /**
     * Get order by ID
     */
    LaundryOrder getById(Long id);

    /**
     * Get order by ID with details
     */
    LaundryOrder getByIdWithDetails(Long id);

    /**
     * Get order by order number
     */
    LaundryOrder getByOrderNo(String orderNo);

    /**
     * Get paginated order list
     */
    PageResult<LaundryOrder> getList(String keyword, Integer status, Long customerId, 
                                      String startDate, String endDate, int page, int pageSize);

    /**
     * Get recent orders
     */
    List<LaundryOrder> getRecentOrders(int limit);

    /**
     * Create new order
     */
    LaundryOrder create(OrderCreateRequest request, Long userId);

    /**
     * Update order
     */
    LaundryOrder update(LaundryOrder order);

    /**
     * Update order status
     */
    void updateStatus(Long orderId, Integer status);

    /**
     * Delete order
     */
    void delete(Long orderId);

    /**
     * Get order details
     */
    List<OrderDetail> getOrderDetails(Long orderId);

    /**
     * Get dashboard statistics
     */
    DashboardStats getDashboardStats();
}
