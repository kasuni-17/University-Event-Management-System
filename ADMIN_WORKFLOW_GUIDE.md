# Admin Workflow Guide - UniEvent Management System

## Overview
This document outlines the complete admin workflow for the UniEvent application. It maps frontend pages to backend API endpoints and explains the expected data flow.

---

## Table of Contents
1. [Admin Dashboard](#admin-dashboard)
2. [User Management](#user-management)
3. [Event Management](#event-management)
4. [Club Management](#club-management)
5. [Venue Management](#venue-management)
6. [Feedback Analytics](#feedback-analytics)

---

## Admin Dashboard

### Endpoint: `admin-dashboard.html`
**Purpose**: Display system overview and quick links to management pages

### Key Metrics Displayed:
- **Total Users**: `GET /api/users`
- **Active Events**: `GET /api/events/admin/approved`
- **Pending Approvals**: `GET /api/users/admin/pending` + `GET /api/clubs/admin/pending` + `GET /api/events/admin/pending`
- **Event Trends**: `GET /api/events` (filter and analyze by date)
- **Recent Activity**: System logs (implementation specific)

### Quick Links:
- "Manage Events" → `manage-events.html`
- "Manage Clubs" → `manage-clubs.html`
- "View Feedback" → `feedback.html`

---

## User Management

### Endpoint: `admin-users.html` (also accessible as `admin-dashboard.html?page=users`)

### Workflow:

#### 1. **View All Users**
```
GET /api/users
```
Response: Array of all users with fields: id, name, email, role, approvalStatus, active

#### 2. **Filter Users by Role**
- **Students**: `GET /api/users/admin/role/STUDENT`
- **Club Admins**: `GET /api/users/admin/role/CLUB_ADMIN`
- **Dept Admins**: `GET /api/users/admin/role/DEPT_ADMIN`
- **Super Admins**: `GET /api/users/admin/role/SUPER_ADMIN`

#### 3. **Get Pending Approvals**
- **All Pending**: `GET /api/users/admin/pending`
- **Pending Students**: `GET /api/users/admin/pending/role/STUDENT`
- **Pending Club Admins**: `GET /api/users/admin/pending/role/CLUB_ADMIN`

#### 4. **Search Users by Name**
```
GET /api/users/admin/search?name=John
```
Response: Array of matching users

#### 5. **Search Users by Name and Role**
```
GET /api/users/admin/search/role/STUDENT?name=Jane
```

#### 6. **Approve User Registration**
```
POST /api/users/{userId}/approve
```
- Changes `approvalStatus` from `PENDING` to `APPROVED`
- Sets `active` to `true`
- User can now log in

#### 7. **Reject User Registration**
```
POST /api/users/{userId}/reject
```
- Changes `approvalStatus` from `PENDING` to `REJECTED`
- Sets `active` to `false`
- User cannot log in

#### 8. **Update User (Admin Edit)**
```
PUT /api/users/{userId}
```
Body: User object with updated fields

---

## Event Management

### Endpoint: `admin-events.html`

### Workflow:

#### 1. **View All Events (Filter by Status)**

**Newly Published (Pending)**
```
GET /api/events/admin/pending
```
- Events awaiting admin approval
- Show in a table with: Event Title, Organizer Club, Date & Venue, Category

**Upcoming Events (Approved)**
```
GET /api/events/admin/approved
```
- Already approved events that haven't started
- Show with View, Edit, Delete buttons

**Past Events (Completed)**
```
GET /api/events/admin/completed
```
- Events that have finished
- Show with View Details, Remove, View Feedback buttons

#### 2. **Get Events by Time (Alternative Filter)**
```
GET /api/events/admin/upcoming   // Events with startTime > now
GET /api/events/admin/past       // Events with startTime < now
```

#### 3. **View Event Details**
```
GET /api/events/{eventId}
```
Response: Full event object with all details

#### 4. **Approve Event (from Pending)**
```
POST /api/events/{eventId}/approve
```
- Changes `status` from `PENDING` to `APPROVED`
- Event becomes visible to students
- Event scheduled on dashboard

#### 5. **Reject Event**
```
POST /api/events/{eventId}/reject
```
- Changes `status` to `CANCELLED`
- Event is removed from student views
- Organizer notified

#### 6. **Edit Event Details**
```
PUT /api/events/{eventId}
```
Body: Updated event object
- Can edit: title, description, startTime, endTime, venue, location, maxParticipants, etc.
- Only available for `APPROVED` and `PENDING` events

#### 7. **Delete/Remove Event**
```
DELETE /api/events/{eventId}
```
- Soft delete: Changes `status` to `CANCELLED`
- Event removed from all user views

#### 8. **Mark Event as Completed**
```
POST /api/events/{eventId}/complete
```
- Changes `status` to `COMPLETED`
- Event moved to Past Events list
- Feedback can now be viewed

---

## Club Management

### Endpoint: `admin-clubs.html`

### Workflow:

#### 1. **Get Pending Club Registrations**
```
GET /api/clubs/admin/pending
```
- Shows clubs awaiting approval
- Display in table: Club Name, Category, Requested By, Date, Status

#### 2. **View Pending Club Details**
```
GET /api/clubs/{clubId}
```
Response: Club object with: name, category, description, presidentEmail, email, etc.

#### 3. **Approve Club Registration**
```
POST /api/clubs/{clubId}/approve
```
- Changes `approvalStatus` from `PENDING` to `APPROVED`
- Sets `active` to `true`
- Club becomes visible to all students
- Club admin gets access to club management

#### 4. **Reject Club Registration**
```
POST /api/clubs/{clubId}/reject
```
- Changes `approvalStatus` from `PENDING` to `REJECTED`
- Sets `active` to `false`
- Club not shown to students

#### 5. **Get All Approved Clubs**
```
GET /api/clubs/admin/approved
```
- Display all active clubs in a grid/card layout
- Each card shows: Club name, category, logo, members count

#### 6. **Edit Club Details**
```
PUT /api/clubs/{clubId}
```
Body: Updated club object
- Can edit: name, category, description, presidentEmail, email, logoUrl, coverUrl

#### 7. **Delete Club**
```
DELETE /api/clubs/{clubId}
```
- Soft delete: Sets `active` to `false`
- Club removed from all views
- Related events need to be reassigned or cancelled

#### 8. **Get Clubs by Approval Status**
```
GET /api/clubs/admin/status/{status}
```
- status: `PENDING`, `APPROVED`, or `REJECTED`

---

## Venue Management

### Endpoint: `admin-venues.html` (or venues.html with admin privileges)

### Workflow:

#### 1. **Get All Venues**
```
GET /api/venues
```

#### 2. **Filter Venues by Type**
```
GET /api/venues/admin/type/{type}
```
- type values: `AUDITORIUM`, `HALL`, `LAB`, `GROUND`, `MEETING_ROOM`, `OTHER`
- Displayed as filter buttons on the page

#### 3. **Search Venues by Name**
```
GET /api/venues/admin/search?name=Main
```

#### 4. **Get Venues by Capacity**
```
GET /api/venues/admin/capacity?minCapacity=500
```
- Find venues that can accommodate at least X people

#### 5. **Add New Venue**
```
POST /api/venues
```
Body:
```json
{
  "name": "New IT Building Hall",
  "location": "3rd Floor, Main Building",
  "capacity": 150,
  "facilities": "Projector, Whiteboard, A/C, WiFi",
  "imageUrl": "url-to-image",
  "venueType": "HALL"
}
```

#### 6. **Update Venue Details**
```
PUT /api/venues/{venueId}
```
- Modify any venue information

#### 7. **Delete Venue**
```
DELETE /api/venues/{venueId}
```
- Remove venue from system

#### 8. **Check Venue Availability**
```
GET /api/venues/{venueId}/availability?startDate=2024-10-15T10:00:00&endDate=2024-10-15T12:00:00
```
Response: `true` or `false`
- Checks if venue has conflicting bookings for requested time slot
- Date format: ISO 8601 (ISO_DATE_TIME)

#### 9. **View Venue Bookings**
```
GET /api/venues/bookings?venueId={venueId}
```
- Shows all bookings for a specific venue

---

## Feedback Analytics

### Endpoint: `admin-feedback.html`

### Workflow:

#### 1. **Get All Events with Feedback**
```
GET /api/feedback/admin/completed-events
```
- Shows completed events that have feedback
- Display with: Event name, feedback count, average rating

#### 2. **View Feedback for Specific Event**
```
GET /api/feedback/event/{eventId}
```
Response: Array of feedback objects with: id, rating, comment, studentName, submittedAt

#### 3. **Get Feedback Analytics for Event**
```
GET /api/feedback/admin/event/{eventId}/analytics
```
Response: 
```json
{
  "totalFeedback": 25,
  "averageRating": 4.2,
  "ratingDistribution": {
    "1": 1,
    "2": 2,
    "3": 5,
    "4": 10,
    "5": 7
  },
  "feedbacks": [
    {
      "id": 1,
      "rating": 5,
      "comment": "Great event!",
      "studentName": "John Doe",
      "submittedAt": "2024-10-15T14:30:00"
    }
    // ... more feedbacks
  ]
}
```

#### 4. **Get Average Rating**
```
GET /api/feedback/admin/event/{eventId}/average-rating
```
Response: `4.2` (number)

#### 5. **Get Feedback Count**
```
GET /api/feedback/admin/event/{eventId}/count
```
Response: `25` (number)

#### 6. **Download Feedback as CSV** (Frontend implementation)
- Extract feedback data from API
- Convert to CSV format
- Trigger browser download

---

## Frontend Implementation Guide

### Key JavaScript Functions to Implement:

#### For Admin Dashboard:
```javascript
// Load dashboard stats
async function loadDashboardStats() {
  const users = await fetch('/api/users').then(r => r.json());
  const events = await fetch('/api/events/admin/approved').then(r => r.json());
  const pendingUsers = await fetch('/api/users/admin/pending').then(r => r.json());
  const pendingClubs = await fetch('/api/clubs/admin/pending').then(r => r.json());
  const pendingEvents = await fetch('/api/events/admin/pending').then(r => r.json());
  
  document.getElementById('stat-total-users').textContent = users.length;
  document.getElementById('stat-active-events').textContent = events.length;
  document.getElementById('stat-pending-approvals').textContent = 
    pendingUsers.length + pendingClubs.length + pendingEvents.length;
}
```

#### For User Management:
```javascript
// Search users
async function searchUsers(name, role) {
  const url = role ? 
    `/api/users/admin/search/role/${role}?name=${name}` :
    `/api/users/admin/search?name=${name}`;
  return await fetch(url).then(r => r.json());
}

// Approve user
async function approveUser(userId) {
  return await fetch(`/api/users/${userId}/approve`, {method: 'POST'}).then(r => r.json());
}

// Reject user
async function rejectUser(userId) {
  return await fetch(`/api/users/${userId}/reject`, {method: 'POST'}).then(r => r.json());
}
```

#### For Event Management:
```javascript
// Get events by filter type
async function getEventsByFilter(filter) {
  return await fetch(`/api/events/admin/${filter}`).then(r => r.json());
}

// Approve event
async function approveEvent(eventId) {
  return await fetch(`/api/events/${eventId}/approve`, {method: 'POST'}).then(r => r.json());
}

// Reject event
async function rejectEvent(eventId) {
  return await fetch(`/api/events/${eventId}/reject`, {method: 'POST'}).then(r => r.json());
}

// Edit event
async function updateEvent(eventId, eventData) {
  return await fetch(`/api/events/${eventId}`, {
    method: 'PUT',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify(eventData)
  }).then(r => r.json());
}
```

---

## Data Models Reference

### User Object:
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "universityId": "ENG2021001",
  "role": "STUDENT",
  "approvalStatus": "PENDING|APPROVED|REJECTED",
  "active": true
}
```

### Event Object:
```json
{
  "id": 1,
  "title": "Tech Fest 2024",
  "description": "Annual technology festival",
  "startTime": "2024-10-15T09:00:00",
  "endTime": "2024-10-15T17:00:00",
  "venue": { "id": 1, "name": "Main Auditorium" },
  "location": "Main Campus",
  "organizer": { "id": 1, "name": "Tech Club" },
  "eventType": "Conference",
  "maxParticipants": 500,
  "registrationDeadline": "2024-10-10T23:59:59",
  "posterUrl": "url/to/poster",
  "status": "PENDING|APPROVED|COMPLETED|CANCELLED"
}
```

### Club Object:
```json
{
  "id": 1,
  "name": "Robotics Club",
  "category": "Technology",
  "description": "Building robots and automation",
  "presidentEmail": "president@example.com",
  "email": "club@example.com",
  "logoUrl": "url/to/logo",
  "coverUrl": "url/to/cover",
  "active": true,
  "approvalStatus": "PENDING|APPROVED|REJECTED"
}
```

### Venue Object:
```json
{
  "id": 1,
  "name": "Main Auditorium",
  "location": "Ground Floor, Main Building",
  "capacity": 1000,
  "facilities": "Sound System, Projector, Lighting",
  "imageUrl": "url/to/image",
  "venueType": "AUDITORIUM|HALL|LAB|GROUND|MEETING_ROOM|OTHER"
}
```

### Feedback Object:
```json
{
  "id": 1,
  "event": { "id": 1 },
  "student": { "id": 1, "name": "Student Name" },
  "rating": 4,
  "comment": "Great event!",
  "submittedAt": "2024-10-15T18:30:00"
}
```

---

## Error Handling

All API endpoints return standard HTTP status codes:
- **200 OK**: Successful GET/PUT request
- **201 Created**: Successful POST request (new resource created)
- **204 No Content**: Successful DELETE request
- **400 Bad Request**: Invalid input data
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource already exists or state conflict
- **500 Internal Server Error**: Server error

Example error response:
```json
{
  "status": 400,
  "message": "User not found with id: 999"
}
```

---

## Summary Table of All Admin Endpoints

| Feature | Method | Endpoint |
|---------|--------|----------|
| Get All Users | GET | `/api/users` |
| Search Users | GET | `/api/users/admin/search?name={name}` |
| Get Users by Role | GET | `/api/users/admin/role/{role}` |
| Get Pending Users | GET | `/api/users/admin/pending` |
| Approve User | POST | `/api/users/{id}/approve` |
| Reject User | POST | `/api/users/{id}/reject` |
| Get Pending Events | GET | `/api/events/admin/pending` |
| Get Approved Events | GET | `/api/events/admin/approved` |
| Get Past Events | GET | `/api/events/admin/past` |
| Approve Event | POST | `/api/events/{id}/approve` |
| Reject Event | POST | `/api/events/{id}/reject` |
| Update Event | PUT | `/api/events/{id}` |
| Get Pending Clubs | GET | `/api/clubs/admin/pending` |
| Get Approved Clubs | GET | `/api/clubs/admin/approved` |
| Approve Club | POST | `/api/clubs/{id}/approve` |
| Reject Club | POST | `/api/clubs/{id}/reject` |
| Update Club | PUT | `/api/clubs/{id}` |
| Get Venues by Type | GET | `/api/venues/admin/type/{type}` |
| Search Venues | GET | `/api/venues/admin/search?name={name}` |
| Add Venue | POST | `/api/venues` |
| Update Venue | PUT | `/api/venues/{id}` |
| Check Availability | GET | `/api/venues/{id}/availability?startDate={...}&endDate={...}` |
| Get Event Feedback | GET | `/api/feedback/event/{eventId}` |
| Get Feedback Analytics | GET | `/api/feedback/admin/event/{eventId}/analytics` |
| Download CSV | GET | `/api/feedback/admin/event/{eventId}/analytics` |

