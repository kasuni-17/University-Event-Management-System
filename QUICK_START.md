# UniEvent Email & Approval System - Quick Start Guide

## 🚀 Get Started in 5 Minutes

Follow these steps to deploy the email and approval system to your UniEvent platform.

---

## Step 1: Configure Gmail (5 minutes)

### 1.1 Enable 2-Step Verification
1. Go to: https://myaccount.google.com/security
2. Look for "2-Step Verification" section
3. Click "Enable"
4. Follow Google's verification process
5. Once enabled, you'll see "App passwords" option

### 1.2 Generate App Password
1. In Security settings, find "App passwords"
2. Select "Mail" and "Windows Computer" (or your OS)
3. Click "Generate"
4. Google will show a 16-character password like: `abcd efgh ijkl mnop`
5. **Copy this password** (you'll need it in Step 2)

### 1.3 Whitelist Email
1. Go to Gmail account settings
2. Add your university email as a forwarding address (optional)

---

## Step 2: Update Configuration (2 minutes)

### 2.1 Edit `application.properties`

Located at: `src/main/resources/application.properties`

Add these lines:

```properties
# Email Configuration (Gmail SMTP)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-gmail@gmail.com
spring.mail.password=abcd efgh ijkl mnop

# Email Properties
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# Application Settings (for email templates)
app.name=UniEvent
app.url=http://localhost:8080
```

**Replace:**
- `your-gmail@gmail.com` → Your actual Gmail address
- `abcd efgh ijkl mnop` → The 16-character password from Step 1.2

### 2.2 Verify Configuration

Test email configuration by running a simple test:

```bash
# Build the project
mvn clean package

# Run the application
mvn spring-boot:run
```

---

## Step 3: Database Migration (3 minutes)

### 3.1 Apply Schema Changes

Connect to your MySQL database and run:

```sql
-- Add approval tracking to users table
ALTER TABLE users 
ADD COLUMN approval_date DATETIME NULL,
ADD COLUMN rejection_reason VARCHAR(255) NULL,
ADD COLUMN created_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Add approval tracking to clubs table
ALTER TABLE clubs 
ADD COLUMN approval_date DATETIME NULL,
ADD COLUMN rejection_reason TEXT NULL,
ADD COLUMN created_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
ADD COLUMN created_by_user_id BIGINT NULL,
ADD FOREIGN KEY (created_by_user_id) REFERENCES users(id);

-- Add approval tracking to events table
ALTER TABLE events 
ADD COLUMN approval_date DATETIME NULL,
ADD COLUMN rejection_reason TEXT NULL,
ADD COLUMN created_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Add indexes for performance
CREATE INDEX idx_users_role_approval ON users(role, approval_status);
CREATE INDEX idx_clubs_approval ON clubs(approval_status, active);
CREATE INDEX idx_events_status ON events(status, start_time);
```

### 3.2 Verify Changes

```sql
-- Check users table
DESC users;

-- Check clubs table
DESC clubs;

-- Check events table
DESC events;
```

All new columns should be visible.

---

## Step 4: Deploy Updated Code (5 minutes)

### 4.1 Copy New Files

The following Java files have been created/updated:

```
✅ NEW: EmailService.java
✅ NEW: AdminController.java
✅ NEW: UserStatusController.java
✅ NEW: EventFilterController.java
✅ NEW: ApprovalRequestDTO.java
✅ NEW: UserRegistrationStatusDTO.java
✅ NEW: UserClubMembershipStatusDTO.java
✅ NEW: EventFilterDTO.java
✅ MODIFIED: User.java
✅ MODIFIED: Club.java
✅ MODIFIED: Event.java
✅ MODIFIED: UserService.java
✅ MODIFIED: ClubService.java
✅ MODIFIED: EventService.java
```

Copy all files to your project.

### 4.2 Rebuild Project

```bash
# Clean build
mvn clean install

# If build succeeds, you're ready!
```

---

## Step 5: Test the System (10 minutes)

### 5.1 Start the Application

```bash
mvn spring-boot:run
```

Navigate to: `http://localhost:8080`

### 5.2 Test Workflow #1: Club Admin Approval

1. **Create a club admin account**
   - Sign up with role "CLUB_ADMIN"
   - Account should be INACTIVE (status shows PENDING)
   - Try to login → Should show "Account Pending Approval"

2. **Navigate to Admin Panel**
   - Go to: `http://localhost:8080/admin/pending-users`
   - Should see the new club admin

3. **Approve the Account**
   - Click "Approve" button
   - Check the personal Gmail inbox (not university email)
   - You should receive approval email with subject:
     ```
     ✅ Your Club Admin Account Has Been Approved - UniEvent
     ```

4. **Verify Account is Active**
   - Club admin can now login and create clubs

### 5.3 Test Workflow #2: Club Approval

1. **Create a Club** (as club admin)
   - Fill in club details
   - Submit → Club is HIDDEN (PENDING status)
   - Club doesn't appear on student club list

2. **Navigate to Admin Panel**
   - Go to: `http://localhost:8080/admin/pending-clubs`
   - Should see the new club

3. **Approve the Club**
   - Click "Approve" button
   - Email sent to club admin's personal Gmail
   - Club now appears on student club list

### 5.4 Test Workflow #3: Event Approval

1. **Create an Event** (as club admin)
   - Select your approved club
   - Fill event details
   - Submit → Event is HIDDEN (PENDING status)

2. **Approve the Event**
   - Go to: `http://localhost:8080/admin/pending-events`
   - Click "Approve"
   - Email sent to club admin
   - Event appears on student event list

### 5.5 Test Workflow #4: Event Filtering

1. **View Events Page**
   - Click "Upcoming Events" button
   - Should see only approved future events

2. **Click "Past Events"**
   - Should see only completed past events
   - Check status indicators

### 5.6 Test Workflow #5: Registration Status

1. **Register for an Event**
   - Click "Register Now" button
   - Receive confirmation email
   - Button changes to red "✓ Registered" (disabled)

2. **Join a Club**
   - Click "Join Club" button
   - Button changes to red "✓ Joined" (disabled)

---

## Common Errors & Fixes

### Error: "Email failed to send"

**Cause:** Gmail credentials incorrect

**Fix:**
```properties
# 1. Verify app password format (should be 16 characters with spaces)
spring.mail.password=abcd efgh ijkl mnop

# 2. Verify Gmail address is correct
spring.mail.username=your-gmail@gmail.com

# 3. Verify 2-Step Verification is enabled
```

### Error: "Database column doesn't exist"

**Cause:** Migration script wasn't run

**Fix:**
```bash
# 1. Run the migration SQL again
# 2. Restart the application
# 3. Check MySQL logs for errors
```

### Error: "Account still shows PENDING after approval"

**Cause:** Browser cache or session issue

**Fix:**
```bash
# 1. Clear browser cache
# 2. Logout and login again
# 3. Check database directly:
SELECT id, approval_status, active FROM users WHERE id = 1;
```

### Error: "Events not showing in upcoming list"

**Cause:** Events are PENDING (not approved yet)

**Fix:**
```bash
# 1. Check event status in admin panel
# 2. Make sure to approve event first
# 3. Verify event's start_time is in future
```

---

## API Testing with cURL

### Test Email Configuration

```bash
# Check if EmailService is working
curl -X GET http://localhost:8080/admin/pending-club-admins
```

### Test Club Admin Approval

```bash
curl -X POST http://localhost:8080/api/admin/approve-club-admin/1 \
  -H "Content-Type: application/json"
```

### Test Event Filtering

```bash
curl http://localhost:8080/api/events/upcoming
curl http://localhost:8080/api/events/past
```

### Test User Status

```bash
curl http://localhost:8080/api/user-status/is-registered/1/1
curl http://localhost:8080/api/user-status/is-joined/1/1
```

---

## Frontend Integration Checklist

- [ ] Copy `events-page-example.html` → Update your events page
- [ ] Copy `clubs-page-example.html` → Update your clubs page
- [ ] Copy `my-clubs-page-example.html` → Create My Clubs page
- [ ] Update user localStorage to store `userId` on login
- [ ] Test API endpoints from frontend JavaScript
- [ ] Update button states based on registration/membership
- [ ] Implement approval admin dashboard
- [ ] Style buttons with red color for "already registered/joined" state

---

## Deployment Checklist

- [ ] Gmail configuration added to `application.properties`
- [ ] Database migration scripts executed
- [ ] New Java files copied to project
- [ ] Modified Java files updated
- [ ] Project builds without errors (`mvn clean install`)
- [ ] Application starts without exceptions
- [ ] All workflows tested in Step 5
- [ ] Frontend pages updated with new JavaScript
- [ ] Email notifications received correctly

---

## Security Considerations

### Before Production:

1. **Store credentials securely**
   ```bash
   # Use environment variables instead of properties file
   export SPRING_MAIL_USERNAME=your-gmail@gmail.com
   export SPRING_MAIL_PASSWORD=your-app-password
   ```

2. **Enable HTTPS**
   ```properties
   server.ssl.enabled=true
   server.ssl.key-store=...
   ```

3. **Add rate limiting**
   - Prevent approval spam
   - Limit registration requests

4. **Add authentication**
   - `@PreAuthorize("hasRole('ADMIN')")` on admin endpoints
   - `@PreAuthorize("isAuthenticated()")` on user endpoints

5. **Add logging**
   - Log all approvals/rejections
   - Monitor email failures

---

## Monitoring & Maintenance

### Daily Checks

- [ ] Check email sending logs
- [ ] Monitor database for pending items
- [ ] Verify email inbox for bounces

### Weekly Tasks

- [ ] Review pending approvals
- [ ] Check database performance
- [ ] Clear old email logs

### Monthly Tasks

- [ ] Analyze approval metrics
- [ ] Review user feedback
- [ ] Update email templates if needed

---

## Next Steps

After successful deployment:

1. **Configure Production Email**
   - Use Office 365 or SendGrid instead of Gmail
   - Set up email templates in CMS

2. **Add Advanced Features**
   - Approval notifications to admin email
   - Bulk approval operations
   - Scheduled status updates

3. **Improve UI/UX**
   - Add confirmation modals
   - Show approval status badges
   - Create admin dashboard

4. **Scale for Production**
   - Add caching layer
   - Implement message queue for emails
   - Add email retry logic

---

## Support

**If something doesn't work:**

1. Check the **Error Logs**
   ```bash
   tail -f logs/spring.log
   ```

2. Check **Database Directly**
   ```sql
   SELECT * FROM users WHERE id = 1;
   SELECT * FROM clubs WHERE approval_status = 'PENDING';
   ```

3. Check **Email Configuration**
   - Gmail credentials correct?
   - 2-Step Verification enabled?
   - App password used (not main password)?

4. Consult **Full Documentation**
   - See: `EMAIL_AND_APPROVAL_SYSTEM_GUIDE.md`
   - See: `API_REFERENCE.md`

---

## Success Indicators ✅

Your system is working correctly when:

- ✅ New club admin accounts require approval
- ✅ Unapproved items don't appear to students
- ✅ Approval emails arrive within 10 seconds
- ✅ Event filtering shows correct items
- ✅ Registration buttons show correct states
- ✅ Admin can approve/reject with reasons
- ✅ No database errors in logs

---

## Time to Production

| Task | Time | Status |
|------|------|--------|
| Gmail Setup | 5 min | ⏱️ |
| Configure App | 2 min | ⏱️ |
| Database Migration | 3 min | ⏱️ |
| Deploy Code | 5 min | ⏱️ |
| Test System | 10 min | ⏱️ |
| Update Frontend | 15 min | ⏱️ |
| **Total** | **40 min** | ✅ |

**Ready in under 1 hour!** 🎉

---

## Congratulations! 🎊

Your UniEvent platform now has:

✅ Email notifications for all approvals
✅ Admin approval workflows
✅ Event filtering system
✅ User status tracking
✅ Professional HTML email templates
✅ API endpoints for all features
✅ Frontend examples ready to integrate

**You're all set to manage approvals and notifications like a pro!**

---

**Questions?** Check the full documentation or review the code comments.

**Last Updated:** January 2024  
**Version:** 1.0
