/**
 * Utility Functions
 */

const utils = {
    /**
     * Format date
     */
    formatDate(dateStr, format = 'YYYY-MM-DD HH:mm') {
        if (!dateStr) return '-';
        const date = new Date(dateStr);
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        const seconds = String(date.getSeconds()).padStart(2, '0');

        return format
            .replace('YYYY', year)
            .replace('MM', month)
            .replace('DD', day)
            .replace('HH', hours)
            .replace('mm', minutes)
            .replace('ss', seconds);
    },

    /**
     * Format currency
     */
    formatCurrency(amount) {
        if (amount === null || amount === undefined) return '¥0.00';
        return '¥' + parseFloat(amount).toFixed(2);
    },

    /**
     * Get order status text
     */
    getOrderStatusText(status) {
        const statusMap = {
            0: '待处理',
            1: '处理中',
            2: '待取件',
            3: '已完成',
            4: '已取消'
        };
        return statusMap[status] || '未知';
    },

    /**
     * Get order status class
     */
    getOrderStatusClass(status) {
        const classMap = {
            0: 'status-pending',
            1: 'status-processing',
            2: 'status-ready',
            3: 'status-delivered',
            4: 'status-cancelled'
        };
        return classMap[status] || '';
    },

    /**
     * Get category display name
     */
    getCategoryName(category) {
        const categoryMap = {
            'wash': '水洗',
            'dry_clean': '干洗',
            'iron': '熨烫',
            'repair': '修补'
        };
        return categoryMap[category] || category;
    },

    /**
     * Debounce function
     */
    debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    },

    /**
     * Escape HTML
     */
    escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    },

    /**
     * Generate random ID
     */
    generateId() {
        return Math.random().toString(36).substring(2, 9);
    },

    /**
     * Validate email
     */
    isValidEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    },

    /**
     * Validate phone (Chinese format)
     */
    isValidPhone(phone) {
        const re = /^1[3-9]\d{9}$/;
        return re.test(phone);
    },

    /**
     * Get initials from name
     */
    getInitials(name) {
        if (!name) return '?';
        const parts = name.split(' ');
        if (parts.length >= 2) {
            return (parts[0][0] + parts[1][0]).toUpperCase();
        }
        return name.substring(0, 2).toUpperCase();
    },

    /**
     * Parse query string
     */
    parseQueryString() {
        const params = {};
        const search = window.location.search.substring(1);
        if (search) {
            search.split('&').forEach(pair => {
                const [key, value] = pair.split('=');
                params[decodeURIComponent(key)] = decodeURIComponent(value || '');
            });
        }
        return params;
    },

    /**
     * Build query string
     */
    buildQueryString(params) {
        return Object.keys(params)
            .filter(key => params[key] !== undefined && params[key] !== null && params[key] !== '')
            .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(params[key])}`)
            .join('&');
    }
};

// Export
window.utils = utils;
