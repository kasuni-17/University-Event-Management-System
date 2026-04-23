/**
 * ============================================================================
 * ADVANCED PAGE TRANSITION SYSTEM
 * Premium SaaS-Level Animations (Stripe/Linear/Vercel Quality)
 * ============================================================================
 * 
 * Features:
 * - GPU-accelerated page transitions (FLIP layout technique)
 * - Shared element transitions with position/size interpolation
 * - Spring-based physics animations
 * - Skeleton loading + progressive reveal
 * - Micro-interactions (buttons, cards, inputs)
 * - Route transition overlays
 * - Modal/drawer animations
 * - Scroll-based animations
 * - Full accessibility support (prefers-reduced-motion)
 * - 60fps performance optimized
 */

// ============================================================================
// 1. ANIMATION TIMELINE FACTORY
// ============================================================================

class AdvancedTransitions {
    constructor(options = {}) {
        this.config = {
            // Duration settings (ms)
            pageTransitionDuration: 400,
            sharedElementDuration: 600,
            skeletonDuration: 300,
            microInteractionDuration: 200,
            modalDuration: 350,
            scrollAnimationDuration: 500,

            // Easing functions
            easing: {
                standard: 'cubic-bezier(0.4, 0, 0.2, 1)',      // Material motion
                decelerate: 'cubic-bezier(0.0, 0, 0.2, 1)',    // Heavy to light
                accelerate: 'cubic-bezier(0.4, 0, 1, 1)',      // Light to heavy
                springy: 'cubic-bezier(0.34, 1.56, 0.64, 1)',  // Spring bounce
            },

            // Performance settings
            useGPUAcceleration: true,
            reduceMotion: window.matchMedia('(prefers-reduced-motion: reduce)').matches,
            enableDebug: false,

            // Layout transition settings
            sharedElementSelector: '[data-shared-element]',
            transitionContainerSelector: '.page-transition-container',

            // Scroll animation settings
            scrollTrigger: true,
            intersectionOptions: { threshold: 0.2 },

            ...options
        };

        this.activeTransitions = new Map();
        this.sharedElementSnapshots = new Map();
        this.isTransitioning = false;

        this._init();
    }

    _init() {
        // Auto-disable animations on low-end devices
        if (this._isLowEndDevice()) {
            this.config.reduceMotion = true;
        }

        // Listen for reduced motion preference changes
        window.matchMedia('(prefers-reduced-motion: reduce)').addEventListener('change', (e) => {
            this.config.reduceMotion = e.matches;
        });

        this._setupScrollObserver();
        this._log('Advanced Transitions initialized');
    }

    _isLowEndDevice() {
        if (navigator.deviceMemory && navigator.deviceMemory < 4) return true;
        if (navigator.hardwareConcurrency && navigator.hardwareConcurrency < 2) return true;
        if (window.innerWidth < 500) return true; // Ultra-mobile
        return false;
    }

    _log(msg, obj) {
        if (this.config.enableDebug) {
            console.log(`[AdvancedTransitions] ${msg}`, obj || '');
        }
    }

    // ========================================================================
    // 2. SHARED ELEMENT TRANSITIONS (FLIP Technique)
    // ========================================================================

    /**
     * Capture positions of shared elements before navigation
     * @returns {Map} Element snapshots with position data
     */
    captureSharedElements() {
        const snapshots = new Map();

        document.querySelectorAll(this.config.sharedElementSelector).forEach((el) => {
            const id = el.dataset.sharedElement;
            const rect = el.getBoundingClientRect();

            snapshots.set(id, {
                id,
                element: el,
                startX: rect.x,
                startY: rect.y,
                startWidth: rect.width,
                startHeight: rect.height,
                startOpacity: window.getComputedStyle(el).opacity,
            });
        });

        this.sharedElementSnapshots.set('from', snapshots);
        this._log('Captured shared elements', snapshots.size);
        return snapshots;
    }

