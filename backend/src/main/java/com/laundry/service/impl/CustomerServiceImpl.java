package com.laundry.service.impl;

import com.laundry.entity.Customer;
import com.laundry.exception.BusinessException;
import com.laundry.mapper.CustomerMapper;
import com.laundry.mapper.LaundryOrderMapper;
import com.laundry.service.CustomerService;
import com.laundry.util.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 客户服务实现类
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private LaundryOrderMapper laundryOrderMapper;

    @Override
    public Customer getById(Long id) {
        return customerMapper.findById(id);
    }

    @Override
    public Customer getByPhone(String phone) {
        return customerMapper.findByPhone(phone);
    }

    @Override
    public PageResult<Customer> getList(String keyword, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Customer> list = customerMapper.findAll(keyword, offset, pageSize);
        long total = customerMapper.count(keyword);
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    public List<Customer> getAllList() {
        return customerMapper.findAllList();
    }

    @Override
    @Transactional
    public Customer create(Customer customer) {
        logger.info("创建客户: {}", customer.getName());
        
        if (customer.getPhone() != null && !customer.getPhone().isEmpty() && customerMapper.existsByPhone(customer.getPhone())) {
            throw new BusinessException("该手机号已被其他客户使用");
        }

        customerMapper.insert(customer);
        logger.info("客户创建成功: {}", customer.getId());
        
        return customer;
    }

    @Override
    @Transactional
    public Customer update(Customer customer) {
        logger.info("更新客户: {}", customer.getId());
        
        Customer existing = customerMapper.findById(customer.getId());
        if (existing == null) {
            throw new BusinessException("客户不存在");
        }

        if (customer.getPhone() != null && !customer.getPhone().isEmpty() && customerMapper.existsByPhoneAndNotId(customer.getPhone(), customer.getId())) {
            throw new BusinessException("该手机号已被其他客户使用");
        }

        customerMapper.update(customer);
        return customerMapper.findById(customer.getId());
    }

    @Override
    @Transactional
    public void delete(Long customerId) {
        logger.info("删除客户: {}", customerId);
        
        Customer customer = customerMapper.findById(customerId);
        if (customer == null) {
            throw new BusinessException("客户不存在");
        }

        // 检查是否有关联订单
        long orderCount = laundryOrderMapper.count(null, null, customerId, null, null);
        if (orderCount > 0) {
            throw new BusinessException("该客户有 " + orderCount + " 个关联订单，无法删除");
        }

        customerMapper.deleteById(customerId);
        logger.info("客户删除成功: {}", customerId);
    }

    @Override
    public long count() {
        return customerMapper.count(null);
    }
}
