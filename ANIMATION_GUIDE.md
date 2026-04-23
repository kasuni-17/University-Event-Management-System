# Animation System Implementation Guide

## Overview

The UniEvent Animation System provides smooth, modern transitions and micro-interactions for a seamless SaaS-like experience. All animations are optimized for performance using CSS transforms and opacity.

## Files

- **`assets/css/animations.css`** - All animation definitions and keyframes
- **`assets/js/animation-system.js`** - JavaScript utilities for managing animations
- **`assets/js/animation-system.js`** - Animation helper functions

## Quick Start

### 1. Include in HTML

```html
<!-- In <head> -->
<link rel="stylesheet" href="assets/css/animations.css">

<!-- Before </body> -->
<script src="assets/js/animation-system.js"></script>
```

### 2. Basic Usage

```javascript
// Show loading
AnimationSystem.showLoading();

// Hide loading
AnimationSystem.hideLoading();

// Show toast
AnimationSystem.showToast('Operation successful!', 'success', 3000);
```

---

## 1. PAGE TRANSITIONS

### Fade + Slide Transition

**HTML:**
```html
<div id="currentPage" class="page-content">
  <!-- Current page content -->
</div>

<div id="newPage" class="page-content" style="display: none;">
  <!-- New page content -->
</div>
```

**JavaScript:**
```javascript
const oldPage = document.getElementById('currentPage');
const newPage = document.getElementById('newPage');

await AnimationSystem.pageTransition(oldPage, newPage);
```

**CSS Classes:**
- `.page-enter` - Slide in from right (300-400ms)
- `.page-exit` - Slide out to left (300ms)
- `.page-fade-in` - Simple fade in (400ms)
- `.page-fade-out` - Simple fade out (300ms)

---

## 2. LOADING ANIMATIONS

### Progress Bar

**HTML:**
```html
<div class="progress-bar"></div>
```

**JavaScript:**
```javascript
// Show loading (appears after 200ms if still loading)
AnimationSystem.showLoading();

// Once loading complete
AnimationSystem.hideLoading();
```

### Spinner

**HTML:**
```html
<!-- Small spinner -->
<span class="spinner small"></span>

<!-- Default spinner -->
<span class="spinner"></span>

<!-- Large spinner -->
<span class="spinner large"></span>
```

### Loading Overlay

**HTML:**
```html
<div class="loading-overlay" style="display: none;">
  <div class="flex flex-col items-center gap-4">
    <span class="spinner"></span>
    <p class="text-sm text-slate-600">Loading resources...</p>
  </div>
</div>
```

---

## 3. BUTTON INTERACTIONS

### Ripple Effect on Click

**HTML:**
```html
<button class="btn-ripple px-4 py-2 bg-primary text-white rounded-lg">
  Click Me
</button>
```

**JavaScript:**
```javascript
// Auto-attached via AnimationSystem.init()
// Or manual:
button.addEventListener('click', (e) => {
  AnimationSystem.addButtonRipple(button, e);
});
```

### Button Click Feedback

**HTML:**
```html
<button class="px-4 py-2 bg-primary text-white rounded-lg">
  Submit
</button>
```

**JavaScript:**
```javascript
AnimationSystem.addButtonClickFeedback(button);
```

### Direct CSS

**HTML:**
```html
<button class="transition-fast hover:scale-95 active:scale-90">
  Click
</button>
```

---

## 4. CARD HOVER EFFECTS

### Lift on Hover

**HTML:**
```html
<div class="card-hover bg-white rounded-xl p-6 shadow-sm">
  Card content that lifts on hover
</div>
```

**Features:**
- Smooth translateY(-5px)
- Enhanced shadow
- 200ms animation

### Card Entrance Animation

**HTML:**
```html
<div class="card-enter bg-white rounded-xl p-6">
  Card with entrance animation
</div>

<!-- Multiple cards with stagger -->
<div class="grid grid-cols-3 gap-4">
  <div class="card-enter card-enter-1">Card 1</div>
  <div class="card-enter card-enter-2">Card 2</div>
  <div class="card-enter card-enter-3">Card 3</div>
</div>
```

**JavaScript:**
```javascript
const cards = document.querySelectorAll('[data-card]');
AnimationSystem.animateCardEntrance(cards, true); // with stagger
```

**CSS Classes for Stagger:**
- `.card-enter-1` through `.card-enter-6` - Staggered with 50ms delays

---

## 5. MODAL / POPUP ANIMATIONS

### Slide Up Modal

**HTML:**
```html
<!-- Backdrop -->
<div class="modal-backdrop fixed inset-0 bg-black/50 hidden"
     id="modalBackdrop"></div>

<!-- Modal -->
<div class="modal fixed top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 
            bg-white rounded-xl shadow-2xl hidden" id="modal">
  <div class="p-6">
    <h2 class="text-xl font-bold mb-4">Modal Title</h2>
    <p>Modal content here</p>
  </div>
</div>
```

**JavaScript:**
```javascript
const modal = document.getElementById('modal');
const backdrop = document.getElementById('modalBackdrop');

// Show
AnimationSystem.showModal(modal, backdrop);

// Hide
AnimationSystem.hideModal(modal, backdrop);
```

