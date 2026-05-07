/**
 * UI Components Module
 */

// ============================================
// Toast Notifications
// ============================================

const Toast = {
    container: null,

    init() {
        if (!this.container) {
            this.container = document.createElement('div');
            this.container.className = 'toast-container';
            document.body.appendChild(this.container);
        }
    },

    show(type, title, message, duration = 4000) {
        this.init();

        const icons = {
            success: '✓',
            error: '✕',
            warning: '⚠',
            info: 'ℹ'
        };

        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.innerHTML = `
            <span class="toast-icon">${icons[type]}</span>
            <div class="toast-content">
                <div class="toast-title">${utils.escapeHtml(title)}</div>
                <div class="toast-message">${utils.escapeHtml(message)}</div>
            </div>
            <button class="toast-close">×</button>
        `;

        this.container.appendChild(toast);

        // Close button
        toast.querySelector('.toast-close').addEventListener('click', () => {
            this.hide(toast);
        });

        // Auto close
        if (duration > 0) {
            setTimeout(() => this.hide(toast), duration);
        }

        return toast;
    },

    hide(toast) {
        toast.classList.add('hiding');
        setTimeout(() => toast.remove(), 300);
    },

    success(title, message) {
        return this.show('success', title, message);
    },

    error(title, message) {
        return this.show('error', title, message);
    },

    warning(title, message) {
        return this.show('warning', title, message);
    },

    info(title, message) {
        return this.show('info', title, message);
    }
};

// ============================================
// Modal Component
// ============================================

const Modal = {
    activeModals: [],

    create(options = {}) {
        const {
            title = 'Modal',
            content = '',
            size = '', // '', 'lg', 'xl'
            showClose = true,
            onClose = null
        } = options;

        const overlay = document.createElement('div');
        overlay.className = 'modal-overlay';
        overlay.innerHTML = `
            <div class="modal ${size ? 'modal-' + size : ''}">
                <div class="modal-header">
                    <h3 class="modal-title">${utils.escapeHtml(title)}</h3>
                    ${showClose ? '<button class="modal-close">×</button>' : ''}
                </div>
                <div class="modal-body">${content}</div>
            </div>
        `;

        document.body.appendChild(overlay);

        // Show with animation
        requestAnimationFrame(() => {
            overlay.classList.add('active');
        });

        // Close handlers
        const closeBtn = overlay.querySelector('.modal-close');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => this.close(overlay, onClose));
        }

        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) {
                this.close(overlay, onClose);
            }
        });

        // ESC key
        const escHandler = (e) => {
            if (e.key === 'Escape' && this.activeModals[this.activeModals.length - 1] === overlay) {
                this.close(overlay, onClose);
                document.removeEventListener('keydown', escHandler);
            }
        };
        document.addEventListener('keydown', escHandler);

        this.activeModals.push(overlay);

        return {
            element: overlay,
            modal: overlay.querySelector('.modal'),
            body: overlay.querySelector('.modal-body'),
            close: () => this.close(overlay, onClose)
        };
    },

    close(overlay, callback) {
        overlay.classList.remove('active');
        setTimeout(() => {
            overlay.remove();
            const index = this.activeModals.indexOf(overlay);
            if (index > -1) {
                this.activeModals.splice(index, 1);
            }
            if (callback) callback();
        }, 300);
    },

    confirm(options = {}) {
        return new Promise((resolve) => {
            const {
                title = '确认',
                message = '确定要执行此操作吗？',
                confirmText = '确定',
                cancelText = '取消',
                confirmClass = 'btn-primary',
                danger = false
            } = options;

            const content = `
                <p style="margin-bottom: 24px;">${utils.escapeHtml(message)}</p>
                <div class="modal-footer" style="margin: -24px; margin-top: 0; padding: 16px 24px;">
                    <button class="btn btn-secondary" id="modal-cancel">${cancelText}</button>
                    <button class="btn ${danger ? 'btn-danger' : confirmClass}" id="modal-confirm">${confirmText}</button>
                </div>
            `;

            const modal = this.create({ title, content, size: '' });

            modal.body.querySelector('#modal-cancel').addEventListener('click', () => {
                modal.close();
                resolve(false);
            });

            modal.body.querySelector('#modal-confirm').addEventListener('click', () => {
                modal.close();
                resolve(true);
            });
        });
    },

    alert(title, message) {
        return new Promise((resolve) => {
            const content = `
                <p style="margin-bottom: 24px;">${utils.escapeHtml(message)}</p>
                <div class="modal-footer" style="margin: -24px; margin-top: 0; padding: 16px 24px;">
                    <button class="btn btn-primary" id="modal-ok">确定</button>
                </div>
            `;

            const modal = this.create({ title, content, size: '' });

            modal.body.querySelector('#modal-ok').addEventListener('click', () => {
                modal.close();
                resolve();
            });
        });
    }
};

