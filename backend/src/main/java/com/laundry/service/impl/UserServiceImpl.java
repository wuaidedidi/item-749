package com.laundry.service.impl;

import com.laundry.dto.LoginRequest;
import com.laundry.dto.LoginResponse;
import com.laundry.dto.PasswordChangeRequest;
import com.laundry.dto.RegisterRequest;
import com.laundry.entity.User;
import com.laundry.exception.BusinessException;
import com.laundry.mapper.UserMapper;
import com.laundry.service.UserService;
import com.laundry.util.JwtUtil;
import com.laundry.util.PageResult;
import com.laundry.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        logger.info("用户登录尝试: {}", request.getUsername());
        
        User user = userMapper.findByUsername(request.getUsername());
        if (user == null) {
            logger.warn("登录失败: 用户不存在 - {}", request.getUsername());
            throw new BusinessException("用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            logger.warn("登录失败: 账户已禁用 - {}", request.getUsername());
            throw new BusinessException("账户已被禁用");
        }

        if (!PasswordUtil.verifyPassword(request.getPassword(), user.getPassword())) {
            logger.warn("登录失败: 密码错误 - {}", request.getUsername());
            throw new BusinessException("用户名或密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        logger.info("用户登录成功: {}", request.getUsername());
        
        return new LoginResponse(token, user);
    }

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        logger.info("用户注册: {}", request.getUsername());
        
        if (userMapper.existsByUsername(request.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.hashPassword(request.getPassword()));
        user.setRealName(request.getRealName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRole(0); // 默认为员工
        user.setStatus(1); // 默认启用

        userMapper.insert(user);
        logger.info("用户注册成功: {}", request.getUsername());
        
        user.setPassword(null); // 不返回密码
        return user;
    }

    @Override
    public User getById(Long id) {
        User user = userMapper.findById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public User getByUsername(String username) {
        User user = userMapper.findByUsername(username);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    @Override
    public PageResult<User> getList(String keyword, Integer role, Integer status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<User> list = userMapper.findAll(keyword, role, status, offset, pageSize);
        long total = userMapper.count(keyword, role, status);
        
        // 移除密码
        list.forEach(u -> u.setPassword(null));
        
        return PageResult.of(list, total, page, pageSize);
    }

    @Override
    @Transactional
    public User create(User user) {
        logger.info("创建用户: {}", user.getUsername());
        
        if (userMapper.existsByUsername(user.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole(0);
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        userMapper.insert(user);
        logger.info("用户创建成功: {}", user.getUsername());
        
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional
    public User update(User user) {
        logger.info("更新用户: {}", user.getId());
        
        User existing = userMapper.findById(user.getId());
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }

        // 不通过此方法更新密码
        user.setPassword(null);
        userMapper.update(user);
        
        return getById(user.getId());
    }

    @Override
    @Transactional
    public User updateProfile(Long userId, User user) {
        logger.info("更新用户资料: {}", userId);
        
        User existing = userMapper.findById(userId);
        if (existing == null) {
            throw new BusinessException("用户不存在");
        }

        // 只允许更新部分字段
        existing.setRealName(user.getRealName());
        existing.setPhone(user.getPhone());
        existing.setEmail(user.getEmail());
        
        userMapper.update(existing);
        
        return getById(userId);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, PasswordChangeRequest request) {
        logger.info("修改密码: {}", userId);
        
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!PasswordUtil.verifyPassword(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException("当前密码错误");
        }

        String newHash = PasswordUtil.hashPassword(request.getNewPassword());
        userMapper.updatePassword(userId, newHash);
        
        logger.info("密码修改成功: {}", userId);
    }

    @Override
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        logger.info("重置密码: {}", userId);
        
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        userMapper.updatePassword(userId, newHash);
        
        logger.info("密码重置成功: {}", userId);
    }

    @Override
    @Transactional
    public void updateStatus(Long userId, Integer status) {
        logger.info("更新用户状态 {}: {}", userId, status);
        
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        userMapper.updateStatus(userId, status);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        logger.info("删除用户: {}", userId);
        
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 禁止删除管理员
        if (user.getRole() == 1 && "admin".equals(user.getUsername())) {
            throw new BusinessException("不能删除系统管理员");
        }

        userMapper.deleteById(userId);
        logger.info("用户删除成功: {}", userId);
    }
}
