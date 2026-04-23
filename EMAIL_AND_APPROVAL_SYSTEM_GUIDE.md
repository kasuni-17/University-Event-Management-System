# Email System & Admin Approval Workflow Implementation Guide

## Overview

This guide explains how the new email notification and admin approval system works in your UniEvent platform. The system includes:

1. **Email Notifications** - Automated emails for all approval decisions
2. **Admin Approval Workflows** - Club admin accounts, club registrations, and event approvals
3. **Data Visibility Logic** - Content only visible after approval
4. **Event Filtering** - Upcoming, pending, and past events
5. **User Status Tracking** - Track registered events and joined clubs

---

## Table of Contents

1. [Configuration](#configuration)
2. [Database Schema](#database-schema)
3. [Email System](#email-system)
4. [Approval Workflows](#approval-workflows)
5. [API Endpoints](#api-endpoints)
6. [Frontend Integration](#frontend-integration)
7. [Security & Best Practices](#security--best-practices)

---

## Configuration

### Update `application.properties`

Replace the email configuration with your actual Gmail credentials:

```properties
# Gmail SMTP Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-gmail@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000

# Application Settings
app.name=UniEvent
app.url=http://localhost:8080
```

### Gmail App Password Setup

1. Enable 2-Step Verification on your Gmail account
2. Go to: https://myaccount.google.com/apppasswords
3. Select "Mail" and "Windows Computer"
4. Copy the generated 16-character password
5. Use it as `spring.mail.password` in your properties file

---

## Database Schema

### New Fields Added to Existing Entities

#### Users Table
```sql
ALTER TABLE users ADD COLUMN approval_date DATETIME;
ALTER TABLE users ADD COLUMN rejection_reason VARCHAR(255);
ALTER TABLE users ADD COLUMN created_at DATETIME;
ALTER TABLE users ADD COLUMN updated_at DATETIME;
```

#### Clubs Table
```sql
ALTER TABLE clubs ADD COLUMN approval_date DATETIME;
ALTER TABLE clubs ADD COLUMN rejection_reason TEXT;
ALTER TABLE clubs ADD COLUMN created_at DATETIME;
ALTER TABLE clubs ADD COLUMN updated_at DATETIME;
ALTER TABLE clubs ADD COLUMN created_by_user_id BIGINT;
ALTER TABLE clubs ADD FOREIGN KEY (created_by_user_id) REFERENCES users(id);
```

#### Events Table
```sql
ALTER TABLE events ADD COLUMN approval_date DATETIME;
ALTER TABLE events ADD COLUMN rejection_reason TEXT;
ALTER TABLE events ADD COLUMN created_at DATETIME;
ALTER TABLE events ADD COLUMN updated_at DATETIME;
```

---

## Email System

### EmailService Features

The `EmailService` automatically handles sending HTML-formatted emails for:

1. **Club Admin Account Approval/Rejection**
   - Sent to user's personal Gmail
   - Includes approval decision and reason

2. **Club Registration Approval/Rejection**
   - Includes club name and status
   - Optional rejection reason

3. **Event Approval/Rejection**
   - Includes event title, date, time
   - Optional rejection reason

4. **Event Registration Confirmation**
   - Sent when user registers for event
   - Includes event details

5. **Club Membership Confirmation**
   - Sent when user joins a club
   - Includes club details

### Email Templates

All emails use professional HTML templates with:
- Consistent branding (colors, logos)
- Clear status indicators
- Call-to-action buttons
- Mobile-responsive design

---

## Approval Workflows

### 1. Club Admin Account Approval Workflow

**Flow:**
```
User Signs Up as Club Admin
    ↓
Account Set to PENDING (not active)
    ↓
Admin Receives Notification
    ↓
Admin Approves/Rejects
    ↓
Club Admin Receives Email
    ↓
If Approved: Account Activated (can login)
If Rejected: Account Remains Inactive
```

**Status Transitions:**
- Initial: `PENDING` (inactive)
- Approved: `APPROVED` (active)
- Rejected: `REJECTED` (inactive)

### 2. Club Registration Approval Workflow

**Flow:**
```
Club Admin Creates New Club
    ↓
Club Set to PENDING (not visible)
    ↓
Admin Sees Pending Clubs List
    ↓
Admin Approves/Rejects
    ↓
Club Admin Receives Email
    ↓
If Approved: Club Becomes VISIBLE
If Rejected: Club Not Visible
```

**Visibility Rules:**
- Only APPROVED + ACTIVE clubs appear on student/club admin pages
- PENDING clubs only visible in admin dashboard
- REJECTED clubs never visible

### 3. Event Approval Workflow

**Flow:**
```
Club Admin Creates Event
    ↓
Event Set to PENDING (not visible)
    ↓
Admin Sees Pending Events List
    ↓
Admin Approves/Rejects
    ↓
Club Admin Receives Email
    ↓
If Approved: Event Becomes VISIBLE
If Rejected: Event Hidden (marked CANCELLED)
```

**Event Status Values:**
- `DRAFT` - Not submitted
- `PENDING` - Submitted, awaiting approval
- `APPROVED` - Approved, visible to students
- `COMPLETED` - Event has occurred
- `CANCELLED` - Rejected or cancelled

---

## API Endpoints

### Admin Approval Endpoints

#### Club Admin Account Management

```
GET  /api/admin/pending-club-admins
     Get all pending club admin registrations

POST /api/admin/approve-club-admin/{userId}
     Approve club admin account

POST /api/admin/reject-club-admin/{userId}
     Reject club admin account
     Body: { "rejectionReason": "Reason text" }
```

#### Club Registration Management

```
GET  /api/admin/pending-clubs
     Get all pending club registrations

POST /api/admin/approve-club/{clubId}
     Approve club registration

POST /api/admin/reject-club/{clubId}
     Reject club registration
     Body: { "rejectionReason": "Reason text" }
```

#### Event Approval Management

```
GET  /api/admin/pending-events
     Get all pending event approvals

POST /api/admin/approve-event/{eventId}
     Approve event

POST /api/admin/reject-event/{eventId}
     Reject event
     Body: { "rejectionReason": "Reason text" }

GET  /api/admin/approval-stats
     Get approval workflow statistics
     Returns: {
       "pending_club_admins": 5,
       "pending_clubs": 3,
       "pending_events": 7
     }
```

### Event Filtering Endpoints

```
GET  /api/events/upcoming
     Get all upcoming events (approved only)
     Returns: List of events sorted by date

GET  /api/events/past
     Get all past events (completed/approved)
     Returns: List of events sorted by date (newest first)

GET  /api/events/approved
     Get all approved events

GET  /api/events/pending/{clubId}
     Get pending events for a specific club

GET  /api/events/club/{clubId}?filter=upcoming|pending|past
     Get club events filtered by type
     Used in "My Clubs" page

GET  /api/events/{eventId}/check-approval
     Check if specific event is approved
     Returns: { "eventId": 1, "approved": true }
```

### User Status Endpoints

```
GET  /api/user-status/registered-events/{userId}
     Get all events registered by user
     Returns: List of Registration objects

GET  /api/user-status/is-registered/{userId}/{eventId}
     Check if user registered for event
     Returns: { "registered": true, "userId": 1, "eventId": 5 }

GET  /api/user-status/joined-clubs/{userId}
     Get all clubs joined by user
     Returns: List of ClubMembership objects

GET  /api/user-status/is-joined/{userId}/{clubId}
     Check if user joined club
     Returns: { "joined": true, "userId": 1, "clubId": 3 }
```

---

## Frontend Integration

### JavaScript Helper Functions

```javascript
// Check if user is registered for event
async function isUserRegisteredForEvent(userId, eventId) {
    const response = await fetch(`/api/user-status/is-registered/${userId}/${eventId}`);
    const data = await response.json();
    return data.registered;
}

// Check if user joined club
async function isUserJoinedClub(userId, clubId) {
    const response = await fetch(`/api/user-status/is-joined/${userId}/${clubId}`);
    const data = await response.json();
    return data.joined;
}

// Get upcoming events
async function getUpcomingEvents() {
    const response = await fetch('/api/events/upcoming');
    return await response.json();
}

// Get past events
async function getPastEvents() {
    const response = await fetch('/api/events/past');
    return await response.json();
}

// Get club events filtered
async function getClubEventsByFilter(clubId, filter = 'upcoming') {
    const response = await fetch(`/api/events/club/${clubId}?filter=${filter}`);
    return await response.json();
}
```

### Event Page Display Logic

```javascript
// Display event with registration button
async function displayEvent(event, userId) {
    const isRegistered = await isUserRegisteredForEvent(userId, event.id);
    
    let buttonHTML = '';
    if (isRegistered) {
        buttonHTML = `<button class="btn btn-danger" disabled>✓ Registered</button>
                     <button class="btn btn-info">View Details</button>`;
    } else {
        buttonHTML = `<button class="btn btn-primary" onclick="registerEvent(${event.id})">Register Now</button>
                     <button class="btn btn-info">View Details</button>`;
    }
    
    return `
        <div class="event-card">
            <h3>${event.title}</h3>
            <p>${event.description}</p>
            <p><strong>Date:</strong> ${event.startTime}</p>
            <p><strong>Location:</strong> ${event.location}</p>
            ${buttonHTML}
        </div>
    `;
}
```

### Clubs Page Display Logic

```javascript
// Display club with join button
async function displayClub(club, userId) {
    const isJoined = await isUserJoinedClub(userId, club.id);
    
    let buttonHTML = '';
    if (isJoined) {
        buttonHTML = `<button class="btn btn-danger" disabled>✓ Joined</button>
                     <button class="btn btn-info">View Profile</button>`;
    } else {
        buttonHTML = `<button class="btn btn-primary" onclick="joinClub(${club.id})">Join Club</button>
                     <button class="btn btn-info">View Profile</button>`;
    }
    
    return `
        <div class="club-card">
            <img src="${club.logoUrl}" alt="${club.name}">
            <h3>${club.name}</h3>
            <p>${club.category}</p>
            <p>${club.description}</p>
            ${buttonHTML}
        </div>
    `;
}
```

### My Clubs Page with Event Filtering

```html
<div class="my-clubs-page">
    <h2>My Clubs</h2>
    
    <!-- Filter Buttons -->
    <div class="event-filter">
        <button class="filter-btn active" onclick="filterEvents('upcoming')">
            Upcoming Events
        </button>
        <button class="filter-btn" onclick="filterEvents('pending')">
            Pending Events
        </button>
        <button class="filter-btn" onclick="filterEvents('past')">
            Past Events
        </button>
    </div>
    
    <!-- Events Container -->
    <div id="events-container"></div>
</div>

<script>
async function filterEvents(filterType = 'upcoming') {
    const clubId = getCurrentClubId(); // Your implementation
    const response = await fetch(`/api/events/club/${clubId}?filter=${filterType}`);
    const data = await response.json();
    
    // Update UI
    updateEventsList(data.events);
    
    // Update button states
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    event.target.classList.add('active');
}
</script>
```

---

## Security & Best Practices

### 1. Email Security

✅ **DO:**
- Always use Gmail App Passwords (never your main password)
- Enable 2-Step Verification on your email account
- Store credentials in environment variables, not in code
- Use HTTPS for all API calls
- Validate email addresses server-side

❌ **DON'T:**
- Commit real email credentials to Git
- Expose email addresses in API responses unnecessarily
- Send emails in synchronous request handlers (causes delays)
- Store plain text passwords

### 2. Approval Workflow Security

✅ **DO:**
- Require admin authentication for all approval endpoints
- Log all approval actions with timestamps and user IDs
- Add role-based access control (@PreAuthorize in Spring)
- Validate all request data server-side
- Use database transactions for atomic operations

❌ **DON'T:**
- Allow users to approve their own submissions
- Skip validation of approval reasons
- Allow bulk approvals without confirmation
- Modify approval status without audit trail

### 3. Data Visibility

✅ **DO:**
- Always filter PENDING/REJECTED items on student pages
- Show only APPROVED items by default
- Implement pagination for large lists
- Cache common queries
- Use database queries, not in-memory filtering

❌ **DON'T:**
- Send PENDING items to frontend and filter there
- Show rejection reasons to other users
- Cache approval status for too long
- Trust client-side filtering

### 4. Role-Based Access Control

```java
@GetMapping("/admin/pending-clubs")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> getPendingClubs() {
    // Only ADMIN role can access
}

@PostMapping("/club/new")
@PreAuthorize("hasAnyRole('ADMIN', 'CLUB_ADMIN') and #user.isApproved()")
public ResponseEntity<?> createClub(@RequestBody Club club, @CurrentUser User user) {
    // Only approved CLUB_ADMIN or ADMIN
}
```

### 5. Input Validation

```java
public User createUser(User user) {
    // Validate university email domain
    if (!user.getEmail().endsWith("@my.sliit.lk")) {
        throw new RuntimeException("Invalid university email");
    }
    
    // Validate personal Gmail
    if (!user.getPersonalEmail().endsWith("@gmail.com")) {
        throw new RuntimeException("Personal Gmail required");
    }
    
    // Validate password complexity
    // ... validate before saving
}
```

### 6. Database Constraints

```sql
-- Add unique constraints
ALTER TABLE users ADD UNIQUE KEY unique_email (email);
ALTER TABLE users ADD UNIQUE KEY unique_personal_email (personal_email);

-- Add indexes for common queries
CREATE INDEX idx_users_role_approval ON users(role, approval_status);
CREATE INDEX idx_clubs_approval ON clubs(approval_status, active);
CREATE INDEX idx_events_status ON events(status, start_time);

-- Add check constraints
ALTER TABLE users ADD CHECK (approval_status IN ('PENDING', 'APPROVED', 'REJECTED'));
ALTER TABLE clubs ADD CHECK (approval_status IN ('PENDING', 'APPROVED', 'REJECTED'));
ALTER TABLE events ADD CHECK (status IN ('DRAFT', 'PENDING', 'APPROVED', 'COMPLETED', 'CANCELLED'));
```

---

## Testing the System

### Test Scenario 1: Club Admin Registration & Approval

1. Register new club admin account
2. Verify account is PENDING and not active (login should fail)
3. Admin approves the account
4. Club admin receives email
5. Club admin can now login

### Test Scenario 2: Club Registration Approval

1. Club admin creates new club
2. Club appears as PENDING in admin dashboard
3. Admin approves club
4. Club admin receives email
5. Club appears on student pages
6. Students can join the club

### Test Scenario 3: Event Approval

1. Club admin creates event
2. Event appears as PENDING in admin dashboard
3. Admin approves event
4. Club admin receives email
5. Event appears on student pages
6. Students can register

### Test Scenario 4: Event Filtering

1. Create multiple events with different dates
2. Test `/api/events/upcoming` - should return only future approved events
3. Test `/api/events/past` - should return only past events
4. Test `/api/events/club/{id}?filter=upcoming` - should return club's upcoming events only

---

## Troubleshooting

### Email Not Sending?

```
Error: "SMTPAuthenticationException"
Solution: Check Gmail credentials and enable 2-Step Verification

Error: "javax.mail.SendFailedException"
Solution: Ensure Gmail "Less secure app access" is disabled (use App Password instead)

Error: "Connection timeout"
Solution: Check firewall, ensure port 587 is open, verify SMTP settings
```

### Approval Not Triggering Email?

- Check EmailService is autowired properly
- Verify `spring.mail.username` is set correctly
- Check application logs for exceptions
- Ensure personal email address is valid

### Events Not Filtering Correctly?

- Verify event status is set to APPROVED
- Check start_time and end_time are correct
- Confirm LocalDateTime.now() is accurate on server
- Use database query tools to verify data

---

## Summary of Files Created/Modified

### New Files Created:
- `EmailService.java` - Email notification service with templates
- `AdminController.java` - Admin approval endpoints
- `UserStatusController.java` - User status checking endpoints
- `EventFilterController.java` - Event filtering endpoints
- DTOs: `ApprovalRequestDTO.java`, `EventFilterDTO.java`, etc.

### Files Modified:
- `UserService.java` - Added approval methods
- `ClubService.java` - Added approval workflow
- `EventService.java` - Added filtering logic
- `User.java` - Added approval tracking fields
- `Club.java` - Added approval tracking fields
- `Event.java` - Added approval tracking fields
- `ClubRepository.java` - Added query methods
- `RegistrationRepository.java` - Added query methods

### Configuration Files:
- `application.properties` - Email configuration

---

## Next Steps

1. ✅ Update `application.properties` with Gmail credentials
2. ✅ Run database migrations for new columns
3. ✅ Test email functionality
4. ✅ Implement frontend components
5. ✅ Add role-based access controls
6. ✅ Deploy to production

---

**For questions or issues, refer to the code comments or contact your development team.**
