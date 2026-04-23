# Frontend Integration Guide - Admin Workflow

This guide explains how to integrate the JavaScript files with the backend API endpoints to create a fully functional admin dashboard.

## File Structure

```
static/
├── admin-dashboard.html
├── admin-users.html
├── admin-events.html
├── admin-clubs.html
├── admin-venues.html
├── admin-feedback.html
├── assets/js/
│   ├── auth.js          (Authentication & user context)
│   ├── admin-api.js     (NEW - Admin API calls)
│   ├── utils.js         (Utility functions)
│   └── components.js    (Reusable UI components)
```

## Core API Module - `admin-api.js`

Create this new file to handle all admin API calls:

```javascript
// static/assets/js/admin-api.js

const API_BASE = 'http://localhost:8080/api';

// ============ USER MANAGEMENT ============

async function getAllUsers() {
  return fetch(`${API_BASE}/users`).then(r => r.json());
}

async function getUsersByRole(role) {
  return fetch(`${API_BASE}/users/admin/role/${role}`).then(r => r.json());
}

async function getPendingUsers(role = null) {
  const url = role ? 
    `${API_BASE}/users/admin/pending/role/${role}` :
    `${API_BASE}/users/admin/pending`;
  return fetch(url).then(r => r.json());
}

async function searchUsers(name, role = null) {
  const url = role?
    `${API_BASE}/users/admin/search/role/${role}?name=${name}` :
    `${API_BASE}/users/admin/search?name=${name}`;
  return fetch(url).then(r => r.json());
}

async function approveUser(userId) {
  return fetch(`${API_BASE}/users/${userId}/approve`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'}
  }).then(r => r.json());
}

async function rejectUser(userId) {
  return fetch(`${API_BASE}/users/${userId}/reject`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'}
  }).then(r => r.json());
}

async function updateUser(userId, userData) {
  return fetch(`${API_BASE}/users/${userId}`, {
    method: 'PUT',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(userData)
  }).then(r => r.json());
}

// ============ EVENT MANAGEMENT ============

async function getPendingEvents() {
  return fetch(`${API_BASE}/events/admin/pending`).then(r => r.json());
}

async function getApprovedEvents() {
  return fetch(`${API_BASE}/events/admin/approved`).then(r => r.json());
}

async function getUpcomingEvents() {
  return fetch(`${API_BASE}/events/admin/upcoming`).then(r => r.json());
}

async function getPastEvents() {
  return fetch(`${API_BASE}/events/admin/past`).then(r => r.json());
}

async function getCompletedEvents() {
  return fetch(`${API_BASE}/events/admin/completed`).then(r => r.json());
}

async function getEventById(eventId) {
  return fetch(`${API_BASE}/events/${eventId}`).then(r => r.json());
}

async function approveEvent(eventId) {
  return fetch(`${API_BASE}/events/${eventId}/approve`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'}
  }).then(r => r.json());
}

async function rejectEvent(eventId) {
  return fetch(`${API_BASE}/events/${eventId}/reject`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'}
  }).then(r => r.json());
}

async function updateEvent(eventId, eventData) {
  return fetch(`${API_BASE}/events/${eventId}`, {
    method: 'PUT',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(eventData)
  }).then(r => r.json());
}

async function deleteEvent(eventId) {
  return fetch(`${API_BASE}/events/${eventId}`, {
    method: 'DELETE',
    headers: {'Content-Type': 'application/json'}
  });
}

async function completeEvent(eventId) {
  return fetch(`${API_BASE}/events/${eventId}/complete`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'}
  }).then(r => r.json());
}

// ============ CLUB MANAGEMENT ============

async function getPendingClubs() {
  return fetch(`${API_BASE}/clubs/admin/pending`).then(r => r.json());
}

async function getApprovedClubs() {
  return fetch(`${API_BASE}/clubs/admin/approved`).then(r => r.json());
}

async function getClubById(clubId) {
  return fetch(`${API_BASE}/clubs/${clubId}`).then(r => r.json());
}

async function approveClub(clubId) {
  return fetch(`${API_BASE}/clubs/${clubId}/approve`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'}
  }).then(r => r.json());
}

async function rejectClub(clubId) {
  return fetch(`${API_BASE}/clubs/${clubId}/reject`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'}
  }).then(r => r.json());
}

async function updateClub(clubId, clubData) {
  return fetch(`${API_BASE}/clubs/${clubId}`, {
    method: 'PUT',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(clubData)
  }).then(r => r.json());
}

async function deleteClub(clubId) {
  return fetch(`${API_BASE}/clubs/${clubId}`, {
    method: 'DELETE',
    headers: {'Content-Type': 'application/json'}
  });
}

// ============ VENUE MANAGEMENT ============

async function getAllVenues() {
  return fetch(`${API_BASE}/venues`).then(r => r.json());
}

async function getVenuesByType(type) {
  return fetch(`${API_BASE}/venues/admin/type/${type}`).then(r => r.json());
}

async function searchVenues(name) {
  return fetch(`${API_BASE}/venues/admin/search?name=${name}`).then(r => r.json());
}

async function getVenuesByCapacity(minCapacity) {
  return fetch(`${API_BASE}/venues/admin/capacity?minCapacity=${minCapacity}`).then(r => r.json());
}

async function createVenue(venueData) {
  return fetch(`${API_BASE}/venues`, {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(venueData)
  }).then(r => r.json());
}

async function updateVenue(venueId, venueData) {
  return fetch(`${API_BASE}/venues/${venueId}`, {
    method: 'PUT',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(venueData)
  }).then(r => r.json());
}

async function deleteVenue(venueId) {
  return fetch(`${API_BASE}/venues/${venueId}`, {
    method: 'DELETE',
    headers: {'Content-Type': 'application/json'}
  });
}

async function checkVenueAvailability(venueId, startDate, endDate) {
  return fetch(`${API_BASE}/venues/${venueId}/availability?startDate=${startDate}&endDate=${endDate}`)
    .then(r => r.json());
}

// ============ FEEDBACK MANAGEMENT ============

async function getFeedbackByEvent(eventId) {
  return fetch(`${API_BASE}/feedback/event/${eventId}`).then(r => r.json());
}

async function getEventAnalytics(eventId) {
  return fetch(`${API_BASE}/feedback/admin/event/${eventId}/analytics`).then(r => r.json());
}

async function getAverageRating(eventId) {
  return fetch(`${API_BASE}/feedback/admin/event/${eventId}/average-rating`).then(r => r.json());
}

async function getFeedbackCount(eventId) {
  return fetch(`${API_BASE}/feedback/admin/event/${eventId}/count`).then(r => r.json());
}

async function getCompletedEventsFeedback() {
  return fetch(`${API_BASE}/feedback/admin/completed-events`).then(r => r.json());
}
```

