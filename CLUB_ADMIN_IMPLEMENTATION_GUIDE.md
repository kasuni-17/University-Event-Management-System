# Club Admin Workflow - Implementation Verification Guide

## Step-by-Step Testing & Verification

### PART 1: Club Registration (Phase 1)

#### Test Case 1.1: Create Club
```
1. Go to: clubadmin-newclub.html
2. Fill form:
   - Name: "Test Club"
   - Category: "Academic"
   - Description: "Test"
   - President Email: (should auto-fill from user profile)
   - Club Email: "testclub@univ.edu"
   - Logo URL: (paste valid image URL)
   - Cover URL: (paste valid image URL)
3. Click Submit
4. Expected Redirect: clubadmin-myclub.html
5. Expected Result: 
   - See club details
   - Status badge shows: YELLOW ⏱ "Pending Approval"
   - Auto-refresh should start (check console logs)
```

#### Verification Checklist:
```
✓ presidentEmail is correctly captured from user.email
✓ Club is created in database
✓ approvalStatus is set to PENDING (not undefined)
✓ active is set to true
✓ User is redirected to clubadmin-myclub.html
✓ Console shows no errors
```

---

### PART 2: Admin Reviews Clubs (Phase 2)

#### Test Case 2.1: View Pending Clubs
```
1. Log in as Admin
2. Go to: admin-clubs.html
3. Expected Result:
   - "Pending Club Registrations" section visible
   - Count shows number of pending clubs
   - Each club card shows: Logo, Name, Category, President Email
   - Buttons visible: [Approve] [Reject] [View Profile]
```

#### Test Case 2.2: Verify API Response
```
1. Open Browser DevTools → Network tab
2. Go to admin-clubs.html
3. Look for request: GET /api/clubs/admin/pending
4. Expected Response (JSON):
   [
     {
       "id": 1,
       "name": "Test Club",
       "category": "Academic",
       "presidentEmail": "clubadmin@univ.edu",
       "approvalStatus": "PENDING",
       "active": true,
       "logoUrl": "...",
       "coverUrl": "..."
     }
   ]
```

#### Verification Checklist:
```
✓ GET /api/clubs/admin/pending returns array of PENDING clubs
✓ Each club has approvalStatus: "PENDING"
✓ Club cards display correctly
✓ No 404 or 500 errors
```

---

### PART 3: Admin Approves Club (Phase 3)

#### Test Case 3.1: Approve Club
```
1. On admin-clubs.html, find the club you created
2. Click [Approve] button
3. Modal appears: "Approve Club - Are you sure?"
4. Click [Approve] in modal
5. Expected Result:
   - Club moves from "Pending" to "All Registered Clubs" section
   - Club now shows with GREEN status: ✓ "Active"
   - Club appears in approved clubs list
```

#### Test Case 3.2: Verify Approve API Call
```
1. Open DevTools → Network tab
2. Click [Approve] button
3. Look for: POST /api/clubs/{id}/approve
4. Expected Response (JSON):
   {
     "id": 1,
     "name": "Test Club",
     "category": "Academic",
     "presidentEmail": "clubadmin@univ.edu",
     "approvalStatus": "APPROVED",  ← Changed!
     "active": true,
     "logoUrl": "...",
     "coverUrl": "..."
   }
5. Database check:
   SELECT * FROM clubs WHERE id = 1;
   Should show: approvalStatus = APPROVED
```

#### Verification Checklist:
```
✓ POST /api/clubs/{id}/approve endpoint works
✓ Response includes approvalStatus: "APPROVED"
✓ Club removed from pending list
✓ Club appears in approved list
✓ Admin page automatically reloads both sections
✓ No errors in console
```

---

### PART 4: Club Admin Sees Update (Phase 4) - CRITICAL

