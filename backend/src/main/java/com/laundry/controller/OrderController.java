package com.laundry.controller;

import com.laundry.dto.OrderCreateRequest;
import com.laundry.entity.LaundryOrder;
import com.laundry.entity.OrderDetail;
import com.laundry.service.OrderService;
import com.laundry.util.PageResult;
import com.laundry.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Order Management Controller
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    /**
     * Get order list
     */
    @GetMapping
    public Result<PageResult<LaundryOrder>> getList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<LaundryOrder> result = orderService.getList(keyword, status, customerId, 
                                                                startDate, endDate, page, pageSize);
        return Result.success(result);
    }

    /**
     * Get order by ID
     */
    @GetMapping("/{id}")
    public Result<LaundryOrder> getById(@PathVariable Long id) {
        LaundryOrder order = orderService.getByIdWithDetails(id);
        if (order == null) {
            return Result.notFound("Order not found");
        }
        return Result.success(order);
    }

    /**
     * Get order details
     */
    @GetMapping("/{id}/details")
    public Result<List<OrderDetail>> getDetails(@PathVariable Long id) {
        List<OrderDetail> details = orderService.getOrderDetails(id);
        return Result.success(details);
    }

    /**
     * Create new order
     */
    @PostMapping
    public Result<LaundryOrder> create(HttpServletRequest request,
                                       @Valid @RequestBody OrderCreateRequest orderRequest) {
        Long userId = (Long) request.getAttribute("userId");
        LaundryOrder created = orderService.create(orderRequest, userId);
        return Result.success("Order created successfully", created);
    }

    /**
     * Update order
     */
    @PutMapping("/{id}")
    public Result<LaundryOrder> update(@PathVariable Long id, @RequestBody LaundryOrder order) {
        order.setId(id);
        LaundryOrder updated = orderService.update(order);
        return Result.success("Order updated successfully", updated);
    }

    /**
     * Update order status
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        orderService.updateStatus(id, status);
        return Result.success("Order status updated successfully", null);
    }

    /**
     * Delete order
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        orderService.delete(id);
        return Result.success("Order deleted successfully", null);
    }
}