## Integration Examples

### Example 1: Admin Users Page

```javascript
// admin-users.html - JavaScript integration

// Load pending users on page load
document.addEventListener('DOMContentLoaded', async () => {
  await loadPendingUsers();
});

async function loadPendingUsers() {
  try {
    const pendingUsers = await getPendingUsers();
    displayUserTable(pendingUsers);
  } catch (error) {
    console.error('Error loading pending users:', error);
    showAlert('Error loading users', 'danger');
  }
}

// Filter users by role
async function filterByRole(role) {
  try {
    let users;
    if (role === 'pending') {
      users = await getPendingUsers();
    } else {
      users = await getUsersByRole(role);
    }
    displayUserTable(users);
  } catch (error) {
    showAlert('Error filtering users', 'danger');
  }
}

// Search users
async function searchUser() {
  const searchInput = document.getElementById('search-users-input');
  const searchTerm = searchInput.value.trim();
  
  if (!searchTerm) {
    await loadPendingUsers();
    return;
  }
  
  try {
    const users = await searchUsers(searchTerm);
    displayUserTable(users);
  } catch (error) {
    showAlert('Error searching users', 'danger');
  }
}

// Display users in table
function displayUserTable(users) {
  const tableBody = document.querySelector('#users-table tbody');
  tableBody.innerHTML = '';
  
  users.forEach(user => {
    const row = document.createElement('tr');
    row.innerHTML = `
      <td class="flex items-center gap-3">
        <img src="https://via.placeholder.com/40" class="w-10 h-10 rounded-full">
        <div>
          <p class="font-medium">${user.name}</p>
          <p class="text-sm text-gray-500">${user.email}</p>
        </div>
      </td>
      <td><a href="#" class="text-blue-600 hover:underline">${getRoleName(user.role)}</a></td>
      <td><span class="px-3 py-1 rounded-full text-sm font-medium ${getStatusColor(user.approvalStatus)}">${user.approvalStatus}</span></td>
      <td class="flex gap-2">
        ${user.approvalStatus === 'PENDING' ? `
          <button onclick="approveUserAction(${user.id})" class="px-3 py-1 bg-green-500 text-white rounded-lg hover:bg-green-600">Approve</button>
          <button onclick="rejectUserAction(${user.id})" class="px-3 py-1 bg-red-500 text-white rounded-lg hover:bg-red-600">Reject</button>
        ` : ''}
        <button onclick="deleteUserAction(${user.id})" class="px-3 py-1 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400">Delete</button>
      </td>
    `;
    tableBody.appendChild(row);
  });
}

// Approve user
async function approveUserAction(userId) {
  if (!confirm('Are you sure you want to approve this user?')) return;
  
  try {
    await approveUser(userId);
    showAlert('User approved successfully', 'success');
    await loadPendingUsers();
  } catch (error) {
    showAlert('Error approving user', 'danger');
  }
}

// Reject user
async function rejectUserAction(userId) {
  if (!confirm('Are you sure you want to reject this user?')) return;
  
  try {
    await rejectUser(userId);
    showAlert('User rejected', 'success');
    await loadPendingUsers();
  } catch (error) {
    showAlert('Error rejecting user', 'danger');
  }
}

function getRoleName(role) {
  const roles = {
    'STUDENT': 'Student',
    'CLUB_ADMIN': 'Club Admin',
    'DEPT_ADMIN': 'Dept Admin',
    'SUPER_ADMIN': 'Super Admin'
  };
  return roles[role] || role;
}

function getStatusColor(status) {
  const colors = {
    'PENDING': 'bg-yellow-100 text-yellow-800',
    'APPROVED': 'bg-green-100 text-green-800',
    'REJECTED': 'bg-red-100 text-red-800'
  };
  return colors[status] || 'bg-gray-100 text-gray-800';
}
```