#### Test Case 4.1: Auto-Refresh on My Club Page
```
Scenario: Club admin is on clubadmin-myclub.html while admin approves
Expected Behavior:
1. Club admin sees club with YELLOW badge
2. Admin approves club
3. After ≤15 seconds:
   - Badge updates to GREEN ✓
   - No page refresh needed (auto-update)

Steps to Test:
1. Open clubadmin-myclub.html (Club Admin logged in)
2. Open admin-clubs.html in NEW WINDOW (Admin logged in)
3. Keep both windows visible (split screen)
4. In admin window: Click [Approve]
5. Watch club-admin window for status update
6. Should update within 15 seconds automatically
```

#### Test Case 4.2: Verify Auto-Refresh Mechanism
```
1. On clubadmin-myclub.html
2. Open DevTools → Console
3. You should see periodic fetch calls like:
   GET /api/clubs/organizer/{userId}?t=1234567890
   
4. Watch network tab:
   - Should see this request every ~15 seconds
   - Response should include club data with current approvalStatus
   - When approvalStatus changes to APPROVED:
     └→ Console should log the update
     └→ renderClub() should be called
     └→ Badge should change color
```

#### Test Case 4.3: Browser Tab Focus Handler
```
1. On clubadmin-myclub.html
2. Switch to another tab (page becomes hidden)
3. Admin approves the club (in another window)
4. Switch back to MyClub tab (page becomes visible)
5. Expected: Immediate fetch of club data
   - Should refresh immediately (not wait 15 seconds)
   - Should show updated status right away
```

#### Verification Checklist:
```
✓ Auto-refresh runs every 15 seconds (check Network tab)
✓ API calls include timestamp (?t=...) to bypass cache
✓ Club data is fetched correctly
✓ Status badge updates when approvalStatus changes
✓ Visibility handler works (immediate refresh on tab focus)
✓ No console errors
✓ renderClub() is called after data fetch
✓ Page does NOT require manual refresh
```

---

## Debugging Guide

### If My Club Page NOT Updating After Approval:

#### Step 1: Check Auto-Refresh is Running
```javascript
// Run in browser console on clubadmin-myclub.html
// Should see output every 15 seconds
console.log('Auto-refresh is active');

// OR manually trigger refresh
// Call the function directly to test
checkUserClub()
```

#### Step 2: Verify API Response
```javascript
// Run in browser console
const userId = JSON.parse(localStorage.getItem('user')).id;
fetch(`/api/clubs/organizer/${userId}`)
  .then(r => r.json())
  .then(clubs => console.log('Clubs:', clubs))
  .catch(e => console.error('Error:', e));

// Check the response in console
// Should show club with approvalStatus: "APPROVED"
```

#### Step 3: Check Database Directly
```sql
-- Run in your database client
SELECT id, name, approvalStatus, presidentEmail, active 
FROM clubs 
WHERE presidentEmail = 'clubadmin@univ.edu';

-- Should show approvalStatus = 'APPROVED' or 'PENDING'
```

#### Step 4: Check HTML Elements
```javascript
// Run in browser console on clubadmin-myclub.html
// Check if elements are being updated
document.getElementById('approvalBadge').innerHTML
document.getElementById('clubTitle').textContent

// Should show current data
```

---

## Network Requests Flow

### When Club Admin Views My Club:

```
1. clubadmin-myclub.html loads
   ↓
2. JavaScript runs:
   let user = JSON.parse(localStorage.getItem('user'))
   ↓
3. init() → checkUserClub()
   ↓
4. First request: GET /api/clubs/organizer/{user.id}
   ├─→ If 404 or empty: Try fallback
   └─→ GET /api/clubs/organizer-email/{user.email}
   ↓
5. Response: Array of clubs (should be 1 club for club admin)
   ↓
6. renderClub(club) updates UI
   ├─→ Sets club name, email, category
   ├─→ Sets logo and cover images
   └─→ Sets status badge based on approvalStatus
   ↓
7. loadClubEvents(clubId) for events list
   ↓
8. startAutoRefresh() begins polling every 15 seconds
   └─→ Repeats steps 3-7 periodically
```

### When Admin Approves Club:

