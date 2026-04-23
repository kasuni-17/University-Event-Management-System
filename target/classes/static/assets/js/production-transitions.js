/**
 * ============================================================================
 * PRODUCTION-LEVEL PAGE DIRECTOR (V3)
 * High-Performance Smooth Page Transitions Orchestrator
 * ============================================================================
 */

class ProductionTransitions {
    constructor() {
        this.currentPath = window.location.pathname;
        this.historyStack = this._loadHistory();
        this.isTransitioning = false;
        this.sharedElements = new Map();
        
        this.config = {
            duration: 0.45,
            ease: "power2.inOut",
            springEase: "elastic.out(1, 0.75)",
            selector: 'main', // Target container for content swap
            sharedIdAttr: 'data-shared-id',
            revealClass: 'skeleton-reveal'
        };

        this._init();
    }

    _init() {
        this._updateHistory(this.currentPath);
        this._bindLinks();
        this._setupMicroInteractions();
        this._setupScrollReveal();
        window.onpopstate = (e) => this._handlePopState(e);
        console.log("🚀 Production Transitions System Ready");
    }

    // ========================================================================
    // 1. LINK HIJACKING & ROUTING
    // ========================================================================

    _bindLinks() {
        document.body.addEventListener('click', (e) => {
            const anchor = e.target.closest('a');
            if (!anchor) return;

            const href = anchor.getAttribute('href');
            if (this._isEligible(anchor, href)) {
                e.preventDefault();
                this.navigate(href);
            }
        });
    }

    _isEligible(anchor, href) {
        if (!href) return false;
        if (href.startsWith('#')) return false;
        if (href.startsWith('http') && !href.includes(window.location.host)) return false;
        if (anchor.target === '_blank') return false;
        if (this.isTransitioning) return false;
        return true;
    }

    async navigate(url, type = 'push') {
        if (this.isTransitioning) return;
        this.isTransitioning = true;
        
        const direction = this._getDirection(url);
        
        // 1. Capture Shared Elements
        this._captureSharedElements();

        // 2. Animate Outgoing Page
        await this._animateOut(direction);

        // 3. Update URL & Fetch Content
        if (type === 'push') {
            window.history.pushState({ url }, '', url);
            this._updateHistory(url);
        }
        
        try {
            const html = await this._fetchPage(url);
            this._swapContent(html);
            
            // 4. Animate Incoming Page
            await this._animateIn(direction);
            
            // 5. Shared Element Playback
            await this._playSharedElements();
            
            // 6. Cleanup & Re-run scripts
            this._finalizeNavigation();
            
        } catch (err) {
            console.error("Navigation failed", err);
            window.location.href = url; // Fallback
        } finally {
            this.isTransitioning = false;
            this.currentPath = url;
        }
    }

    async _fetchPage(url) {
        const resp = await fetch(url);
        if (!resp.ok) throw new Error("Failed to load page");
        return await resp.text();
    }

    _swapContent(html) {
        const parser = new DOMParser();
        const doc = parser.parseFromString(html, 'text/html');
        const newMain = doc.querySelector(this.config.selector);
        const currentMain = document.querySelector(this.config.selector);

        if (newMain && currentMain) {
            currentMain.innerHTML = newMain.innerHTML;
            document.title = doc.title;
            // Transfer specific body classes if needed
            document.body.className = doc.body.className;
        }
    }

    _finalizeNavigation() {
        window.scrollTo(0, 0);
        this._bindLinks(); // Re-bind just in case, though event delegation is used
        this._setupMicroInteractions();
        this._setupScrollReveal();
        
        // Re-run scripts (basic approach: find scripts that were marked to re-run or all scripts in main)
        document.querySelectorAll('main script').forEach(oldScript => {
            const newScript = document.createElement('script');
            Array.from(oldScript.attributes).forEach(attr => newScript.setAttribute(attr.name, attr.value));
            newScript.appendChild(document.createTextNode(oldScript.innerHTML));
            oldScript.parentNode.replaceChild(newScript, oldScript);
        });

        // Trigger custom navigation event
        window.dispatchEvent(new CustomEvent('pageTransitionComplete'));
    }

    // ========================================================================
    // 2. ANIMATION CORE
    // ========================================================================

