package com.laundry.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laundry.util.JwtUtil;
import com.laundry.util.Result;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Authentication Interceptor
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Allow OPTIONS requests for CORS
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String token = extractToken(request);
        
        if (token == null || token.isEmpty()) {
            sendUnauthorizedResponse(response, "No token provided");
            return false;
        }

        try {
            if (!jwtUtil.validateToken(token)) {
                sendUnauthorizedResponse(response, "Invalid token");
                return false;
            }

            // Set user info in request attributes
            Long userId = jwtUtil.getUserId(token);
            String username = jwtUtil.getUsername(token);
            Integer role = jwtUtil.getRole(token);

            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
            request.setAttribute("role", role);

            logger.debug("Authenticated user: {} (ID: {}, Role: {})", username, userId, role);
            return true;

        } catch (JwtException e) {
            logger.warn("JWT validation failed: {}", e.getMessage());
            sendUnauthorizedResponse(response, "Token expired or invalid");
            return false;
        }
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        // Also check query parameter for some cases
        return request.getParameter("token");
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        
        Result<?> result = Result.error(401, message);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(result));
    }
}