```
1. Admin clicks [Approve] button
   ↓
2. Modal confirmation appears
   ↓
3. Admin clicks [Approve] in modal
   ↓
4. POST /api/clubs/{id}/approve
   ├─→ ClubController.approveClub(id)
   ├─→ ClubService.approveClub(id)
   ├─→ Database: UPDATE clubs SET approvalStatus='APPROVED' WHERE id={id}
   └─→ Response: Updated club object with approvalStatus="APPROVED"
   ↓
5. Admin page JavaScript:
   ├─→ Calls loadPendingClubs()
   └─→ Calls loadApprovedClubs()
   ↓
6. Both sections refresh (club moved from pending to approved)
```

### Club Admin's Page Auto-Refresh Detects Update:

```
1. Every 15 seconds (or on tab focus):
   ↓
2. GET /api/clubs/organizer/{userId}
   ↓
3. Response returns club with approvalStatus="APPROVED"
   ↓
4. renderClub(club) is called
   ↓
5. Status badge: YELLOW → GREEN
   ├─→ HTML: <span class="...bg-secondary text-white">
   ├─→ Icon: verified
   └─→ Text: "Verified Club"
   ↓
6. UI automatically updates without page reload
```

---

## Common Issues & Fixes

### Issue 1: My Club Page Still Shows "Pending" After Admin Approval

**Possible Causes:**
1. Auto-refresh not running
2. Wrong user.id or email
3. API returning old data (caching)
4. Browser cache issue
5. Database not actually updated

**Fix Checklist:**
```
□ Clear localStorage: localStorage.clear()
□ Hard refresh page: Ctrl+Shift+R (or Cmd+Shift+R on Mac)
□ Re-login with club admin account
□ Check database directly to verify approvalStatus
□ Check Network tab to see if /api/clubs/organizer/ is being called
□ Check if response contains current approvalStatus
□ Open Developer Tools and manually run:
  checkUserClub()  // Force immediate refresh
```

### Issue 2: Admin Don't See Pending Clubs

**Possible Causes:**
1. No clubs with PENDING status in database
2. All clubs rejected/deleted
3. API endpoint not working
4. Database connection issue

**Fix Checklist:**
```
□ Check database: SELECT * FROM clubs WHERE approvalStatus='PENDING'
□ Verify /api/clubs/admin/pending endpoint exists
□ Test endpoint in Postman/curl
□ Check for 500 errors in server logs
□ Ensure ClubService.getPendingClubs() is implemented
```

### Issue 3: Can't Create Club (Form Submission Fails)

**Possible Causes:**
1. Required field missing (presidentEmail might be auto-filled incorrectly)
2. Image URLs invalid
3. API endpoint error
4. Validation error

**Fix Checklist:**
```
□ Check all required fields are filled
□ Verify image URLs are valid (open in new tab)
□ Check Network tab for failed POST /api/clubs request
□ Look for 400/422 error responses
□ Check server logs for validation errors
□ Verify presidentEmail is from logged-in user
```

---

## Key Code Locations

| Component | File | Location |
|-----------|------|----------|
| Club Entity | `Club.java` | `src/main/java/com/unievent/entity/` |
| Club Service | `ClubService.java` | `src/main/java/com/unievent/service/` |
| Club Controller | `ClubController.java` | `src/main/java/com/unievent/controller/` |
| Club Admin Form | `clubadmin-newclub.html` | `src/main/resources/static/` |
| My Club Page | `clubadmin-myclub.html` | `src/main/resources/static/` |
| Admin Clubs Page | `admin-clubs.html` | `src/main/resources/static/` |

---

## Performance Notes

- **Auto-refresh interval:** 15 seconds (configurable in JavaScript)
- **API requests:** Include `?t={timestamp}` to prevent caching
- **Browser optimization:** Uses `visibility` event to refresh immediately when tab comes into focus

---

## Security Considerations

✓ Club is linked to presidentEmail (user can only see/manage their own club)
✓ Approval status cannot be bypassed (stored server-side)
✓ Admin-only endpoints require proper authentication
✓ Database constraints ensure data integrity

---
