package com.laundry.config;

import com.laundry.entity.User;
import com.laundry.mapper.UserMapper;
import com.laundry.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Initialize critical data on startup with retry mechanism for database readiness
 */
@Component
public class DataInitializer implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private static final int MAX_RETRIES = 10;
    private static final int RETRY_DELAY_MS = 3000;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void afterPropertiesSet() throws Exception {
        // Retry loop to wait for database to be ready
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                initializeData();
                return; // Success, exit
            } catch (Exception e) {
                if (attempt == MAX_RETRIES) {
                    logger.error("Database initialization failed after {} attempts", MAX_RETRIES);
                    throw e; // Fail on last attempt
                }
                logger.warn("Database not ready (attempt {}/{}), retrying in {}ms...", 
                           attempt, MAX_RETRIES, RETRY_DELAY_MS);
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
    }

    private void initializeData() {
        // Fix admin password if needed
        User admin = userMapper.findByUsername("admin");
        if (admin != null) {
            boolean isValid = PasswordUtil.verifyPassword("123456", admin.getPassword());
            if (!isValid) {
                String newHash = PasswordUtil.hashPassword("123456");
                userMapper.updatePassword(admin.getId(), newHash);
                logger.info("Admin password reset to default '123456'");
            }
        }
        logger.info("Data initialization completed successfully");
    }
}
