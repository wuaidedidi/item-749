package com.laundry.service.impl;

import com.laundry.entity.ServiceItem;
import com.laundry.exception.BusinessException;
import com.laundry.mapper.OrderDetailMapper;
import com.laundry.mapper.ServiceItemMapper;
import com.laundry.service.ServiceItemService;
import com.laundry.util.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 服务项目服务实现类
 */
@Service
public class ServiceItemServiceImpl implements ServiceItemService {
    private static final Logger logger = LoggerFactory.getLogger(ServiceItemServiceImpl.class);

    @Autowired
    private ServiceItemMapper serviceItemMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public ServiceItem getById(Long id) {
        return serviceItemMapper.findById(id);
    }

    @Override
    public PageResult<ServiceItem> getList(String keyword, String category, Integer status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<ServiceItem> list = serviceItemMapper.findAll(keyword, category, status, offset, pageSize);
        long total = serviceItemMapper.count(keyword, category, status);
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    public List<ServiceItem> getAllActive() {
        return serviceItemMapper.findAllActive();
    }

    @Override
    public List<String> getAllCategories() {
        return serviceItemMapper.findAllCategories();
    }

    @Override
    @Transactional
    public ServiceItem create(ServiceItem service) {
        logger.info("创建服务: {}", service.getName());
        
        if (service.getStatus() == null) {
            service.setStatus(1);
        }

        serviceItemMapper.insert(service);
        logger.info("服务创建成功: {}", service.getId());
        
        return service;
    }

    @Override
    @Transactional
    public ServiceItem update(ServiceItem service) {
        logger.info("更新服务: {}", service.getId());
        
        ServiceItem existing = serviceItemMapper.findById(service.getId());
        if (existing == null) {
            throw new BusinessException("服务项目不存在");
        }

        serviceItemMapper.update(service);
        return serviceItemMapper.findById(service.getId());
    }

    @Override
    @Transactional
    public void updateStatus(Long serviceId, Integer status) {
        logger.info("更新服务状态 {}: {}", serviceId, status);
        
        ServiceItem service = serviceItemMapper.findById(serviceId);
        if (service == null) {
            throw new BusinessException("服务项目不存在");
        }

        serviceItemMapper.updateStatus(serviceId, status);
    }

    @Override
    @Transactional
    public void delete(Long serviceId) {
        logger.info("删除服务: {}", serviceId);
        
        ServiceItem service = serviceItemMapper.findById(serviceId);
        if (service == null) {
            throw new BusinessException("服务项目不存在");
        }

        // 检查是否有订单明细使用此服务
        long usageCount = orderDetailMapper.countByServiceId(serviceId);
        if (usageCount > 0) {
            throw new BusinessException("该服务项目已被 " + usageCount + " 个订单使用，无法删除");
        }

        serviceItemMapper.deleteById(serviceId);
        logger.info("服务删除成功: {}", serviceId);
    }

    @Override
    public long countActive() {
        return serviceItemMapper.count(null, null, 1);
    }
}