### Example 2: Admin Events Page

```javascript
// admin-events.html - JavaScript integration

let currentEventFilter = 'pending';

document.addEventListener('DOMContentLoaded', async () => {
  await loadEventsByFilter('pending');
});

async function loadEventsByFilter(filter) {
  currentEventFilter = filter;
  try {
    let events = [];
    
    if (filter === 'pending') events = await getPendingEvents();
    else if (filter === 'approved') events = await getApprovedEvents();
    else if (filter === 'upcoming') events = await getUpcomingEvents();
    else if (filter === 'past') events = await getPastEvents();
    
    displayEvents(events, filter);
    
    // Update active tab
    document.querySelectorAll('[data-filter]').forEach(btn => {
      btn.classList.remove('border-b-2', 'border-blue-500', 'text-blue-600');
      if (btn.dataset.filter === filter) {
        btn.classList.add('border-b-2', 'border-blue-500', 'text-blue-600');
      }
    });
  } catch (error) {
    console.error('Error loading events:', error);
    showAlert('Error loading events', 'danger');
  }
}

function displayEvents(events, filter) {
  const container = document.getElementById('events-container');
  container.innerHTML = '';
  
  if (events.length === 0) {
    container.innerHTML = '<p class="text-center text-gray-500 py-8">No events found</p>';
    return;
  }
  
  events.forEach(event => {
    const eventCard = document.createElement('div');
    eventCard.className = 'bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow';
    
    let actionButtons = '';
    
    if (filter === 'pending') {
      actionButtons = `
        <button onclick="approveEventAction(${event.id})" class="px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600">Approve</button>
        <button onclick="rejectEventAction(${event.id})" class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600">Reject</button>
        <button onclick="viewEventDetails(${event.id})" class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">View Details</button>
      `;
    } else if (filter === 'upcoming' || filter === 'approved') {
      actionButtons = `
        <button onclick="editEventAction(${event.id})" class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">Edit</button>
        <button onclick="deleteEventAction(${event.id})" class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600">Delete</button>
      `;
    } else if (filter === 'past') {
      actionButtons = `
        <button onclick="viewEventDetails(${event.id})" class="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">View Details</button>
        <button onclick="viewEventFeedback(${event.id})" class="px-4 py-2 bg-purple-500 text-white rounded hover:bg-purple-600">View Feedback</button>
        <button onclick="deleteEventAction(${event.id})" class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600">Remove</button>
      `;
    }
    
    eventCard.innerHTML = `
      <div class="p-4">
        <h3 class="text-lg font-bold text-gray-900">${event.title}</h3>
        <p class="text-sm text-gray-600 mt-1">${event.description}</p>
        <p class="text-sm text-gray-500 mt-2">
          📅 ${formatDate(event.startTime)} | 
          📍 ${event.location}
        </p>
        <p class="text-sm text-gray-500">
          👥 ${event.organizer?.name || 'N/A'} | 
          Capacity: ${event.maxParticipants}
        </p>
        <div class="flex gap-2 mt-4">
          ${actionButtons}
        </div>
      </div>
    `;
    
    container.appendChild(eventCard);
  });
}

async function approveEventAction(eventId) {
  if (!confirm('Approve this event?')) return;
  try {
    await approveEvent(eventId);
    showAlert('Event approved!', 'success');
    await loadEventsByFilter(currentEventFilter);
  } catch (error) {
    showAlert('Error approving event', 'danger');
  }
}

async function rejectEventAction(eventId) {
  if (!confirm('Reject this event?')) return;
  try {
    await rejectEvent(eventId);
    showAlert('Event rejected', 'success');
    await loadEventsByFilter(currentEventFilter);
  } catch (error) {
    showAlert('Error rejecting event', 'danger');
  }
}

async function deleteEventAction(eventId) {
  if (!confirm('Are you sure you want to delete this event? This action cannot be undone.')) return;
  try {
    await deleteEvent(eventId);
    showAlert('Event deleted', 'success');
    await loadEventsByFilter(currentEventFilter);
  } catch (error) {
    showAlert('Error deleting event', 'danger');
  }
}

async function editEventAction(eventId) {
  // Show modal to edit event
  const event = await getEventById(eventId);
  // Populate form with event data and show edit modal
  showEditEventModal(event);
}

async function viewEventDetails(eventId) {
  const event = await getEventById(eventId);
  showEventDetailsModal(event);
}

async function viewEventFeedback(eventId) {
  window.location.href = `feedback.html?eventId=${eventId}`;
}

function formatDate(dateString) {
  return new Date(dateString).toLocaleDateString();
}
```