### Bottom Sheet Modal (Mobile)

**HTML:**
```html
<div class="modal fixed bottom-0 left-0 right-0 bg-white rounded-t-2xl shadow-2xl"
     id="bottomSheet">
  <!-- Content -->
</div>
```

---

## 6. DROPDOWN & COLLAPSE

### Dropdown Menu

**HTML:**
```html
<div class="dropdown-smooth max-h-0" id="dropdown" style="display: none;">
  <a href="#" class="block px-4 py-2 hover:bg-slate-100">Option 1</a>
  <a href="#" class="block px-4 py-2 hover:bg-slate-100">Option 2</a>
  <a href="#" class="block px-4 py-2 hover:bg-slate-100">Option 3</a>
</div>
```

**JavaScript:**
```javascript
const dropdown = document.getElementById('dropdown');

// Toggle
AnimationSystem.toggleDropdown(dropdown);

// Open
AnimationSystem.openDropdown(dropdown);

// Close
AnimationSystem.closeDropdown(dropdown);
```

---

## 7. TOAST NOTIFICATIONS

### Show Success Toast

```javascript
AnimationSystem.showToast('Changes saved successfully!', 'success', 3000);
```

### Show Error Toast

```javascript
AnimationSystem.showToast('Error: Failed to save changes', 'error', 5000);
```

### Show Info Toast

```javascript
AnimationSystem.showToast('Please log in to continue', 'info', 4000);
```

### Show Warning Toast

```javascript
AnimationSystem.showToast('This action cannot be undone', 'warning', 4000);
```

**HTML Structure (auto-created):**
```html
<div class="toast-container fixed bottom-4 right-4 space-y-3 z-50">
  <div class="toast toast-success toast-enter">
    <div class="flex items-center gap-3">
      <span class="material-symbols-outlined">check_circle</span>
      <span>Success message</span>
    </div>
  </div>
</div>
```

---

## 8. LIST & GRID ANIMATIONS

### Animated List Items

**HTML:**
```html
<ul class="space-y-2">
  <li class="list-item-enter list-item-enter-1">Item 1</li>
  <li class="list-item-enter list-item-enter-2">Item 2</li>
  <li class="list-item-enter list-item-enter-3">Item 3</li>
  <li class="list-item-enter list-item-enter-4">Item 4</li>
  <li class="list-item-enter list-item-enter-5">Item 5</li>
</ul>
```

**JavaScript:**
```javascript
const items = document.querySelectorAll('li');
AnimationSystem.animateListItems(items);
```

### Remove Item with Animation

```javascript
const item = document.querySelector('li');
AnimationSystem.removeListItemAnimated(item);
```

---

## 9. INPUT VALIDATION

### Shake on Error

```javascript
const input = document.querySelector('input');
AnimationSystem.shakeInput(input);
```

### Focus Effect

```javascript
const input = document.querySelector('input');
AnimationSystem.addInputFocusEffect(input);
```

---

## 10. SIDEBAR NAVIGATION

### Toggle Sidebar

**HTML:**
```html
<aside class="sidebar fixed left-0 top-0 h-full w-64 bg-slate-900 text-white"
       id="sidebar">
  <!-- Sidebar content -->
</aside>
```

**JavaScript:**
```javascript
const sidebar = document.getElementById('sidebar');

// Open
AnimationSystem.toggleSidebar(sidebar, true);

// Close
AnimationSystem.toggleSidebar(sidebar, false);
```

---

## 11. UTILITY CLASSES

### Quick Transition Classes

```html
<!-- Fast transition (150ms) -->
<div class="transition-fast hover:opacity-80">Hover me</div>

<!-- Normal transition (300ms) -->
<div class="transition-normal hover:scale-105">Hover me</div>

<!-- Slow transition (500ms) -->
<div class="transition-slow hover:translate-y-2">Hover me</div>

<!-- Specific transitions -->
<div class="transition-transform">Transform only</div>
<div class="transition-opacity">Opacity only</div>
<div class="transition-colors">Colors only</div>
<div class="transition-shadow">Shadow only</div>
```

---

## 12. EXAMPLES BY USER ROLE

### Admin Dashboard

```html
<!-- Page loading -->
<script>
async function loadDashboard() {
  AnimationSystem.showLoading();
  
  // Fetch data...
  await fetch('/api/admin/dashboard');
  
  AnimationSystem.hideLoading();
}
</script>

<!-- Cards entrance -->
<div class="grid grid-cols-4 gap-6">
  <div class="card-enter-1 bg-white rounded-xl p-6">
    <h3>Total Users</h3>
    <p class="text-3xl font-bold">1,234</p>
  </div>
  <div class="card-enter-2 bg-white rounded-xl p-6">
    <h3>Active Clubs</h3>
    <p class="text-3xl font-bold">42</p>
  </div>
  <!-- More cards... -->
</div>

<!-- Button with ripple -->
<button class="btn-ripple px-6 py-2 bg-primary text-white rounded-lg">
  Export Report
</button>
```

### Club Admin My Club Page