    /**
     * Animate shared elements from old positions to new positions
     * Uses FLIP (First, Last, Invert, Play) technique
     */
    animateSharedElements() {
        if (this.config.reduceMotion) return Promise.resolve();

        const fromSnapshots = this.sharedElementSnapshots.get('from');
        if (!fromSnapshots) return Promise.resolve();

        return new Promise((resolve) => {
            const timeline = gsap.timeline({
                onComplete: resolve,
            });

            fromSnapshots.forEach((fromSnapshot, id) => {
                const toElement = document.querySelector(`[data-shared-element="${id}"]`);
                if (!toElement) return;

                const toRect = toElement.getBoundingClientRect();

                // Calculate delta (INVERT)
                const deltaX = fromSnapshot.startX - toRect.x;
                const deltaY = fromSnapshot.startY - toRect.y;
                const scaleX = fromSnapshot.startWidth / toRect.width;
                const scaleY = fromSnapshot.startHeight / toRect.height;

                // Set initial state (FIRST)
                gsap.set(toElement, {
                    x: deltaX,
                    y: deltaY,
                    scaleX: scaleX,
                    scaleY: scaleY,
                    opacity: fromSnapshot.startOpacity,
                });

                // Animate to final position (LAST, PLAY)
                timeline.to(
                    toElement,
                    {
                        x: 0,
                        y: 0,
                        scaleX: 1,
                        scaleY: 1,
                        opacity: 1,
                        duration: this.config.sharedElementDuration / 1000,
                        ease: this.config.easing.standard,
                    },
                    0
                ); // Start at timeline beginning
            });
        });
    }

    // ========================================================================
    // 3. PAGE TRANSITION (Layered Animation)
    // ========================================================================

    /**
     * Animate page out (outgoing page)
     * @param {HTMLElement} element - Element to animate out
     * @returns {Promise}
     */
    pageTransitionOut(element) {
        if (this.config.reduceMotion) {
            gsap.set(element, { opacity: 0 });
            return Promise.resolve();
        }

        return gsap.to(element, {
            opacity: 0,
            y: -20,
            scale: 0.98,
            duration: this.config.pageTransitionDuration / 1000,
            ease: this.config.easing.accelerate,
        });
    }

    /**
     * Animate page in (incoming page)
     * @param {HTMLElement} element - Element to animate in
     * @returns {Promise}
     */
    pageTransitionIn(element) {
        if (this.config.reduceMotion) {
            gsap.set(element, { opacity: 1, y: 0, scale: 1 });
            return Promise.resolve();
        }

        // Initial state
        gsap.set(element, {
            opacity: 0,
            y: 20,
            scale: 0.95,
        });

        return gsap.to(element, {
            opacity: 1,
            y: 0,
            scale: 1,
            duration: this.config.pageTransitionDuration / 1000,
            ease: this.config.easing.decelerate,
        });
    }

    /**
     * Full page transition (out + in)
     * Coordinated animation between outgoing/incoming pages
     */
    transitionPages(fromElement, toElement) {
        if (this.config.reduceMotion) {
            gsap.set(fromElement, { opacity: 0 });
            gsap.set(toElement, { opacity: 1, y: 0, scale: 1 });
            return Promise.resolve();
        }

        const timeline = gsap.timeline();

        // Animate outgoing page
        timeline.to(fromElement, {
            opacity: 0,
            y: -20,
            scale: 0.98,
            duration: this.config.pageTransitionDuration / 1000 * 0.6,
            ease: this.config.easing.accelerate,
        }, 0);

        // Animate incoming page (slight overlap)
        gsap.set(toElement, { opacity: 0, y: 20, scale: 0.95 });
        timeline.to(
            toElement,
            {
                opacity: 1,
                y: 0,
                scale: 1,
                duration: this.config.pageTransitionDuration / 1000 * 0.8,
                ease: this.config.easing.decelerate,
            },
            this.config.pageTransitionDuration / 1000 * 0.2 // Stagger start
        );

        return timeline;
    }

    // ========================================================================
    // 4. ROUTE TRANSITION OVERLAY
    // ========================================================================

