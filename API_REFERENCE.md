# UniEvent Email & Approval System - API Reference

## Quick API Reference Guide

### Base URL
```
http://localhost:8080/api
```

---

## Admin Approval APIs

### 1. Club Admin Account Approvals

#### Get Pending Club Admins
```http
GET /admin/pending-club-admins
```

**Response:**
```json
[
    {
        "id": 1,
        "name": "John Admin",
        "email": "john@my.sliit.lk",
        "personalEmail": "john@gmail.com",
        "role": "CLUB_ADMIN",
        "approvalStatus": "PENDING",
        "createdAt": "2024-01-15T10:30:00"
    }
]
```

#### Approve Club Admin
```http
POST /admin/approve-club-admin/1
```

**Response:**
```json
{
    "message": "Club admin account approved successfully",
    "user": {
        "id": 1,
        "name": "John Admin",
        "approvalStatus": "APPROVED",
        "active": true,
        "approvalDate": "2024-01-15T14:20:00"
    }
}
```

#### Reject Club Admin
```http
POST /admin/reject-club-admin/1
Content-Type: application/json

{
    "rejectionReason": "Incomplete application information"
}
```

**Response:**
```json
{
    "message": "Club admin account rejected successfully",
    "user": {
        "id": 1,
        "name": "John Admin",
        "approvalStatus": "REJECTED",
        "active": false,
        "rejectionReason": "Incomplete application information",
        "approvalDate": "2024-01-15T14:20:00"
    }
}
```

---

### 2. Club Registration Approvals

#### Get Pending Clubs
```http
GET /admin/pending-clubs
```

**Response:**
```json
[
    {
        "id": 5,
        "name": "Sports Club",
        "category": "Sports",
        "description": "For sports enthusiasts",
        "presidentEmail": "john@my.sliit.lk",
        "approvalStatus": "PENDING",
        "createdAt": "2024-01-14T09:15:00"
    }
]
```

#### Approve Club
```http
POST /admin/approve-club/5
```

**Response:**
```json
{
    "message": "Club approved successfully",
    "club": {
        "id": 5,
        "name": "Sports Club",
        "approvalStatus": "APPROVED",
        "active": true,
        "approvalDate": "2024-01-15T14:25:00"
    }
}
```

#### Reject Club
```http
POST /admin/reject-club/5
Content-Type: application/json

{
    "rejectionReason": "Club description does not meet standards"
}
```

---

### 3. Event Approvals

#### Get Pending Events
```http
GET /admin/pending-events
```

**Response:**
```json
[
    {
        "id": 12,
        "title": "Annual Sports Meet",
        "description": "Annual sports competition",
        "startTime": "2024-02-20T10:00:00",
        "location": "Main Ground",
        "organizer": {
            "id": 5,
            "name": "Sports Club"
        },
        "status": "PENDING",
        "createdAt": "2024-01-14T16:45:00"
    }
]
```

#### Approve Event
```http
POST /admin/approve-event/12
```

**Response:**
```json
{
    "message": "Event approved successfully",
    "event": {
        "id": 12,
        "title": "Annual Sports Meet",
        "status": "APPROVED",
        "approvalDate": "2024-01-15T14:30:00"
    }
}
```

#### Reject Event
```http
POST /admin/reject-event/12
Content-Type: application/json

{
    "rejectionReason": "Event time conflicts with other scheduled events"
}
```

#### Get Approval Statistics
```http
GET /admin/approval-stats
```

**Response:**
```json
{
    "pending_club_admins": 3,
    "pending_clubs": 2,
    "pending_events": 5
}
```

---

## Event Filtering APIs

### Get Upcoming Events
```http
GET /events/upcoming
```

**Response:**
```json
[
    {
        "id": 1,
        "title": "Tech Seminar",
        "startTime": "2024-02-01T10:00:00",
        "endTime": "2024-02-01T12:00:00",
        "location": "Auditorium",
        "status": "APPROVED",
        "organizer": "Tech Club",
        "eventType": "Seminar",
        "posterUrl": "https://..."
    }
]
```

### Get Past Events
```http
GET /events/past
```

### Get All Approved Events
```http
GET /events/approved
```

### Get Club Events (Filtered)
```http
GET /events/club/5?filter=upcoming
GET /events/club/5?filter=pending
GET /events/club/5?filter=past
```

**Response:**
```json
{
    "filter": "upcoming",
    "count": 3,
    "events": [
        {
            "id": 1,
            "title": "Event Title",
            "startTime": "2024-02-01T10:00:00",
            ...
        }
    ]
}
```

### Get Pending Events for Club
```http
GET /events/pending/5
```

### Check Event Approval Status
```http
GET /events/12/check-approval
```

**Response:**
```json
{
    "eventId": 12,
    "approved": true
}
```

---

## User Status APIs

### Check if User Registered for Event
```http
GET /user-status/is-registered/1/12
```

