package com.laundry.controller;

import com.laundry.dto.DashboardStats;
import com.laundry.entity.LaundryOrder;
import com.laundry.service.OrderService;
import com.laundry.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Dashboard Controller
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private OrderService orderService;

    /**
     * Get dashboard statistics
     */
    @GetMapping("/stats")
    public Result<DashboardStats> getStats() {
        DashboardStats stats = orderService.getDashboardStats();
        return Result.success(stats);
    }

    /**
     * Get recent orders
     */
    @GetMapping("/recent-orders")
    public Result<List<LaundryOrder>> getRecentOrders(
            @RequestParam(defaultValue = "5") int limit) {
        List<LaundryOrder> orders = orderService.getRecentOrders(limit);
        return Result.success(orders);
    }
}
