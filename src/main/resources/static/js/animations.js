/**
 * Landing Page Animations
 * Handles scroll-triggered animations using Intersection Observer API
 */

class ScrollAnimations {
    constructor() {
        this.observerOptions = {
            threshold: 0.1,
            rootMargin: '0px 0px -100px 0px'
        };
        
        this.init();
    }

    init() {
        this.observeElements();
        this.initCounterAnimations();
    }

    /**
     * Observe elements and trigger animations on scroll
     */
    observeElements() {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    this.animateElement(entry.target);
                    observer.unobserve(entry.target);
                }
            });
        }, this.observerOptions);

        // Observe sections
        document.querySelectorAll('.hero-section, .stats-section, .features-section, .events-section, .cta-section, .footer-section').forEach(section => {
            observer.observe(section);
        });

        // Observe individual items
        document.querySelectorAll('.stat-item, .feature-card, .event-card').forEach(item => {
            observer.observe(item);
        });
    }

    /**
     * Animate element when it comes into view
     */
    animateElement(element) {
        if (element.classList.contains('stat-item')) {
            element.classList.add('item-visible');
        } else if (element.classList.contains('feature-card') || element.classList.contains('event-card')) {
            element.classList.add('item-visible');
        } else {
            element.classList.add('section-visible');
        }
    }

    /**
     * Initialize counter animations for statistics
     */
    initCounterAnimations() {
        const statNumbers = document.querySelectorAll('.stat-number');
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    this.animateCounter(entry.target);
                    observer.unobserve(entry.target);
                }
            });
        }, this.observerOptions);

        statNumbers.forEach(number => {
            observer.observe(number);
        });
    }

    /**
     * Animate number counter
     */
    animateCounter(element) {
        const finalValue = element.textContent;
        const numericValue = parseInt(finalValue.replace(/\D/g, ''));
        const suffix = finalValue.replace(/\d/g, '').trim();
        
        let currentValue = 0;
        const increment = Math.ceil(numericValue / 30);
        const duration = 1200; // 1.2 seconds
        const startTime = Date.now();

        const updateCounter = () => {
            const elapsed = Date.now() - startTime;
            const progress = Math.min(elapsed / duration, 1);
            
            currentValue = Math.floor(numericValue * progress);
            element.textContent = currentValue + suffix;

            if (progress < 1) {
                requestAnimationFrame(updateCounter);
            } else {
                element.textContent = finalValue;
            }
        };

        updateCounter();
    }
}

// Initialize animations when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        new ScrollAnimations();
    });
} else {
    new ScrollAnimations();
}