**Response:**
```json
{
    "registered": true,
    "userId": 1,
    "eventId": 12
}
```

### Get User's Registered Events
```http
GET /user-status/registered-events/1
```

**Response:**
```json
[
    {
        "id": 100,
        "student": {...},
        "event": {...},
        "registrationDate": "2024-01-15T10:00:00",
        "status": "CONFIRMED"
    }
]
```

### Check if User Joined Club
```http
GET /user-status/is-joined/1/5
```

**Response:**
```json
{
    "joined": true,
    "userId": 1,
    "clubId": 5
}
```

### Get User's Joined Clubs
```http
GET /user-status/joined-clubs/1
```

**Response:**
```json
[
    {
        "id": 50,
        "student": {...},
        "club": {...},
        "joinDate": "2024-01-10T14:20:00",
        "status": "APPROVED"
    }
]
```

---

## Error Responses

### Bad Request (400)
```json
{
    "error": "User not found with id: 999"
}
```

### Unauthorized (401)
```json
{
    "error": "This user is not a club admin"
}
```

### Not Found (404)
```json
{
    "error": "Event not found with id: 999"
}
```

### Conflict (409)
```json
{
    "error": "User is already registered for this event"
}
```

---

## Common Use Cases

### Frontend: Display Event with Registration Button

```javascript
const userId = 1;
const eventId = 12;

// Check if already registered
const response = await fetch(`/api/user-status/is-registered/${userId}/${eventId}`);
const {registered} = await response.json();

if (registered) {
    // Show "Already Registered" button (disabled, red)
    buttonText = "✓ Registered";
    buttonDisabled = true;
    buttonClass = "btn-danger";
} else {
    // Show "Register Now" button
    buttonText = "Register Now";
    buttonDisabled = false;
    buttonClass = "btn-primary";
}
```

### Frontend: Display Club with Join Button

```javascript
const userId = 1;
const clubId = 5;

// Check if already joined
const response = await fetch(`/api/user-status/is-joined/${userId}/${clubId}`);
const {joined} = await response.json();

if (joined) {
    // Show "Already Joined" button (disabled, red)
    buttonText = "✓ Joined";
    buttonDisabled = true;
    buttonClass = "btn-danger";
} else {
    // Show "Join Club" button
    buttonText = "Join Club";
    buttonDisabled = false;
    buttonClass = "btn-primary";
}
```

### Frontend: Load Upcoming Events

```javascript
async function loadUpcomingEvents() {
    const response = await fetch('/api/events/upcoming');
    const events = await response.json();
    
    events.forEach(event => {
        console.log(`${event.title} on ${event.startTime}`);
    });
}
```

### Frontend: Filter Club Events

```javascript
async function filterClubEvents(clubId, filterType = 'upcoming') {
    const response = await fetch(`/api/events/club/${clubId}?filter=${filterType}`);
    const data = await response.json();
    
    console.log(`${data.count} ${filterType} events found`);
    console.log(data.events);
}
```

### Admin: Get Approval Dashboard Stats

```javascript
async function getApprovalStats() {
    const response = await fetch('/api/admin/approval-stats');
    const stats = await response.json();
    
    console.log(`${stats.pending_club_admins} club admins waiting for approval`);
    console.log(`${stats.pending_clubs} clubs waiting for approval`);
    console.log(`${stats.pending_events} events waiting for approval`);
}
```

---

## Rate Limiting & Pagination

### Current Implementation
- No rate limiting (add in production)
- No pagination (implement for large datasets)

### Future Improvements
```http
GET /events/upcoming?page=1&limit=20
GET /admin/pending-clubs?page=1&limit=10
```

---

## Authentication

Currently, these endpoints require:
- Valid session/JWT token
- Role-based permissions (for admin endpoints)

Example with Authorization:
```http
GET /admin/pending-clubs
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Testing with cURL

### Test Event Approval
```bash
curl -X POST http://localhost:8080/api/admin/approve-event/12 \
  -H "Content-Type: application/json"
```

### Test Event Rejection
```bash
curl -X POST http://localhost:8080/api/admin/reject-event/12 \
  -H "Content-Type: application/json" \
  -d '{"rejectionReason":"Invalid event details"}'
```

### Test User Registration Check
```bash
curl http://localhost:8080/api/user-status/is-registered/1/12
```

### Test Get Upcoming Events
```bash
curl http://localhost:8080/api/events/upcoming
```

---

## Email Notifications

Each approval/rejection triggers an automatic email:

1. **Club Admin Approval** → Email sent to club admin's personal Gmail
2. **Club Approval** → Email sent to club president's personal Gmail  
3. **Event Approval** → Email sent to club admin's personal Gmail
4. **Event Registration** → Email sent to student's personal Gmail
5. **Club Membership** → Email sent to student's personal Gmail

See `EmailService.java` for template details.

---

**Last Updated:** January 2024
**Version:** 1.0