    async _animateOut(direction) {
        const main = document.querySelector(this.config.selector);
        const xOffset = direction === 'forward' ? -50 : 50;
        
        return gsap.to(main, {
            opacity: 0,
            x: xOffset,
            scale: 0.98,
            duration: this.config.duration * 0.7,
            ease: "power2.in",
            clearProps: "all"
        });
    }

    async _animateIn(direction) {
        const main = document.querySelector(this.config.selector);
        const xOffset = direction === 'forward' ? 50 : -50;
        
        gsap.set(main, {
            opacity: 0,
            x: xOffset,
            scale: 0.98
        });

        return gsap.to(main, {
            opacity: 1,
            x: 0,
            scale: 1,
            duration: this.config.duration,
            ease: this.config.ease,
            delay: 0.1
        });
    }

    // ========================================================================
    // 3. SHARED ELEMENT SYSTEM (FLIP)
    // ========================================================================

    _captureSharedElements() {
        this.sharedElements.clear();
        document.querySelectorAll(`[${this.config.sharedIdAttr}]`).forEach(el => {
            const id = el.getAttribute(this.config.sharedIdAttr);
            const rect = el.getBoundingClientRect();
            this.sharedElements.set(id, {
                id,
                rect,
                src: el.src || null,
                innerHTML: el.innerHTML,
                className: el.className,
                style: window.getComputedStyle(el)
            });
        });
    }

    async _playSharedElements() {
        const playPromises = [];
        
        document.querySelectorAll(`[${this.config.sharedIdAttr}]`).forEach(newEl => {
            const id = newEl.getAttribute(this.config.sharedIdAttr);
            const prevData = this.sharedElements.get(id);

            if (prevData) {
                const newRect = newEl.getBoundingClientRect();
                
                // Create Ghost element for transition
                const ghost = document.createElement('div');
                ghost.className = 'shared-element-ghost ' + prevData.className;
                ghost.innerHTML = prevData.innerHTML;
                
                // Copy key styles
                ghost.style.position = 'fixed';
                ghost.style.top = prevData.rect.top + 'px';
                ghost.style.left = prevData.rect.left + 'px';
                ghost.style.width = prevData.rect.width + 'px';
                ghost.style.height = prevData.rect.height + 'px';
                ghost.style.zIndex = '10000';
                ghost.style.pointerEvents = 'none';
                
                document.body.appendChild(ghost);
                
                // Hide original new element momentarily
                newEl.style.opacity = '0';
                
                const timeline = gsap.timeline({
                    onComplete: () => {
                        ghost.remove();
                        newEl.style.opacity = '1';
                    }
                });

                timeline.to(ghost, {
                    top: newRect.top,
                    left: newRect.left,
                    width: newRect.width,
                    height: newRect.height,
                    duration: 0.6,
                    ease: "expo.out"
                });

                playPromises.push(timeline);
            }
        });

        return Promise.all(playPromises);
    }

    // ========================================================================
    // 4. UTILITIES & HELPERS
    // ========================================================================

    _loadHistory() {
        return JSON.parse(sessionStorage.getItem('ptStack') || '[]');
    }

    _updateHistory(url) {
        if (!this.historyStack.includes(url)) {
            this.historyStack.push(url);
            if (this.historyStack.length > 50) this.historyStack.shift();
            sessionStorage.setItem('ptStack', JSON.stringify(this.historyStack));
        }
    }

    _getDirection(targetUrl) {
        const curIdx = this.historyStack.indexOf(this.currentPath);
        const tarIdx = this.historyStack.indexOf(targetUrl);
        
        if (tarIdx !== -1 && tarIdx < curIdx) return 'back';
        return 'forward';
    }

    _handlePopState(e) {
        const url = window.location.pathname;
        this.navigate(url, 'pop');
    }

    _setupMicroInteractions() {
        // Button scales
        document.querySelectorAll('button, .btn, .btn-animate').forEach(btn => {
            btn.classList.add('btn-animate');
        });
        
        // Card transitions
        document.querySelectorAll('.card, [data-shared-id]').forEach(card => {
            card.classList.add('card-animate');
        });
    }

    _setupScrollReveal() {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('revealed');
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.1 });

        document.querySelectorAll('.' + this.config.revealClass).forEach(el => observer.observe(el));
    }
}

// Global Export
window.ProductionDirector = new ProductionTransitions();