    /**
     * Create animated overlay during route transition
     * Subtle gradient wipe or expanding circle reveal
     */
    showTransitionOverlay(clickX = null, clickY = null) {
        if (this.config.reduceMotion) return Promise.resolve();

        // Remove existing overlay
        document.querySelectorAll('.transition-overlay').forEach((el) => el.remove());

        // Create overlay element
        const overlay = document.createElement('div');
        overlay.className = 'transition-overlay';
        overlay.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(135deg, rgba(99, 102, 241, 0.1) 0%, rgba(59, 130, 246, 0.05) 100%);
            z-index: 9999;
            pointer-events: none;
            opacity: 0;
        `;
        document.body.appendChild(overlay);

        // Animate overlay
        const timeline = gsap.timeline({
            onComplete: () => overlay.remove(),
        });

        if (clickX && clickY) {
            // Expanding circle reveal from click position
            return this._expandingCircleReveal(overlay, clickX, clickY);
        } else {
            // Soft fade overlay
            timeline
                .to(overlay, { opacity: 1, duration: 0.2 }, 0)
                .to(overlay, { opacity: 0, duration: 0.2 }, 0.2);
            return timeline;
        }
    }

    _expandingCircleReveal(overlay, clickX, clickY) {
        // Create mask for expanding circle effect
        const maxDist = Math.max(
            Math.hypot(clickX, clickY),
            Math.hypot(window.innerWidth - clickX, clickY),
            Math.hypot(clickX, window.innerHeight - clickY),
            Math.hypot(window.innerWidth - clickX, window.innerHeight - clickY)
        );

        overlay.style.clipPath = `circle(0px at ${clickX}px ${clickY}px)`;

        return gsap.to(overlay, {
            clipPath: `circle(${maxDist}px at ${clickX}px ${clickY}px)`,
            duration: 0.6,
            ease: 'power2.inOut',
        });
    }

    // ========================================================================
    // 5. SKELETON LOADING + PROGRESSIVE REVEAL
    // ========================================================================

    /**
     * Fade skeleton UI and reveal actual content
     * @param {HTMLElement} container - Container with skeleton + content
     * @param {string} skeletonSelector - Selector for skeleton elements
     * @param {string} contentSelector - Selector for content elements
     */
    revealContent(container, skeletonSelector = '.skeleton', contentSelector = '.content') {
        if (this.config.reduceMotion) {
            gsap.set(`${skeletonSelector}`, { display: 'none' });
            gsap.set(`${contentSelector}`, { opacity: 1 });
            return Promise.resolve();
        }

        const timeline = gsap.timeline();

        // Hide skeleton with fade
        timeline.to(skeletonSelector, {
            opacity: 0,
            duration: 0.2,
        }, 0);

        // Staggered reveal of content elements
        timeline.to(
            `${contentSelector}`,
            {
                opacity: 1,
                y: 0,
                duration: this.config.skeletonDuration / 1000,
                stagger: 0.05, // 50ms between items
                ease: this.config.easing.decelerate,
            },
            0.1 // Start while skeleton fades
        );

        // Hide skeleton completely
        timeline.set(skeletonSelector, { display: 'none' }, 0.3);

        return timeline;
    }

    /**
     * Set initial state for progressive reveal
     */
    setupProgressiveReveal(container, contentSelector = '.content') {
        // Hide skeleton initially (show via CSS)
        gsap.set(`${contentSelector}`, {
            opacity: 0,
            y: 10,
        });
    }

    // ========================================================================
    // 6. MICRO-INTERACTIONS
    // ========================================================================

    /**
     * Button tap animation (scale down + spring back)
     */
    setupButtonInteractions(selector = 'button') {
        document.querySelectorAll(selector).forEach((btn) => {
            btn.addEventListener('pointerdown', (e) => {
                if (this.config.reduceMotion) return;

                gsap.to(btn, {
                    scale: 0.96,
                    duration: 0.1,
                    ease: 'power2.out',
                });
            });

            btn.addEventListener('pointerup pointerleave', (e) => {
                if (this.config.reduceMotion) return;

                gsap.to(btn, {
                    scale: 1,
                    duration: 0.2,
                    ease: this.config.easing.springy,
                });
            });
        });
    }

    /**
     * Card hover animation (lift + shadow)
     */
    setupCardInteractions(selector = '.card, .card-hover') {
        document.querySelectorAll(selector).forEach((card) => {
            card.addEventListener('mouseenter', () => {
                if (this.config.reduceMotion) return;

                gsap.to(card, {
                    y: -6,
                    boxShadow: '0 20px 40px rgba(0, 0, 0, 0.15)',
                    duration: 0.3,
                    ease: this.config.easing.standard,
                });
            });

            card.addEventListener('mouseleave', () => {
                if (this.config.reduceMotion) return;

                gsap.to(card, {
                    y: 0,
                    boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)',
                    duration: 0.3,
                    ease: this.config.easing.standard,
                });
            });
        });
    }

    /**
     * Input focus glow animation
     */
    setupInputInteractions(selector = 'input, textarea') {
        document.querySelectorAll(selector).forEach((input) => {
            input.addEventListener('focus', () => {
                if (this.config.reduceMotion) return;

                gsap.to(input, {
                    boxShadow: '0 0 0 3px rgba(99, 102, 241, 0.1), 0 0 0 1px rgba(99, 102, 241, 0.5)',
                    duration: 0.2,
                    ease: this.config.easing.standard,
                });
            });

            input.addEventListener('blur', () => {
                if (this.config.reduceMotion) return;

                gsap.to(input, {
                    boxShadow: '0 1px 2px rgba(0, 0, 0, 0.05)',
                    duration: 0.2,
                    ease: this.config.easing.standard,
                });
            });
        });
    }

    // ========================================================================
    // 7. NAVIGATION TRANSITIONS
    // ========================================================================

    /**
     * Sidebar expand/collapse with spring animation
     */
    toggleSidebar(sidebar, isOpen) {
        if (this.config.reduceMotion) {
            gsap.set(sidebar, { width: isOpen ? 280 : 64 });
            return Promise.resolve();
        }

        return gsap.to(sidebar, {
            width: isOpen ? 280 : 64,
            duration: 0.4,
            ease: this.config.easing.springy,
        });
    }

    /**
     * Animated tab underline (spring-based)
     */
    animateTabUnderline(underlineElement, targetTab) {
        if (this.config.reduceMotion) {
            const rect = targetTab.getBoundingClientRect();
            const containerRect = targetTab.parentElement.getBoundingClientRect();
            gsap.set(underlineElement, {
                left: rect.left - containerRect.left,
                width: rect.width,
            });
            return Promise.resolve();
        }

        const rect = targetTab.getBoundingClientRect();
        const containerRect = targetTab.parentElement.getBoundingClientRect();

        return gsap.to(underlineElement, {
            left: rect.left - containerRect.left,
            width: rect.width,
            duration: 0.4,
            ease: this.config.easing.springy,
        });
    }

    // ========================================================================
    // 8. MODAL / DRAWER ANIMATIONS
    // ========================================================================

    /**
     * Show modal with spring physics + backdrop blur
     */
    showModal(modalElement, backdropElement = null) {
        if (this.config.reduceMotion) {
            gsap.set(modalElement, { opacity: 1, scale: 1 });
            if (backdropElement) {
                gsap.set(backdropElement, { opacity: 1, backdropFilter: 'blur(4px)' });
            }
            return Promise.resolve();
        }

        const timeline = gsap.timeline();

        // Set initial state
        gsap.set(modalElement, { opacity: 0, scale: 0.95 });
        if (backdropElement) {
            gsap.set(backdropElement, { opacity: 0, backdropFilter: 'blur(0px)' });
        }

        // Animate modal
        timeline.to(
            modalElement,
            {
                opacity: 1,
                scale: 1,
                duration: this.config.modalDuration / 1000,
                ease: this.config.easing.springy,
            },
            0
        );

        // Animate backdrop
        if (backdropElement) {
            timeline.to(
                backdropElement,
                {
                    opacity: 1,
                    backdropFilter: 'blur(4px)',
                    duration: this.config.modalDuration / 1000,
                    ease: this.config.easing.standard,
                },
                0
            );
        }

        return timeline;
    }

    /**
     * Hide modal with spring physics
     */
    hideModal(modalElement, backdropElement = null) {
        if (this.config.reduceMotion) {
            gsap.set(modalElement, { opacity: 0 });
            if (backdropElement) {
                gsap.set(backdropElement, { opacity: 0 });
            }
            return Promise.resolve();
        }

        const timeline = gsap.timeline();

        timeline.to(
            modalElement,
            {
                opacity: 0,
                scale: 0.95,
                duration: this.config.modalDuration / 1000 * 0.8,
                ease: this.config.easing.accelerate,
            },
            0
        );

        if (backdropElement) {
            timeline.to(
                backdropElement,
                {
                    opacity: 0,
                    backdropFilter: 'blur(0px)',
                    duration: this.config.modalDuration / 1000 * 0.8,
                    ease: this.config.easing.standard,
                },
                0
            );
        }

        return timeline;
    }

    /**
     * Show drawer from bottom/side
     */
    showDrawer(drawerElement, direction = 'bottom', backdropElement = null) {
        if (this.config.reduceMotion) {
            gsap.set(drawerElement, { opacity: 1, x: 0, y: 0 });
            if (backdropElement) gsap.set(backdropElement, { opacity: 1 });
            return Promise.resolve();
        }

        const timeline = gsap.timeline();
        const startPosition = direction === 'bottom' ? { y: 100 } : { x: 100 };

        gsap.set(drawerElement, { opacity: 0, ...startPosition });
        if (backdropElement) gsap.set(backdropElement, { opacity: 0 });

        timeline.to(
            drawerElement,
            {
                opacity: 1,
                x: 0,
                y: 0,
                duration: this.config.modalDuration / 1000,
                ease: this.config.easing.springy,
            },
            0
        );

        if (backdropElement) {
            timeline.to(backdropElement, {
                opacity: 1,
                duration: this.config.modalDuration / 1000,
                ease: this.config.easing.standard,
            }, 0);
        }

        return timeline;
    }

    // ========================================================================
    // 9. SCROLL-BASED ANIMATIONS
    // ========================================================================

    _setupScrollObserver() {
        if (!this.config.scrollTrigger) return;

        const observer = new IntersectionObserver((entries) => {
            entries.forEach((entry) => {
                if (entry.isIntersecting) {
                    this._animateOnScroll(entry.target);
                    observer.unobserve(entry.target);
                }
            });
        }, this.config.intersectionOptions);

        // Observe scroll animation targets
        document.querySelectorAll('[data-scroll-animate]').forEach((el) => {
            observer.observe(el);
        });
    }

    _animateOnScroll(element) {
        if (this.config.reduceMotion) {
            gsap.set(element, { opacity: 1, y: 0 });
            return;
        }

        gsap.set(element, { opacity: 0, y: 20 });

        gsap.to(element, {
            opacity: 1,
            y: 0,
            duration: this.config.scrollAnimationDuration / 1000,
            ease: this.config.easing.decelerate,
        });
    }

    /**
     * Setup scroll animations for batch of elements
     */
    setupScrollAnimations(selector = '[data-scroll-animate]') {
        document.querySelectorAll(selector).forEach((el) => {
            gsap.set(el, { opacity: 0, y: 20 });
        });

        this._setupScrollObserver();
    }

    // ========================================================================
    // 10. UTILITY HELPERS
    // ========================================================================

    /**
     * Stagger animation for list of elements
     */
    staggerAnimation(elements, fromState = {}, toState = {}, staggerDelay = 0.05) {
        const defaultFrom = { opacity: 0, y: 10 };
        const defaultTo = { opacity: 1, y: 0 };

        gsap.set(elements, { ...defaultFrom, ...fromState });

        return gsap.to(elements, {
            ...defaultTo,
            ...toState,
            stagger: staggerDelay,
            duration: 0.4,
            ease: this.config.easing.decelerate,
        });
    }

    /**
     * Shake animation (error state)
     */
    shake(element, intensity = 5) {
        if (this.config.reduceMotion) return Promise.resolve();

        const timeline = gsap.timeline();

        for (let i = 0; i < 6; i++) {
            timeline.to(element, {
                x: (i % 2) * intensity * 2 - intensity,
                duration: 0.05,
            }, 0 + i * 0.05);
        }

        timeline.to(element, { x: 0, duration: 0.05 }, 0.3);

        return timeline;
    }

    /**
     * Pulse animation (attention)
     */
    pulse(element, duration = 1) {
        if (this.config.reduceMotion) return;

        gsap.to(element, {
            scale: 1.05,
            repeat: -1,
            yoyo: true,
            duration: duration,
            ease: 'sine.inOut',
        });
    }

    /**
     * Cleanup active animations
     */
    killAll() {
        gsap.killTweensOf('*');
        this.activeTransitions.clear();
    }
}

// ============================================================================
// 11. EXPORT & INITIALIZATION
// ============================================================================

// Create global instance
window.AdvancedTransitions = window.AdvancedTransitions || AdvancedTransitions;

// Auto-initialize on DOM ready
document.addEventListener('DOMContentLoaded', () => {
    if (!window.advancedTransitions) {
        window.advancedTransitions = new AdvancedTransitions();
    }
});

// Export for module systems
if (typeof module !== 'undefined' && module.exports) {
    module.exports = AdvancedTransitions;
}
