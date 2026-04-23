/**
 * Landing Page Interactions
 * Handles button clicks, form interactions, and other user interactions
 */

class LandingPageInteractions {
    constructor() {
        this.init();
    }

    init() {
        this.setupButtonInteractions();
        this.setupFormInteractions();
        this.setupCardInteractions();
        this.setupSmoothScroll();
    }

    /**
     * Setup button click interactions
     */
    setupButtonInteractions() {
        // Explore Events Button
        const exploreBtn = document.querySelector('.btn-primary');
        if (exploreBtn) {
            exploreBtn.addEventListener('click', () => {
                window.location.href = '/clubs';
            });
        }

        // Get Started Button
        const getStartedBtn = document.querySelector('.btn-secondary');
        if (getStartedBtn) {
            getStartedBtn.addEventListener('click', () => {
                window.location.href = '/signup';
            });
        }

        // Become an Organizer Button
        const organizerBtn = document.querySelectorAll('button')[document.querySelectorAll('button').length - 2];
        if (organizerBtn && organizerBtn.textContent.includes('Organizer')) {
            organizerBtn.addEventListener('click', () => {
                window.location.href = '/signup';
            });
        }

        // Download App Button
        const downloadBtn = document.querySelectorAll('button')[document.querySelectorAll('button').length - 1];
        if (downloadBtn && downloadBtn.textContent.includes('Download')) {
            downloadBtn.addEventListener('click', () => {
                this.showNotification('App download links opening...');
            });
        }

        // View All Events Button
        const viewAllBtn = document.querySelector('.view-all-btn');
        if (viewAllBtn) {
            viewAllBtn.addEventListener('click', () => {
                window.location.href = '/clubs';
            });
        }

        // Event Card Add Buttons
        document.querySelectorAll('.event-card .material-symbols-outlined').forEach((btn, index) => {
            if (btn.textContent === 'add') {
                btn.closest('button').addEventListener('click', (e) => {
                    e.stopPropagation();
                    this.handleEventRegistration(btn.closest('.event-card'), index);
                });
            }
        });

        // Event Card Click
        document.querySelectorAll('.event-card').forEach((card, index) => {
            card.addEventListener('click', () => {
                window.location.href = '/clubs';
            });
        });
    }

    /**
     * Handle event registration
     */
    handleEventRegistration(card, index) {
        const eventName = card.querySelector('h5').textContent;
        const button = card.querySelector('.material-symbols-outlined').closest('button');
        
        // Add visual feedback
        button.classList.add('bg-primary', 'text-white');
        button.classList.remove('bg-slate-100', 'dark:bg-slate-800');
        
        this.showNotification(`Added "${eventName}" to your calendar!`, 'success');

        // Reset after 2 seconds
        setTimeout(() => {
            button.classList.remove('bg-primary', 'text-white');
            button.classList.add('bg-slate-100', 'dark:bg-slate-800');
        }, 2000);
    }

    /**
     * Setup form interactions
     */
    setupFormInteractions() {
        const newsletterForm = document.querySelector('form');
        if (newsletterForm) {
            newsletterForm.addEventListener('submit', async (e) => {
                e.preventDefault();
                const emailInput = newsletterForm.querySelector('input[type="email"]');
                const email = emailInput.value;
                
                if (this.validateEmail(email)) {
                    try {
                        // Try to submit to backend if endpoint exists
                        // const response = await fetch('/api/newsletter/subscribe', {
                        //     method: 'POST',
                        //     headers: { 'Content-Type': 'application/json' },
                        //     body: JSON.stringify({ email })
                        // });
                        
                        // if (response.ok) {
                        //     this.showNotification(`Subscribed! Confirmation sent to ${email}`, 'success');
                        // } else {
                        //     this.showNotification('Subscription failed. Please try again.', 'error');
                        // }
                        
                        // For now, just show success
                        this.showNotification(`Thank you! We've sent a confirmation to ${email}`, 'success');
                        newsletterForm.reset();
                    } catch (error) {
                        this.showNotification('An error occurred. Please try again.', 'error');
                    }
                } else {
                    this.showNotification('Please enter a valid email address', 'error');
                }
            });
        }
    }

    /**
     * Setup card hover effects
     */
    setupCardInteractions() {
        // Feature cards
        document.querySelectorAll('.feature-card').forEach(card => {
            card.addEventListener('mouseenter', () => {
                this.addCardGlow(card);
            });
            card.addEventListener('mouseleave', () => {
                this.removeCardGlow(card);
            });
        });

        // Event cards
        document.querySelectorAll('.event-card').forEach(card => {
            card.addEventListener('mouseenter', () => {
                card.style.transform = 'translateY(-8px)';
            });
            card.addEventListener('mouseleave', () => {
                card.style.transform = 'translateY(0)';
            });
        });
    }

    /**
     * Add glow effect to cards
     */
    addCardGlow(card) {
        card.style.boxShadow = 'var(--primary-shadow, 0 0 20px rgba(0, 51, 102, 0.2))';
    }

    /**
     * Remove glow effect from cards
     */
    removeCardGlow(card) {
        card.style.boxShadow = '';
    }

    /**
     * Setup smooth scroll for internal links
     */
    setupSmoothScroll() {
        document.querySelectorAll('a[href^="#"]').forEach(link => {
            link.addEventListener('click', (e) => {
                const href = link.getAttribute('href');
                if (href !== '#') {
                    e.preventDefault();
                    const target = document.querySelector(href);
                    if (target) {
                        target.scrollIntoView({ behavior: 'smooth' });
                    }
                }
            });
        });
    }

    /**
     * Validate email address
     */
    validateEmail(email) {
        const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return re.test(email);
    }

    /**
     * Show notification toast
     */
    showNotification(message, type = 'info') {
        const toast = document.createElement('div');
        toast.className = `fixed bottom-6 right-6 px-6 py-4 rounded-xl text-white font-semibold z-50 shadow-lg animate-slideUp transition-all duration-300 ${
            type === 'success' ? 'bg-green-500' : 
            type === 'error' ? 'bg-red-500' : 
            'bg-primary'
        }`;
        toast.textContent = message;
        
        document.body.appendChild(toast);

        // Auto remove after 3 seconds
        setTimeout(() => {
            toast.classList.add('opacity-0', 'translate-y-4');
            setTimeout(() => {
                toast.remove();
            }, 300);
        }, 3000);
    }
}

// Initialize interactions when DOM is ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        new LandingPageInteractions();
    });
} else {
    new LandingPageInteractions();
}
