package com.laundry.service.impl;

import com.laundry.dto.DashboardStats;
import com.laundry.dto.OrderCreateRequest;
import com.laundry.entity.LaundryOrder;
import com.laundry.entity.OrderDetail;
import com.laundry.entity.ServiceItem;
import com.laundry.exception.BusinessException;
import com.laundry.mapper.CustomerMapper;
import com.laundry.mapper.LaundryOrderMapper;
import com.laundry.mapper.OrderDetailMapper;
import com.laundry.mapper.ServiceItemMapper;
import com.laundry.service.OrderService;
import com.laundry.util.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单服务实现类
 */
@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private LaundryOrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper detailMapper;

    @Autowired
    private ServiceItemMapper serviceItemMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public LaundryOrder getById(Long id) {
        return orderMapper.findById(id);
    }

    @Override
    public LaundryOrder getByIdWithDetails(Long id) {
        LaundryOrder order = orderMapper.findByIdWithDetails(id);
        if (order != null && (order.getOrderDetails() == null || order.getOrderDetails().isEmpty())) {
            order.setOrderDetails(detailMapper.findByOrderId(id));
        }
        return order;
    }

    @Override
    public LaundryOrder getByOrderNo(String orderNo) {
        return orderMapper.findByOrderNo(orderNo);
    }

    @Override
    public PageResult<LaundryOrder> getList(String keyword, Integer status, Long customerId,
                                            String startDate, String endDate, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<LaundryOrder> list = orderMapper.findAll(keyword, status, customerId, startDate, endDate, offset, pageSize);
        long total = orderMapper.count(keyword, status, customerId, startDate, endDate);
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    public List<LaundryOrder> getRecentOrders(int limit) {
        return orderMapper.findRecent(limit);
    }

    @Override
    @Transactional
    public LaundryOrder create(OrderCreateRequest request, Long userId) {
        logger.info("创建订单，客户ID: {}", request.getCustomerId());

        // 验证客户
        if (customerMapper.findById(request.getCustomerId()) == null) {
            throw new BusinessException("客户不存在");
        }

        // 生成订单号
        String datePrefix = "ORD" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String orderNo = orderMapper.getNextOrderNo(datePrefix);
        if (orderNo == null || orderNo.isEmpty()) {
            orderNo = datePrefix + "001";
        }

        // 创建订单
        LaundryOrder order = new LaundryOrder();
        order.setOrderNo(orderNo);
        order.setCustomerId(request.getCustomerId());
        order.setUserId(userId);
        order.setStatus(0); // 待处理
        order.setReceiveTime(LocalDateTime.now());
        order.setRemark(request.getRemark());

        if (request.getExpectTime() != null && !request.getExpectTime().isEmpty()) {
            order.setExpectTime(LocalDateTime.parse(request.getExpectTime(), 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } else {
            // 默认: 3天后
            order.setExpectTime(LocalDateTime.now().plusDays(3));
        }

        order.setTotalAmount(BigDecimal.ZERO);
        orderMapper.insert(order);

        // 创建订单明细
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderDetail> details = new ArrayList<>();
        
        for (OrderCreateRequest.OrderItemRequest item : request.getItems()) {
            ServiceItem service = serviceItemMapper.findById(item.getServiceId());
            if (service == null) {
                throw new BusinessException("服务项目不存在: " + item.getServiceId());
            }

            OrderDetail detail = new OrderDetail();
            detail.setOrderId(order.getId());
            detail.setServiceId(service.getId());
            detail.setServiceName(service.getName());
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(service.getPrice());
            detail.setSubtotal(service.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            detail.setRemark(item.getRemark());

            details.add(detail);
            totalAmount = totalAmount.add(detail.getSubtotal());
        }

        if (!details.isEmpty()) {
            detailMapper.batchInsert(details);
        }

        // 更新订单总金额
        order.setTotalAmount(totalAmount);
        orderMapper.updateTotalAmount(order.getId(), totalAmount);

        logger.info("订单创建成功: {}", orderNo);
        return getByIdWithDetails(order.getId());
    }

    @Override
    @Transactional
    public LaundryOrder update(LaundryOrder order) {
        logger.info("更新订单: {}", order.getId());

        LaundryOrder existing = orderMapper.findById(order.getId());
        if (existing == null) {
            throw new BusinessException("订单不存在");
        }

        orderMapper.update(order);
        return getById(order.getId());
    }

    @Override
    @Transactional
    public void updateStatus(Long orderId, Integer status) {
        logger.info("更新订单状态 {}: {}", orderId, status);

        LaundryOrder order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        LocalDateTime completeTime = null;
        LocalDateTime deliverTime = null;

        // 根据状态设置时间戳
        if (status == 2) { // 待取件
            completeTime = LocalDateTime.now();
        } else if (status == 3) { // 已完成
            deliverTime = LocalDateTime.now();
            if (order.getCompleteTime() == null) {
                completeTime = LocalDateTime.now();
            }
        }

        orderMapper.updateStatus(orderId, status, completeTime, deliverTime);
        logger.info("订单状态更新成功: {} -> {}", orderId, status);
    }

    @Override
    @Transactional
    public void delete(Long orderId) {
        logger.info("删除订单: {}", orderId);

        LaundryOrder order = orderMapper.findById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        // 只允许删除待处理或已取消的订单
        if (order.getStatus() != 0 && order.getStatus() != 4) {
            throw new BusinessException("当前状态的订单不能删除");
        }

        // 订单明细会通过级联删除
        orderMapper.deleteById(orderId);
        logger.info("订单删除成功: {}", orderId);
    }

    @Override
    public List<OrderDetail> getOrderDetails(Long orderId) {
        return detailMapper.findByOrderId(orderId);
    }

    @Override
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        // 订单统计
        stats.setTotalOrders(orderMapper.count(null, null, null, null, null));
        stats.setPendingOrders(orderMapper.countByStatus(0));
        stats.setProcessingOrders(orderMapper.countByStatus(1));
        stats.setCompletedOrders(orderMapper.countByStatus(3));

        // 客户数量
        stats.setTotalCustomers(customerMapper.count(null));

        // 服务数量
        stats.setTotalServices(serviceItemMapper.count(null, null, 1));

        // 收入统计
        LocalDate today = LocalDate.now();
        String todayStart = today.atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String todayEnd = today.plusDays(1).atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        stats.setTodayRevenue(orderMapper.getRevenue(todayStart, todayEnd));

        String monthStart = today.withDayOfMonth(1).atStartOfDay().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        stats.setMonthRevenue(orderMapper.getRevenue(monthStart, todayEnd));

        if (stats.getTodayRevenue() == null) {
            stats.setTodayRevenue(BigDecimal.ZERO);
        }
        if (stats.getMonthRevenue() == null) {
            stats.setMonthRevenue(BigDecimal.ZERO);
        }

        return stats;
    }
}
