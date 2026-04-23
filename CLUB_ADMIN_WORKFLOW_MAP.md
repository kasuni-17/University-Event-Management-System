# Club Admin Workflow - Complete Process Map

## Overview
This document maps the complete workflow for Club Admin club registration, admin approval, and My Club page status updates.

---

## Process Flow Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                  CLUB ADMIN WORKFLOW PROCESS                        │
└─────────────────────────────────────────────────────────────────────┘

PHASE 1: CLUB ADMIN SUBMITS REGISTRATION
─────────────────────────────────────────
Club Admin User
    │
    ├─→ Navigate to: clubadmin-newclub.html
    │        Create club registration form with:
    │        - Club Name
    │        - Category (Sports, Cultural, Academic, etc.)
    │        - Description
    │        - President Email
    │        - Club Email
    │        - Logo URL
    │        - Cover URL
    │
    ├─→ Submit Form (POST)
    │        └→ /api/clubs (ClubController.createClub)
    │           └→ ClubService.createClub(club)
    │              └→ Saves to Database
    │
    ├─→ New Club Created with Status:
    │        ApprovalStatus = PENDING
    │        Active = true
    │        presidentEmail = [Club Admin's Email]
    │
    └─→ Redirected to clubadmin-myclub.html
           ✓ My Club page shows:
             • Club details (Name, Logo, Cover)
             • Yellow "Pending Approval" badge
             • Auto-refresh enabled every 15 seconds


PHASE 2: ADMIN REVIEWS PENDING CLUBS
─────────────────────────────────────
Admin User
    │
    ├─→ Navigate to: admin-clubs.html
    │
    ├─→ Page loads (JavaScript runs):
    │        └→ Calls: GET /api/clubs/admin/pending
    │           └→ ClubController.getPendingClubs()
    │              └→ ClubService.getPendingClubs()
    │                 └→ Returns all PENDING clubs
    │
    └─→ Displays pending clubs in grid:
           Each card shows:
           • Club Logo
           • Club Name
           • Category
           • President Email
           • [Approve Button] [Reject Button] [View Profile Button]


PHASE 3: ADMIN APPROVES OR REJECTS
────────────────────────────────────
Admin User
    │
    ├─→ Clicks "Approve" Button
    │        └→ POST /api/clubs/{id}/approve
    │           └→ ClubController.approveClub(id)
    │              └→ ClubService.approveClub(id)
    │                 └→ Updates: approvalStatus = APPROVED
    │                 └→ Updates: active = true
    │                 └→ Saves to Database
    │
    ├─→ OR Clicks "Reject" Button
    │        └→ POST /api/clubs/{id}/reject
    │           └→ ClubController.rejectClub(id)
    │              └→ ClubService.rejectClub(id)
    │                 └→ Updates: approvalStatus = REJECTED
    │                 └→ Saves to Database
    │
    └─→ Admin page reloads:
           Both pending and approved club lists refresh


PHASE 4: CLUB ADMIN SEES UPDATED STATUS
─────────────────────────────────────────
Club Admin User (On clubadmin-myclub.html)
    │
    ├─→ Auto-refresh mechanism triggers (every 15 seconds)
    │        └→ JavaScript runs:
    │           if (autoRefreshInterval) clearInterval(autoRefreshInterval)
    │           GET /api/clubs/organizer/{user.id}
    │           OR GET /api/clubs/organizer-email/{user.email}
    │
    ├─→ Fetches club data from backend
    │        └→ ClubController.getClubsByOrganizer(userId)
    │           └→ ClubService.getClubsByPresidentEmail(email)
    │              └→ Returns club with updated approvalStatus
    │
    ├─→ Status Badge Updates (renderClub function):
    │
    │   IF approvalStatus === 'PENDING':
    │       └─→ Yellow badge: ⏱ "Pending Approval"
    │
    │   IF approvalStatus === 'APPROVED':
    │       └─→ Green badge: ✓ "Verified Club"
    │           • Club page becomes fully functional
    │           • Can now create events
    │           • Can manage members
    │
    │   IF approvalStatus === 'REJECTED':
    │       └─→ Red badge: ✗ "Rejected"
    │           • May need to resubmit
    │
    └─→ User sees real-time status update


=============================================================================
DATABASE STATE CHANGES
=============================================================================

Initial Club Creation (Phase 1):
┌──────────────────────────────────────────┐
│ Club Entity (Newly Created)              │
├──────────────────────────────────────────┤
│ id: 1 (auto-generated)                   │
│ name: "Chess Club"                       │
│ category: "Academic"                     │
│ description: "For chess enthusiasts"     │
│ presidentEmail: "clubadmin@univ.edu"     │
│ email: "chess@univ.edu"                  │
│ approvalStatus: PENDING ⚠️                │
│ active: true                             │
│ logoUrl: "https://..."                   │
│ coverUrl: "https://..."                  │
└──────────────────────────────────────────┘

After Admin Approval (Phase 3):
┌──────────────────────────────────────────┐
│ Club Entity (Updated)                    │
├──────────────────────────────────────────┤
│ id: 1                                    │
│ ... (other fields unchanged)             │
│ approvalStatus: APPROVED ✓                │
│ active: true                             │
└──────────────────────────────────────────┘


=============================================================================
API ENDPOINTS USED
=============================================================================

1. CREATE CLUB (Phase 1)
   POST /api/clubs
   Body: { name, category, description, presidentEmail, email, logoUrl, coverUrl }
   Response: Created Club (with approvalStatus: PENDING)

2. GET PENDING CLUBS (Phase 2)
   GET /api/clubs/admin/pending
   Response: Array of clubs with approvalStatus: PENDING

3. APPROVE CLUB (Phase 3)
   POST /api/clubs/{id}/approve
   Response: Updated club (with approvalStatus: APPROVED)

4. REJECT CLUB (Phase 3)
   POST /api/clubs/{id}/reject
   Response: Updated club (with approvalStatus: REJECTED)

5. GET CLUB ADMIN'S CLUBS (Phase 4)
   GET /api/clubs/organizer/{userId}
   OR
   GET /api/clubs/organizer-email/{email}
   Response: Array of clubs owned by this president


=============================================================================
HTML PAGES INVOLVED
=============================================================================

clubadmin-newclub.html
  └→ Allows club admin to fill registration form
  └→ Submits to API, redirects to clubadmin-myclub.html

clubadmin-myclub.html
  └→ Shows club admin's club with status badge
  └→ Auto-refreshes every 15 seconds to check approval status
  └→ Shows: Club details, members, pending join requests, published events
  └→ Status badges:
      • PENDING (yellow) ⏱ "Pending Approval"
      • APPROVED (green) ✓ "Verified Club"
      • REJECTED (red) ✗ "Rejected"

admin-clubs.html
  └→ Shows two sections:
      1. "Pending Club Registrations" (Phase 2)
         - Lists clubs waiting for approval
         - [Approve] [Reject] [View Profile] buttons
      2. "All Registered Clubs"
         - Lists approved clubs
         - [Edit Details] [Remove] [View Profile] buttons


=============================================================================
KEY JAVA CLASSES AND METHODS
=============================================================================

Entity: Club.java
  - id: Long (PK, auto-generated)
  - name: String
  - category: String
  - description: String
  - presidentEmail: String
  - email: String
  - logoUrl: String
  - coverUrl: String
  - active: boolean = true
  - approvalStatus: ApprovalStatus (enum) = PENDING
    └─→ Values: PENDING, APPROVED, REJECTED

Repository: ClubRepository.java
  - findByApprovalStatus(ApprovalStatus status): List<Club>
  - findByPresidentEmail(String email): List<Club>

Service: ClubService.java
  - createClub(Club club): Club
  - getAllClubs(): List<Club>
  - getPendingClubs(): List<Club>
    └→ Returns clubs with approvalStatus == PENDING
  - getApprovedClubs(): List<Club>
    └→ Returns clubs with approvalStatus == APPROVED
  - approveClub(Long clubId): Club
    └→ Sets approvalStatus to APPROVED, active to true
  - rejectClub(Long clubId): Club
    └→ Sets approvalStatus to REJECTED
  - getClubsByPresidentEmail(String email): List<Club>
    └→ Returns all clubs owned by this president

Controller: ClubController.java
  - POST /api/clubs → createClub()
  - GET /api/clubs/admin/pending → getPendingClubs()
  - GET /api/clubs/admin/approved → getApprovedClubs()
  - POST /api/clubs/{id}/approve → approveClub()
  - POST /api/clubs/{id}/reject → rejectClub()
  - GET /api/clubs/organizer/{userId} → getClubsByOrganizer()
  - GET /api/clubs/organizer-email/{email} → getClubsByOrganizerEmail()


=============================================================================
AUTO-REFRESH MECHANISM (My Club Page)
=============================================================================

clubadmin-myclub.html JavaScript:
  
  1. On Page Load:
     └→ init() → checkUserClub()
        └→ Fetch club data from /api/clubs/organizer/{user.id}
        └→ Display club with current approvalStatus

  2. Auto-Refresh Loop:
     └→ startAutoRefresh() runs every 15 seconds
        └→ Checks if club status has changed
        └→ If was PENDING and now APPROVED:
           ├→ Hide empty state (if shown)
           └→ Render club with updated status badge

  3. On Tab Focus:
     └→ document.addEventListener('visibilitychange')
     └→ When page comes into focus, immediately refresh
     └→ Ensures fresh data when user returns to MyClub tab

  4. Status Badge Rendering:
     └→ renderClub(club) updates badge:
        ├─→ PENDING: Yellow ⏱ "Pending Approval"
        ├─→ APPROVED: Green ✓ "Verified Club"
        └─→ REJECTED: Red ✗ "Rejected"


=============================================================================
COMPLETE USER JOURNEY
=============================================================================

CLUB ADMIN (ClubAdmin role)
└─→ 1. Log in
    2. Navigate to clubadmin-dashboard.html
    3. Click "My Club" or go to clubadmin-myclub.html
    4. If no club exists → See "You Don't Have a Club Yet" screen
    5. Click "Register Your Club" → Go to clubadmin-newclub.html
    6. Fill registration form (name, category, description, email, logo, cover)
    7. Submit → Club created with PENDING status
    8. Redirected to clubadmin-myclub.html
    9. See club with YELLOW "Pending Approval" badge
    10. Wait for admin approval...
    11. Page auto-refreshes every 15 seconds
    12. When admin approves → Badge turns GREEN "Verified Club"
    13. Now can manage events, members, and club operations

ADMIN (Admin role)
└─→ 1. Log in
    2. Navigate to admin-dashboard.html
    3. Click on "Clubs" → admin-clubs.html
    4. See "Pending Club Registrations" section
    5. Review pending clubs (logo, name, category, president)
    6. For each club:
       ├─→ Click "View Profile" to see full details
       ├─→ Click "Approve" to approve the registration
       └─→ Click "Reject" to reject the registration
    7. Approved clubs appear in "All Registered Clubs" section
    8. Click "View Profile" or "Edit Details" for approved clubs


=============================================================================
TROUBLESHOOTING CHECKLIST
=============================================================================

If My Club page not showing club after admin approval:

□ 1. Check if auto-refresh is running
      - Open browser console
      - Check if /api/clubs/organizer/{userId} calls are happening every 15s
      - Look for fetch responses

□ 2. Verify API endpoint returns club data
      - Test: GET /api/clubs/organizer/{userId}
      - Should return array with club object
      - Club should have approvalStatus: "APPROVED"

□ 3. Check if user ID is correct
      - In localStorage, check user.id value
      - May need fallback to email lookup if ID mismatch

□ 4. Verify renderClub() is being called
      - Check browser console for club data
      - Verify DOM elements are being updated

□ 5. Clear browser cache and localStorage
      - localStorage.removeItem('user')
      - Re-login and try again

□ 6. Check database directly
      - Query: SELECT * FROM clubs WHERE president_email = ?
      - Verify approvalStatus column is updated to "APPROVED"


=============================================================================
```

---

## Key Points Summary

### The 3-Stage Process:

1. **Club Registration (Club Admin)** → Club created with `approvalStatus = PENDING`
2. **Admin Review & Approval** → Admin approves, `approvalStatus = APPROVED`
3. **Real-time Status Update** → My Club page auto-refreshes and shows updated status

### Critical Fields:
- `presidentEmail` - Links club to club admin
- `approvalStatus` - Tracks: PENDING → APPROVED or REJECTED
- `active` - Set to true when approved

### Frontend-Backend Integration:
- Frontend auto-refreshes every 15 seconds (configurable)
- Fetches from `/api/clubs/organizer/{userId}` or `/api/clubs/organizer-email/{email}`
- Updates UI with current approval status

---

## Common Issues & Solutions

**Issue:** My Club page shows "You Don't Have a Club Yet" after admin approval
**Solution:** 
- Verify `presidentEmail` matches logged-in user's email
- Check if auto-refresh is running (check browser console)
- Clear localStorage and re-login
- Verify database: `approvalStatus` should be "APPROVED"

**Issue:** Admin doesn't see pending clubs
**Solution:**
- Check API endpoint: `/api/clubs/admin/pending`
- Verify database has clubs with `approvalStatus = PENDING`
- Check network requests in browser DevTools

---
