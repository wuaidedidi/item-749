/**
 * API Request Module
 * Handles all HTTP requests to the backend
 */

const API_BASE_URL = '/api';

// Create API instance
const api = {
    token: localStorage.getItem('token'),

    /**
     * Set authorization token
     */
    setToken(token) {
        this.token = token;
        if (token) {
            localStorage.setItem('token', token);
        } else {
            localStorage.removeItem('token');
        }
    },

    /**
     * Get authorization headers
     */
    getHeaders() {
        const headers = {
            'Content-Type': 'application/json'
        };
        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }
        return headers;
    },

    /**
     * Handle API response
     */
    async handleResponse(response) {
        const data = await response.json();
        
        if (response.status === 401) {
            // Unauthorized - redirect to login
            this.setToken(null);
            localStorage.removeItem('user');
            window.location.href = '/pages/login.html';
            throw new Error('Session expired. Please login again.');
        }
        
        if (!response.ok || data.code !== 200) {
            throw new Error(data.message || 'Request failed');
        }
        
        return data;
    },

    /**
     * GET request
     */
    async get(endpoint, params = {}) {
        const url = new URL(API_BASE_URL + endpoint, window.location.origin);
        Object.keys(params).forEach(key => {
            if (params[key] !== undefined && params[key] !== null && params[key] !== '') {
                url.searchParams.append(key, params[key]);
            }
        });

        const response = await fetch(url.toString(), {
            method: 'GET',
            headers: this.getHeaders()
        });

        return this.handleResponse(response);
    },

    /**
     * POST request
     */
    async post(endpoint, data = {}) {
        const response = await fetch(API_BASE_URL + endpoint, {
            method: 'POST',
            headers: this.getHeaders(),
            body: JSON.stringify(data)
        });

        return this.handleResponse(response);
    },

    /**
     * PUT request
     */
    async put(endpoint, data = {}) {
        const response = await fetch(API_BASE_URL + endpoint, {
            method: 'PUT',
            headers: this.getHeaders(),
            body: JSON.stringify(data)
        });

        return this.handleResponse(response);
    },

    /**
     * DELETE request
     */
    async delete(endpoint) {
        const response = await fetch(API_BASE_URL + endpoint, {
            method: 'DELETE',
            headers: this.getHeaders()
        });

        return this.handleResponse(response);
    }
};

// Auth API
const authApi = {
    login(username, password) {
        return api.post('/auth/login', { username, password });
    },

    register(data) {
        return api.post('/auth/register', data);
    }
};

// User API
const userApi = {
    getProfile() {
        return api.get('/users/profile');
    },

    updateProfile(data) {
        return api.put('/users/profile', data);
    },

    changePassword(oldPassword, newPassword) {
        return api.put('/users/password', { oldPassword, newPassword });
    },

    getList(params) {
        return api.get('/users', params);
    },

    getById(id) {
        return api.get(`/users/${id}`);
    },

    create(data) {
        return api.post('/users', data);
    },

    update(id, data) {
        return api.put(`/users/${id}`, data);
    },

    updateStatus(id, status) {
        return api.put(`/users/${id}/status`, { status });
    },

    resetPassword(id, password) {
        return api.put(`/users/${id}/reset-password`, { password });
    },

    delete(id) {
        return api.delete(`/users/${id}`);
    }
};

// Customer API
const customerApi = {
    getList(params) {
        return api.get('/customers', params);
    },

    getAll() {
        return api.get('/customers/all');
    },

    getById(id) {
        return api.get(`/customers/${id}`);
    },

    create(data) {
        return api.post('/customers', data);
    },

    update(id, data) {
        return api.put(`/customers/${id}`, data);
    },

    delete(id) {
        return api.delete(`/customers/${id}`);
    }
};

// Service API
const serviceApi = {
    getList(params) {
        return api.get('/services', params);
    },

    getActive() {
        return api.get('/services/active');
    },

    getCategories() {
        return api.get('/services/categories');
    },

    getById(id) {
        return api.get(`/services/${id}`);
    },

    create(data) {
        return api.post('/services', data);
    },

    update(id, data) {
        return api.put(`/services/${id}`, data);
    },

    updateStatus(id, status) {
        return api.put(`/services/${id}/status`, { status });
    },

    delete(id) {
        return api.delete(`/services/${id}`);
    }
};

// Order API
const orderApi = {
    getList(params) {
        return api.get('/orders', params);
    },

    getById(id) {
        return api.get(`/orders/${id}`);
    },

    getDetails(id) {
        return api.get(`/orders/${id}/details`);
    },

    create(data) {
        return api.post('/orders', data);
    },

    update(id, data) {
        return api.put(`/orders/${id}`, data);
    },

    updateStatus(id, status) {
        return api.put(`/orders/${id}/status`, { status });
    },

    delete(id) {
        return api.delete(`/orders/${id}`);
    }
};

// Dashboard API
const dashboardApi = {
    getStats() {
        return api.get('/dashboard/stats');
    },

    getRecentOrders(limit = 5) {
        return api.get('/dashboard/recent-orders', { limit });
    }
};

// Export
window.api = api;
window.authApi = authApi;
window.userApi = userApi;
window.customerApi = customerApi;
window.serviceApi = serviceApi;
window.orderApi = orderApi;
window.dashboardApi = dashboardApi;
