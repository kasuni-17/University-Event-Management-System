/**
 * ============================================================================
 * UNIEVENT PRODUCTION JS v1.0
 * Intelligent Page Transitions & UI Stability
 * ============================================================================
 */

(function () {
    class UniEventUI {
        constructor() {
            this.config = {
                duration: 350,
                activeClass: 'active',
                transitionContainer: 'main',
            };
            this._init();
        }

        _init() {
            this._handleInitialLoad();
            this._setupActiveLinks();
            this._interceptLinks();
            this._ensureLayoutRoot();
            this._setupStaggeredAnimations();
            this._setupScrollEffects();
        }

        _handleInitialLoad() {
            const container = document.querySelector(this.config.transitionContainer);
            if (container) {
                container.classList.add('page-transition-enter');
                requestAnimationFrame(() => {
                    container.classList.add('page-transition-active');
                });
            }
        }

        _setupActiveLinks() {
            const currentPath = window.location.pathname.split('/').pop() || 'landing-page.html';
            const navLinks = document.querySelectorAll('.nav-link, nav a');
            
            navLinks.forEach(link => {
                const href = link.getAttribute('href');
                if (!href) return;
                
                const linkFile = href.split('/').pop().split('?')[0];
                const currentFile = currentPath.split('?')[0];

                if (linkFile === currentFile) {
                    link.classList.add(this.config.activeClass);
                } else {
                    link.classList.remove(this.config.activeClass);
                }
            });
        }

        _interceptLinks() {
            document.addEventListener('click', (e) => {
                const anchor = e.target.closest('a[href]:not([target="_blank"]):not([href^="#"]):not([href^="javascript:"])');
                if (!anchor || anchor.classList.contains('no-transition')) return;

                const url = anchor.getAttribute('href');
                if (!url) return;

                // Only intercept internal links
                const targetUrl = new URL(url, window.location.origin);
                if (targetUrl.origin !== window.location.origin) return;

                e.preventDefault();
                this.navigate(url);
            });
        }

        navigate(url) {
            const container = document.querySelector(this.config.transitionContainer);
            if (!container) {
                window.location.href = url;
                return;
            }

            container.classList.remove('page-transition-active');
            container.classList.add('page-exit');

            setTimeout(() => {
                window.location.href = url;
            }, this.config.duration);
        }

        _ensureLayoutRoot() {
            // Ensure body has a consistent structure
            if (!document.getElementById('layout-root')) {
                const wrapper = document.createElement('div');
                wrapper.id = 'layout-root';
                while (document.body.firstChild) {
                    wrapper.appendChild(document.body.firstChild);
                }
                document.body.appendChild(wrapper);
            }
            
            // Fix header if it exists
            const header = document.querySelector('header');
            if (header) {
                header.classList.add('sticky-nav');
            }
        }

        _setupStaggeredAnimations() {
            const observer = new IntersectionObserver((entries) => {
                entries.forEach(entry => {
                    if (entry.isIntersecting) {
                        entry.target.classList.add('animate');
                        observer.unobserve(entry.target);
                    }
                });
            }, { threshold: 0.1 });

            document.querySelectorAll('.stagger-container').forEach(container => {
                observer.observe(container);
            });
        }

        _setupScrollEffects() {
            const nav = document.querySelector('header.sticky-nav');
            if (!nav) return;

            window.addEventListener('scroll', () => {
                if (window.scrollY > 20) {
                    nav.classList.add('scrolled');
                } else {
                    nav.classList.remove('scrolled');
                }
            }, { passive: true });
        }
    }

    // Initialize on DOMContentLoaded
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => new UniEventUI());
    } else {
        new UniEventUI();
    }
})();
