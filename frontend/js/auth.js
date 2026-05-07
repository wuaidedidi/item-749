/**
 * Authentication Module
 * Handles user authentication state
 */

const auth = {
    /**
     * Check if user is logged in
     */
    isLoggedIn() {
        return !!localStorage.getItem('token');
    },

    /**
     * Get current user
     */
    getUser() {
        const userStr = localStorage.getItem('user');
        return userStr ? JSON.parse(userStr) : null;
    },

    /**
     * Set user data
     */
    setUser(user) {
        localStorage.setItem('user', JSON.stringify(user));
    },

    /**
     * Update user data (alias for setUser)
     */
    updateUser(user) {
        this.setUser(user);
    },

    /**
     * Login user
     */
    async login(username, password) {
        const response = await authApi.login(username, password);
        if (response.code === 200) {
            api.setToken(response.data.token);
            this.setUser(response.data.user);
            return response.data;
        }
        throw new Error(response.message);
    },

    /**
     * Register user
     */
    async register(data) {
        const response = await authApi.register(data);
        if (response.code === 200) {
            return response.data;
        }
        throw new Error(response.message);
    },

    /**
     * Logout user
     */
    logout() {
        api.setToken(null);
        localStorage.removeItem('user');
        window.location.href = '/pages/login.html';
    },

    /**
     * Check if user is admin
     */
    isAdmin() {
        const user = this.getUser();
        return user && user.role === 1;
    },

    /**
     * Require authentication
     */
    requireAuth() {
        if (!this.isLoggedIn()) {
            window.location.href = '/pages/login.html';
            return false;
        }
        return true;
    },

    /**
     * Require admin role
     */
    requireAdmin() {
        if (!this.isAdmin()) {
            Toast.warning('Access denied', 'Admin privileges required');
            return false;
        }
        return true;
    },

    /**
     * Refresh user data from server
     */
    async refreshUser() {
        try {
            const response = await userApi.getProfile();
            if (response.code === 200) {
                this.setUser(response.data);
                return response.data;
            }
        } catch (error) {
            console.error('Failed to refresh user:', error);
        }
        return null;
    }
};

// Export
window.auth = auth;