### Example 3: Admin Feedback Page

```javascript
// admin-feedback.html - JavaScript integration

document.addEventListener('DOMContentLoaded', async () => {
  await loadCompletedEvents();
});

async function loadCompletedEvents() {
  try {
    const feedbacks = await getCompletedEventsFeedback();
    displayFeedbackEvents(feedbacks);
  } catch (error) {
    console.error('Error loading completed events:', error);
    showAlert('Error loading events', 'danger');
  }
}

function displayFeedbackEvents(feedbacks) {
  const container = document.getElementById('feedback-events-container');
  container.innerHTML = '';
  
  // Group feedbacks by event
  const eventMap = {};
  feedbacks.forEach(feedback => {
    const eventId = feedback.event.id;
    if (!eventMap[eventId]) {
      eventMap[eventId] = {
        event: feedback.event,
        feedbacks: []
      };
    }
    eventMap[eventId].feedbacks.push(feedback);
  });
  
  // Display events
  Object.values(eventMap).forEach(({ event, feedbacks }) => {
    const avgRating = (feedbacks.reduce((sum, f) => sum + f.rating, 0) / feedbacks.length).toFixed(1);
    
    const eventCard = document.createElement('div');
    eventCard.className = 'bg-white rounded-lg shadow-md p-4 cursor-pointer hover:shadow-lg transition-shadow';
    eventCard.innerHTML = `
      <h3 class="text-lg font-bold text-gray-900">${event.title}</h3>
      <p class="text-sm text-gray-600">${feedbacks.length} Feedbacks</p>
      <p class="text-sm text-gray-600">⭐ ${avgRating}</p>
      <button onclick="viewEventFeedbackAnalytics(${event.id})" class="mt-2 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">View Analytics</button>
    `;
    
    container.appendChild(eventCard);
  });
}

async function viewEventFeedbackAnalytics(eventId) {
  try {
    const analytics = await getEventAnalytics(eventId);
    showFeedbackAnalytics(analytics);
  } catch (error) {
    showAlert('Error loading analytics', 'danger');
  }
}

function showFeedbackAnalytics(analytics) {
  // Display analytics modal with charts and feedback list
  const modal = document.getElementById('analytics-modal');
  
  const content = `
    <div class="space-y-6">
      <div class="grid grid-cols-3 gap-4">
        <div class="bg-blue-50 p-4 rounded">
          <p class="text-gray-600">Total Feedbacks</p>
          <h3 class="text-2xl font-bold">${analytics.totalFeedback}</h3>
        </div>
        <div class="bg-green-50 p-4 rounded">
          <p class="text-gray-600">Average Rating</p>
          <h3 class="text-2xl font-bold">⭐ ${analytics.averageRating.toFixed(1)}</h3>
        </div>
      </div>
      
      <div>
        <h4 class="font-bold mb-2">Rating Distribution</h4>
        <div class="space-y-2">
          ${Object.entries(analytics.ratingDistribution).map(([rating, count]) => `
            <div class="flex items-center gap-2">
              <span>${rating}⭐</span>
              <div class="w-full bg-gray-200 rounded-full h-2">
                <div class="bg-blue-500 h-2 rounded-full" style="width: ${(count/analytics.totalFeedback)*100}%"></div>
              </div>
              <span>${count}</span>
            </div>
          `).join('')}
        </div>
      </div>
      
      <div>
        <h4 class="font-bold mb-2">Comments</h4>
        <div class="space-y-3 max-h-64 overflow-y-auto">
          ${analytics.feedbacks.map(f => `
            <div class="bg-gray-50 p-3 rounded">
              <p class="font-medium">${f.student?.name || 'Anonymous'} - ${f.rating}⭐</p>
              <p class="text-sm text-gray-700">${f.comment}</p>
            </div>
          `).join('')}
        </div>
      </div>
      
      <button onclick="downloadFeedbackCSV(${analytics.eventId})" class="w-full px-4 py-2 bg-green-500 text-white rounded hover:bg-green-600">
        Download as CSV
      </button>
    </div>
  `;
  
  modal.innerHTML = content;
  modal.classList.remove('hidden');
}

function downloadFeedbackCSV(eventId) {
  // Implement CSV download logic
  getEventAnalytics(eventId).then(analytics => {
    let csv = 'Student Name,Rating,Comment,Date\n';
    analytics.feedbacks.forEach(f => {
      csv += `"${f.student?.name || 'Anonymous'}",${f.rating},"${f.comment.replace(/"/g, '""')}","${formatDate(f.submittedAt)}"\n`;
    });
    
    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `feedback_event_${eventId}.csv`;
    a.click();
  });
}
```

## Utility Functions

Add these to your `utils.js`:

```javascript
// static/assets/js/utils.js

