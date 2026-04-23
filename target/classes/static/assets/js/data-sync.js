/* data-sync.js - Ensures user role and status are synchronized with the server */
(function() {
    async function syncUserData() {
        const user = JSON.parse(localStorage.getItem('user'));
        if (!user || !user.id || user.isDemo) return;

        // Skip sync if we're on the login or signup page
        const path = window.location.pathname;
        if (path.includes('login.html') || path.includes('signup.html')) return;

        try {
            const resp = await fetch(`/api/users/${user.id}?t=${Date.now()}`);
            
            if (resp.status === 404 || resp.status === 401) {
                // User deleted or unauthorized
                console.log("User account not found or unauthorized. Logging out.");
                localStorage.removeItem('user');
                window.location.href = 'login.html';
                return;
            }

            if (resp.ok) {
                const updatedUser = await resp.json();
                
                // If user is set to inactive, log them out
                if (!updatedUser.active) {
                   console.log("User account is inactive. Logging out.");
                   localStorage.removeItem('user');
                   window.location.href = 'login.html?reason=inactive';
                   return;
                }

                // If role has changed, update and redirect
                if (updatedUser.role !== user.role) {
                    console.log(`Role changed from ${user.role} to ${updatedUser.role}. Synchronizing...`);
                    localStorage.setItem('user', JSON.stringify(updatedUser));
                    
                    // Route to correct dashboard based on new role
                    if (updatedUser.role === 'ADMIN') window.location.href = 'admin-dashboard.html';
                    else if (updatedUser.role === 'CLUB_ADMIN') window.location.href = 'clubadmin-dashboard.html';
                    else window.location.href = 'student-dashboard.html';
                }
            }
        } catch (e) {
            console.warn("User sync failed. Check server connection.", e);
        }
    }

    // Run on load
    document.addEventListener('DOMContentLoaded', () => {
        syncUserData();
        // Check every 30 seconds for role changes or account status
        setInterval(syncUserData, 30000);
    });
})();