```html
<!-- Club data loading -->
<script>
async function loadClub() {
  AnimationSystem.showLoading();
  const res = await fetch(`/api/clubs/${clubId}`);
  const club = await res.json();
  
  // Render club info
  renderClub(club);
  AnimationSystem.hideLoading();
  
  // Animate cards
  const cards = document.querySelectorAll('[data-card]');
  AnimationSystem.animateCardEntrance(cards);
}
</script>

<!-- Animated member list -->
<ul class="space-y-2">
  <li class="list-item-enter list-item-enter-1">Member 1</li>
  <li class="list-item-enter list-item-enter-2">Member 2</li>
  <!-- More items... -->
</ul>

<!-- Modal for new event -->
<div id="eventModal" class="hidden" style="display: none;">
  <!-- Form content -->
</div>

<button onclick="AnimationSystem.showModal(eventModal, backdrop)">
  New Event
</button>
```

### Student Event Registration

```html
<!-- Event cards with hover -->
<div class="grid grid-cols-3 gap-6">
  <div class="card-hover bg-white rounded-xl overflow-hidden cursor-pointer">
    <img src="event.jpg" class="w-full h-40 object-cover" />
    <div class="p-4">
      <h3 class="font-bold">Event Name</h3>
      <button class="btn-ripple w-full mt-4 px-4 py-2 bg-primary text-white rounded-lg">
        Register
      </button>
    </div>
  </div>
</div>

<!-- Success toast after registration -->
<script>
async function registerEvent() {
  AnimationSystem.showLoading();
  const res = await fetch('/api/events/register', { method: 'POST' });
  AnimationSystem.hideLoading();
  
  if (res.ok) {
    AnimationSystem.showToast('Registered successfully!', 'success', 3000);
  }
}
</script>
```

---

## 13. PERFORMANCE TIPS

### 1. Use GPU Acceleration
```html
<div class="gpu-accelerate card-hover">Card</div>
```

### 2. Use will-change Wisely
```html
<div class="animate-page">Animating page</div>
<div class="animate-modal">Animating modal</div>
<div class="animate-card">Animating card</div>
```

### 3. Reduce Motion Support
```javascript
if (AnimationSystem.animationsEnabled()) {
  // Use animations
} else {
  // Skip or simplify animations
}
```

### 4. Debounce Animations
```javascript
let animationTimeout;
function onScroll() {
  clearTimeout(animationTimeout);
  animationTimeout = setTimeout(() => {
    // Animation logic
  }, 150);
}
```

---

## 14. ACCESSIBILITY

### Respects Prefers Reduced Motion

All animations automatically respect `prefers-reduced-motion: reduce`:

```css
@media (prefers-reduced-motion: reduce) {
  * {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}
```

Users with motion sensitivity won't experience animations.

---

## 15. MOBILE OPTIMIZATION

Animations automatically:
- Reduce duration on mobile (300ms → 200ms)
- Optimize for touch interactions
- Respect device performance settings

### Custom Mobile Adjustments

```html
<!-- Adapt animations for mobile -->
<div class="page-enter md:page-enter">Content</div>
```

---

## 16. TIMING REFERENCE

| Animation | Duration | Easing |
|-----------|----------|--------|
| Page Transition | 300-400ms | cubic-bezier(0.34, 1.56, 0.64, 1) |
| Modal | 250-300ms | cubic-bezier(0.34, 1.56, 0.64, 1) |
| Card Entrance | 400ms | cubic-bezier(0.34, 1.56, 0.64, 1) |
| Card Hover | 200ms | cubic-bezier(0.34, 1.56, 0.64, 1) |
| Button Click | 150-200ms | cubic-bezier(0.34, 1.56, 0.64, 1) |
| Dropdown | 200-250ms | cubic-bezier(0.34, 1.56, 0.64, 1) |
| Toast | 300ms | cubic-bezier(0.34, 1.56, 0.64, 1) |

---

## 17. TROUBLESHOOTING

### Animations Not Playing

1. Check if animations.css is loaded
2. Check browser's motion preferences
3. Check if `.page-enter` class is applied
4. Verify `AnimationSystem.js` is loaded

### Performance Issues

1. Use `.gpu-accelerate` class
2. Reduce number of simultaneous animations
3. Use `will-change` property
4. Profile with DevTools Performance tab

### Flash of Unstyled Content

```html
<!-- Hide initially -->
<div class="opacity-0" id="content">Content</div>

<!-- Fade in -->
<script>
AnimationSystem.pageTransition(null, document.getElementById('content'));
</script>
```

---

## 18. BROWSER SUPPORT

| Browser | Support |
|---------|---------|
| Chrome/Edge 88+ | ✅ Full |
| Firefox 78+ | ✅ Full |
| Safari 14+ | ✅ Full |
| Mobile Safari 14+ | ✅ Full |
| Chrome Mobile | ✅ Full |

CSS transforms, opacity, and transitions are broadly supported.

---

## Need Help?

For detailed examples, check:
- `admin-dashboard.html` - Admin animations
- `clubadmin-myclub.html` - Club admin animations
- `student-event.html` - Student role animations

All pages include animation examples with comments.