function showAlert(message, type = 'info') {
  // Show alert/toast notification
  const alert = document.createElement('div');
  alert.className = `fixed top-4 right-4 px-6 py-3 rounded-lg text-white ${
    type === 'success' ? 'bg-green-500' :
    type === 'danger' ? 'bg-red-500' :
    'bg-blue-500'
  }`;
  alert.textContent = message;
  document.body.appendChild(alert);
  
  setTimeout(() => alert.remove(), 3000);
}

function formatDate(dateString) {
  return new Date(dateString).toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

function formatTime(dateString) {
  return new Date(dateString).toLocaleTimeString('en-US', {
    hour: '2-digit',
    minute: '2-digit'
  });
}
```

## Implementation Checklist

- [ ] Create `admin-api.js` with all API functions
- [ ] Update `admin-dashboard.html` to load stats from API
- [ ] Update `admin-users.html` with JavaScript integration
- [ ] Update `admin-events.html` with JavaScript integration
- [ ] Update `admin-clubs.html` with JavaScript integration
- [ ] Update `admin-venues.html` with JavaScript integration
- [ ] Update `admin-feedback.html` with JavaScript integration
- [ ] Add error handling for network failures
- [ ] Add loading spinners for async operations
- [ ] Add confirmation dialogs for destructive actions
- [ ] Test all API endpoints
- [ ] Add form validation for create/update operations

