/**
 * UniEvent Animation System
 * Handles smooth page transitions, loading states, and micro-interactions
 * 
 * Usage:
 *   AnimationSystem.pageTransition(oldElement, newElement);
 *   AnimationSystem.showLoading();
 *   AnimationSystem.addButtonRipple(button, event);
 */

const AnimationSystem = {
  // Configuration
  config: {
    pageTransitionDuration: 400,
    loadingShowDelay: 200,
    reduceMotion: window.matchMedia('(prefers-reduced-motion: reduce)').matches,
  },

  // ============================================
  // 1. PAGE TRANSITIONS
  // ============================================

  /**
   * Smooth page transition with fade and slide
   * @param {HTMLElement} oldElement - Current page element
   * @param {HTMLElement} newElement - New page element
   * @param {Object} options - Transition options
   */
  pageTransition: async function(oldElement, newElement, options = {}) {
    const {
      exitDuration = 300,
      enterDuration = 400,
      type = 'slide' // 'slide', 'fade', 'slideUp'
    } = options;

    if (this.config.reduceMotion) {
      oldElement.style.display = 'none';
      newElement.style.display = 'block';
      return Promise.resolve();
    }

    // Exit animation
    if (oldElement) {
      oldElement.classList.add(`page-exit`);
      await this.sleep(exitDuration);
      oldElement.style.display = 'none';
      oldElement.classList.remove(`page-exit`);
    }

    // Enter animation
    newElement.style.display = 'block';
    newElement.classList.add(`page-enter`);

    return new Promise(resolve => {
      setTimeout(() => {
        newElement.classList.remove(`page-enter`);
        resolve();
      }, enterDuration);
    });
  },

  /**
   * Batch fade in elements
   * @param {HTMLElement|NodeList} elements - Elements to animate
   * @param {Number} staggerDelay - Delay between elements (ms)
   */
  batchFadeIn: function(elements, staggerDelay = 50) {
    const nodeList = elements instanceof NodeList ? Array.from(elements) : [elements];
    
    nodeList.forEach((element, index) => {
      element.style.animation = `none`;
      element.offsetHeight; // Trigger reflow
      element.classList.add(`page-fade-in`);
      element.style.animationDelay = `${index * staggerDelay}ms`;
    });
  },

  // ============================================
  // 2. LOADING ANIMATIONS
  // ============================================

  loadingTimeout: null,
  loadingElement: null,

  /**
   * Show loading indicator with progress bar
   * @param {String} message - Loading message (optional)
   */
  showLoading: function(message = 'Loading...') {
    // Only show if loading takes more than 200ms
    this.loadingTimeout = setTimeout(() => {
      const progressBar = document.querySelector('.progress-bar');
      
      if (progressBar) {
        progressBar.classList.add('active');
      } else {
        // Create progress bar if it doesn't exist
        const bar = document.createElement('div');
        bar.className = 'progress-bar active';
        document.body.appendChild(bar);
        this.loadingElement = bar;
      }

      // Show spinner overlay if provided
      const overlay = document.querySelector('.loading-overlay');
      if (overlay && overlay.style.display === 'none') {
        overlay.style.display = 'flex';
        overlay.classList.add('animate-page');
      }
    }, this.config.loadingShowDelay);
  },

  /**
   * Hide loading indicator
   */
  hideLoading: function() {
    clearTimeout(this.loadingTimeout);

    const progressBar = document.querySelector('.progress-bar');
    if (progressBar) {
      progressBar.classList.remove('active');
      progressBar.classList.add('complete');

      // Remove after animation completes
      setTimeout(() => {
        progressBar.classList.remove('complete');
      }, 600);
    }

    const overlay = document.querySelector('.loading-overlay');
    if (overlay && overlay.style.display === 'flex') {
      overlay.style.opacity = '0';
      setTimeout(() => {
        overlay.style.display = 'none';
        overlay.style.opacity = '1';
      }, 300);
    }
  },

  // ============================================
  // 3. BUTTON INTERACTIONS
  // ============================================

  /**
   * Add ripple effect to button on click
   * @param {HTMLElement} button - Button element
   * @param {Event} event - Click event
   */
  addButtonRipple: function(button, event) {
    if (this.config.reduceMotion) return;

    const ripple = document.createElement('span');
    const rect = button.getBoundingClientRect();
    const size = Math.max(rect.width, rect.height);
    const x = event.clientX - rect.left - size / 2;
    const y = event.clientY - rect.top - size / 2;

    ripple.style.width = ripple.style.height = size + 'px';
    ripple.style.left = x + 'px';
    ripple.style.top = y + 'px';
    ripple.className = 'ripple';

    // Ensure button has position relative
    if (getComputedStyle(button).position === 'static') {
      button.style.position = 'relative';
    }

    button.appendChild(ripple);

    // Remove ripple after animation
    setTimeout(() => ripple.remove(), 600);
  },

  /**
   * Add click feedback to button
   * @param {HTMLElement} button - Button element
   */
  addButtonClickFeedback: function(button) {
    button.addEventListener('click', (event) => {
      if (!this.config.reduceMotion) {
        button.style.animation = 'buttonPress 200ms cubic-bezier(0.34, 1.56, 0.64, 1)';
        setTimeout(() => {
          button.style.animation = '';
        }, 200);
      }
    });
  },

  // ============================================
  // 4. CARD ANIMATIONS
  // ============================================

  /**
   * Animate card entrance with stagger
   * @param {NodeList|Array} cards - Card elements
   * @param {Boolean} stagger - Apply stagger effect
   */
  animateCardEntrance: function(cards, stagger = true) {
    const cardArray = Array.from(cards);

    cardArray.forEach((card, index) => {
      card.classList.add('card-enter');
      if (stagger) {
        card.classList.add(`card-enter-${Math.min(index + 1, 6)}`);
      }
    });
  },

  /**
   * Add hover lift effect to card
   * @param {HTMLElement} card - Card element
   */
  addCardHoverEffect: function(card) {
    card.classList.add('card-hover');
  },

  // ============================================
  // 5. MODAL ANIMATIONS
  // ============================================

  /**
   * Show modal with animation
   * @param {HTMLElement} modal - Modal element
   * @param {HTMLElement} backdrop - Backdrop element
   */
  showModal: function(modal, backdrop) {
    if (this.config.reduceMotion) {
      backdrop.style.display = 'block';
      modal.style.display = 'block';
      return;
    }

    backdrop.style.display = 'block';
    backdrop.classList.add('modal-backdrop-enter');

    modal.style.display = 'block';
    modal.classList.add('modal-enter');

    // Remove animation classes after completion
    setTimeout(() => {
      backdrop.classList.remove('modal-backdrop-enter');
      modal.classList.remove('modal-enter');
    }, 300);
  },

  /**
   * Hide modal with animation
   * @param {HTMLElement} modal - Modal element
   * @param {HTMLElement} backdrop - Backdrop element
   */
  hideModal: function(modal, backdrop) {
    if (this.config.reduceMotion) {
      backdrop.style.display = 'none';
      modal.style.display = 'none';
      return;
    }

    backdrop.classList.add('modal-backdrop-exit');
    modal.classList.add('modal-exit');

    setTimeout(() => {
      backdrop.style.display = 'none';
      modal.style.display = 'none';

      backdrop.classList.remove('modal-backdrop-exit');
      modal.classList.remove('modal-exit');
    }, 250);
  },

  // ============================================
  // 6. DROPDOWN ANIMATIONS
  // ============================================

  /**
   * Toggle dropdown with smooth animation
   * @param {HTMLElement} dropdown - Dropdown element
   */
  toggleDropdown: function(dropdown) {
    if (dropdown.style.display === 'none' || !dropdown.style.display) {
      this.openDropdown(dropdown);
    } else {
      this.closeDropdown(dropdown);
    }
  },

  /**
   * Open dropdown
   * @param {HTMLElement} dropdown - Dropdown element
   */
  openDropdown: function(dropdown) {
    if (this.config.reduceMotion) {
      dropdown.style.display = 'block';
      return;
    }

    dropdown.style.display = 'block';
    dropdown.offsetHeight; // Trigger reflow
    dropdown.classList.add('dropdown-enter');

    setTimeout(() => {
      dropdown.classList.remove('dropdown-enter');
    }, 250);
  },

  /**
   * Close dropdown
   * @param {HTMLElement} dropdown - Dropdown element
   */
  closeDropdown: function(dropdown) {
    if (this.config.reduceMotion) {
      dropdown.style.display = 'none';
      return;
    }

    dropdown.classList.add('dropdown-exit');

    setTimeout(() => {
      dropdown.style.display = 'none';
      dropdown.classList.remove('dropdown-exit');
    }, 200);
  },

  // ============================================
  // 7. TOAST NOTIFICATIONS
  // ============================================

  /**
   * Show toast notification
   * @param {String} message - Toast message
   * @param {String} type - Type: 'success', 'error', 'info', 'warning'
   * @param {Number} duration - Auto-hide duration (ms)
   */
  showToast: function(message, type = 'info', duration = 3000) {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type} toast-enter`;
    toast.innerHTML = `
      <div class="flex items-center gap-3">
        <span class="material-symbols-outlined">${this.getToastIcon(type)}</span>
        <span>${message}</span>
      </div>
    `;

    const container = document.querySelector('.toast-container') || this.createToastContainer();
    container.appendChild(toast);

    // Auto-hide
    if (duration > 0) {
      setTimeout(() => {
        toast.classList.add('toast-exit');
        setTimeout(() => toast.remove(), 300);
      }, duration);
    }

    return toast;
  },

  /**
   * Get icon for toast type
   * @param {String} type - Toast type
   */
  getToastIcon: function(type) {
    const icons = {
      success: 'check_circle',
      error: 'error',
      info: 'info',
      warning: 'warning'
    };
    return icons[type] || 'info';
  },

  /**
   * Create toast container
   */
  createToastContainer: function() {
    const container = document.createElement('div');
    container.className = 'toast-container fixed bottom-4 right-4 space-y-3 z-50';
    document.body.appendChild(container);
    return container;
  },

  // ============================================
  // 8. LIST ITEM ANIMATIONS
  // ============================================

  /**
   * Animate list items with stagger
   * @param {NodeList|Array} items - List item elements
   */
  animateListItems: function(items) {
    const itemArray = Array.from(items);

    itemArray.forEach((item, index) => {
      if (index < 5) {
        item.classList.add('list-item-enter');
        item.classList.add(`list-item-enter-${index + 1}`);
      }
    });
  },

  /**
   * Remove list item with animation
   * @param {HTMLElement} item - Item to remove
   */
  removeListItemAnimated: function(item) {
    if (this.config.reduceMotion) {
      item.remove();
      return;
    }

    item.classList.add('list-item-exit');

    setTimeout(() => {
      item.remove();
    }, 200);
  },

  // ============================================
  // 9. INPUT VALIDATION ANIMATIONS
  // ============================================

  /**
   * Shake input on error
   * @param {HTMLElement} input - Input element
   */
  shakeInput: function(input) {
    if (this.config.reduceMotion) return;

    input.classList.add('input-error');

    setTimeout(() => {
      input.classList.remove('input-error');
    }, 300);
  },

  /**
   * Add focus animation to input
   * @param {HTMLElement} input - Input element
   */
  addInputFocusEffect: function(input) {
    input.addEventListener('focus', () => {
      if (!this.config.reduceMotion) {
        input.style.boxShadow = '0 0 0 3px rgba(33, 65, 127, 0.1)';
      }
    });

    input.addEventListener('blur', () => {
      input.style.boxShadow = '';
    });
  },

  // ============================================
  // 10. SCROLL ANIMATIONS
  // ============================================

  /**
   * Animate element on scroll visibility
   * @param {String} selector - Element selector
   */
  observeScroll: function(selector) {
    const elements = document.querySelectorAll(selector);

    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          entry.target.classList.add('scroll-fade-in');
          observer.unobserve(entry.target);
        }
      });
    }, {
      threshold: 0.1,
      rootMargin: '0px 0px -100px 0px'
    });

    elements.forEach(el => observer.observe(el));
  },

  // ============================================
  // 11. SIDEBAR ANIMATIONS
  // ============================================

  /**
   * Toggle sidebar with animation
   * @param {HTMLElement} sidebar - Sidebar element
   * @param {Boolean} isOpen - Open state
   */
  toggleSidebar: function(sidebar, isOpen) {
    if (this.config.reduceMotion) {
      sidebar.style.display = isOpen ? 'block' : 'none';
      return;
    }

    if (isOpen) {
      sidebar.classList.add('sidebar-enter');
      setTimeout(() => sidebar.classList.remove('sidebar-enter'), 300);
    } else {
      sidebar.classList.add('sidebar-exit');
      setTimeout(() => {
        sidebar.style.display = 'none';
        sidebar.classList.remove('sidebar-exit');
      }, 250);
    }
  },

  // ============================================
  // 12. UTILITY METHODS
  // ============================================

  /**
   * Sleep/delay helper
   * @param {Number} ms - Milliseconds to sleep
   */
  sleep: function(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  },

  /**
   * Get animation duration
   * @param {HTMLElement} element - Element with animation
   */
  getAnimationDuration: function(element) {
    const style = getComputedStyle(element);
    const duration = style.animationDuration;
    return parseFloat(duration) * 1000;
  },

  /**
   * Wait for animation to complete
   * @param {HTMLElement} element - Element with animation
   */
  waitForAnimation: function(element) {
    return new Promise(resolve => {
      const handler = () => {
        element.removeEventListener('animationend', handler);
        resolve();
      };
      element.addEventListener('animationend', handler);
    });
  },

  /**
   * Disable animations temporarily
   */
  disableAnimations: function() {
    document.documentElement.style.setProperty('--animate-duration', '0.01ms');
  },

  /**
   * Enable animations
   */
  enableAnimations: function() {
    document.documentElement.style.removeProperty('--animate-duration');
  },

  /**
   * Check if animations are enabled
   */
  animationsEnabled: function() {
    return !this.config.reduceMotion && !window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  },

  /**
   * Initialize animation system
   */
  init: function() {
    // Setup global click handler for ripples
    document.addEventListener('click', (e) => {
      if (e.target.classList.contains('btn-ripple') && this.animationsEnabled()) {
        this.addButtonRipple(e.target, e);
      }
    });

    // Setup scroll observer
    this.observeScroll('[data-animate-scroll]');

    console.log('✨ Animation System initialized');
  }
};

// Auto-initialize when DOM is ready
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', () => AnimationSystem.init());
} else {
  AnimationSystem.init();
}

// Export for use in modules
if (typeof module !== 'undefined' && module.exports) {
  module.exports = AnimationSystem;
}
