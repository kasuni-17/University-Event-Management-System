# SLIIT EventHub Landing Page

## 📋 Overview

A modern, production-ready landing page for SLIIT EventHub built with **Vanilla JavaScript**, **Tailwind CSS**, and **CSS animations**. No framework dependencies - just clean, performant code.

## 📁 File Structure

```
src/main/resources/static/
├── landing-page.html          # Main HTML file (no navbar)
├── css/
│   └── landing.css            # Styles & animations
└── js/
    ├── animations.js          # Scroll animations & counters
    └── interactions.js        # Button clicks & interactions
```

## 🚀 Quick Start

### 1. Access the Landing Page
Open your browser and navigate to:
```
http://localhost:8080/landing-page.html
```

### 2. Integrate into Spring Boot
Add a controller route to serve the landing page:

```java
@Controller
public class LandingPageController {
    
    @GetMapping("/")
    public String landing() {
        return "forward:/landing-page.html";
    }
    
    @GetMapping("/home")
    public String home() {
        return "forward:/landing-page.html";
    }
}
```

### 3. Update Navigation Links
All buttons and links in the landing page use placeholder routes. Update them in `interactions.js`:

```javascript
// Change these routes to match your application
exploreBtn.addEventListener('click', () => {
    window.location.href = '/events';  // Your events page
});

getStartedBtn.addEventListener('click', () => {
    window.location.href = '/register';  // Your registration page
});
```

## 🎨 Theme Configuration

The page uses your existing SLIIT EventHub theme defined in Tailwind config:

```javascript
colors: {
    "primary": "#003366",      // Navy Blue
    "accent": "#4DA6FF",       // Sky Blue
    "background-light": "#f5f7f8",   // Light background
    "background-dark": "#0f1923",    // Dark background
}
```

All colors are centralized in the HTML `<script id="tailwind-config">` tag. To change colors, modify only this section.

## ✨ Features

### Sections Included
- ✅ **Hero Section** - Main call-to-action with image
- ✅ **Stats Section** - Key metrics with animated counters
- ✅ **Features Section** - 4 main platform features
- ✅ **Upcoming Events** - Event cards with images
- ✅ **CTA Section** - Call-to-action for organizers
- ✅ **Footer** - Links, newsletter signup, social

### Animations & Interactions
- **Scroll Animations** - Sections fade in as they come into view
- **Counter Animations** - Numbers count up when stats section is visible
- **Hover Effects** - Cards lift, buttons glow, images zoom
- **Button Ripple** - Modern ripple effect on button hover
- **Smooth Transitions** - All interactions are smooth and polished
- **Staggered Items** - Grid items animate in sequence

## 🔧 Customization Guide

### Change Content
Edit text directly in `landing-page.html`:

```html
<h1 class="hero-title text-5xl lg:text-7xl font-black">
    Your new heading here
</h1>
```

### Change Event Cards
Update the event card HTML with your actual events:

```html
<div class="event-card ...">
    <h5>Your Event Name</h5>
    <p>Oct 15, 2024 • 09:00 AM</p>
    <!-- Update other details -->
</div>
```

### Add New Sections
1. Add HTML section in `landing-page.html`
2. Add CSS in `landing.css` with animation classes
3. Observer automatically picks it up in `animations.js`

### Modify Animations
Edit the keyframes in `landing.css`:

```css
@keyframes slideUp {
    0%: { opacity: '0', transform: 'translateY(30px)' }
    100%: { opacity: '1', transform: 'translateY(0)' }
}
```

### Change Button Routes
Edit button click handlers in `interactions.js`:

```javascript
exploreBtn.addEventListener('click', () => {
    window.location.href = '/your-route-here';
});
```

## 📱 Responsive Design

The page is fully responsive using Tailwind's breakpoints:
- **Mobile**: Base styles (< 768px)
- **Tablet**: `md:` prefix (768px - 1024px)
- **Desktop**: `lg:` prefix (> 1024px)

Test responsiveness:
```bash
# Open DevTools (F12) and toggle device toolbar
# Test on mobile (375px), tablet (768px), desktop (1920px)
```

## 🎯 Performance Optimizations

- ✅ **No External Dependencies** - Only Tailwind CDN & Google Fonts
- ✅ **Lazy Loading** - Intersection Observer for efficient animations
- ✅ **CSS Classes Only** - No inline styles blocking renders
- ✅ **Minimal JavaScript** - Pure JS, no frameworks
- ✅ **RequestAnimationFrame** - Smooth 60fps animations
- ✅ **Event Delegation** - Efficient event handling

## 🔌 Backend Integration

### Newsletter Signup
Update the form submission in `interactions.js`:

```javascript
newsletterForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = newsletterForm.querySelector('input[type="email"]').value;
    
    // Call your backend API
    const response = await fetch('/api/newsletter/subscribe', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email })
    });
    
    if (response.ok) {
        this.showNotification('Subscribed successfully!', 'success');
    }
});
```

### Event Registration
Update event card click handler:

```javascript
card.addEventListener('click', async () => {
    const eventId = card.dataset.eventId; // Add data attribute in HTML
    const response = await fetch(`/api/events/${eventId}/register`, {
        method: 'POST'
    });
    
    if (response.ok) {
        this.showNotification('Registered successfully!', 'success');
    }
});
```

## 🌙 Dark Mode Support

The page includes full dark mode support using Tailwind's `dark:` prefix:

```html
<body class="bg-background-light dark:bg-background-dark">
    <!-- Content automatically adapts to dark mode -->
</body>
```

Users can toggle dark mode via system preferences or manually:

```javascript
// Toggle dark mode
document.documentElement.classList.toggle('dark');
```

## ♿ Accessibility

- ✅ Semantic HTML structure
- ✅ Proper heading hierarchy
- ✅ Alt text for images (via data-alt attributes)
- ✅ ARIA labels for interactive elements
- ✅ Keyboard navigation support
- ✅ Reduced motion support (prefers-reduced-motion)

## 📊 Browser Support

- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

The code uses modern CSS and JavaScript features. For older browser support, add polyfills.

## 🐛 Troubleshooting

### Animations not showing?
- Check browser console for errors
- Ensure CSS and JS files are loaded (F12 > Network)
- Clear browser cache (Ctrl+Shift+Delete)

### Buttons not working?
- Update routes in `interactions.js` to match your application
- Check console for errors
- Verify backend routes exist

### Styles look broken?
- Ensure Tailwind CDN is loaded
- Check for CSS conflicts with existing stylesheets
- Verify custom colors in Tailwind config

### Dark mode not working?
- Check if `dark` class is on `<html>` element
- Verify dark mode is enabled in Tailwind config: `darkMode: "class"`

## 📝 License

This landing page is part of the SLIIT EventHub project. All rights reserved.

## 🤝 Support

For issues or questions:
1. Check the troubleshooting section above
2. Review the code comments in each file
3. Test in browser console (F12)
4. Check network requests in DevTools

---

**Last Updated**: April 2026
**Version**: 1.0.0
**Built for**: SLIIT EventHub Web Application