// ============================================
// Loading Component
// ============================================

const Loading = {
    overlay: null,

    show(message = 'Loading...') {
        if (this.overlay) return;

        this.overlay = document.createElement('div');
        this.overlay.className = 'modal-overlay active';
        this.overlay.style.background = 'rgba(0, 0, 0, 0.8)';
        this.overlay.innerHTML = `
            <div style="text-align: center;">
                <div class="spinner" style="margin: 0 auto 16px;"></div>
                <div style="color: #f1f5f9;">${utils.escapeHtml(message)}</div>
            </div>
        `;

        document.body.appendChild(this.overlay);
    },

    hide() {
        if (this.overlay) {
            this.overlay.remove();
            this.overlay = null;
        }
    }
};

// ============================================
// Pagination Component
// ============================================

const Pagination = {
    create(options = {}) {
        const {
            container,
            page = 1,
            pageSize = 10,
            total = 0,
            onChange = () => {}
        } = options;

        const totalPages = Math.ceil(total / pageSize);

        if (totalPages <= 1) {
            container.innerHTML = '';
            return;
        }

        let html = '<div class="pagination">';

        // Previous button
        html += `<button class="pagination-btn" data-page="${page - 1}" ${page === 1 ? 'disabled' : ''}>« 上一页</button>`;

        // Page numbers
        const showPages = [];
        for (let i = 1; i <= totalPages; i++) {
            if (i === 1 || i === totalPages || (i >= page - 2 && i <= page + 2)) {
                showPages.push(i);
            } else if (showPages[showPages.length - 1] !== '...') {
                showPages.push('...');
            }
        }

        showPages.forEach(p => {
            if (p === '...') {
                html += '<span class="pagination-info">...</span>';
            } else {
                html += `<button class="pagination-btn ${p === page ? 'active' : ''}" data-page="${p}">${p}</button>`;
            }
        });

        // Next button
        html += `<button class="pagination-btn" data-page="${page + 1}" ${page === totalPages ? 'disabled' : ''}>下一页 »</button>`;

        html += '</div>';

        container.innerHTML = html;

        // Event handlers
        container.querySelectorAll('.pagination-btn:not([disabled])').forEach(btn => {
            btn.addEventListener('click', () => {
                const newPage = parseInt(btn.dataset.page);
                if (newPage !== page) {
                    onChange(newPage);
                }
            });
        });
    }
};

// ============================================
// Table Component Helper
// ============================================

const Table = {
    render(options = {}) {
        const {
            container,
            columns = [],
            data = [],
            emptyText = 'No data available'
        } = options;

        if (data.length === 0) {
            container.innerHTML = `
                <div class="empty-state">
                    <div class="empty-state-icon">📋</div>
                    <div class="empty-state-title">${emptyText}</div>
                </div>
            `;
            return;
        }

        let html = '<div class="table-container"><table class="table"><thead><tr>';

        // Header
        columns.forEach(col => {
            html += `<th style="${col.width ? 'width:' + col.width : ''}">${col.title}</th>`;
        });
        html += '</tr></thead><tbody>';

        // Body
        data.forEach((row, index) => {
            html += '<tr>';
            columns.forEach(col => {
                let value = row[col.key];
                if (col.render) {
                    value = col.render(value, row, index);
                }
                html += `<td>${value !== undefined && value !== null ? value : '-'}</td>`;
            });
            html += '</tr>';
        });

        html += '</tbody></table></div>';
        container.innerHTML = html;
    }
};

// Export
window.Toast = Toast;
window.Modal = Modal;
window.Loading = Loading;
window.Pagination = Pagination;
window.Table = Table;
