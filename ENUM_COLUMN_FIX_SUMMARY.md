# Venue Type Column Truncation Error - Fix Summary

## Problem
When publishing a new event as a club admin, the application was throwing a SQL error:
```
Server Error (400): Error adding venue: could not execute statement
[Data truncated for column 'venue_type' at row 1] [insert into venues (...) values (...)]
```

The column `venue_type` in the database was too small to hold the enum values being inserted.

## Root Cause
The Hibernate JPA entity mapping defined `@Enumerated(EnumType.STRING)` for enum fields but didn't explicitly specify a column length. The database defaulted to a small VARCHAR size (likely VARCHAR(255) or smaller), which was insufficient for storing enum values like `SPORTS_AREA` and `MEETING_ROOM`.

## Solution Applied

### 1. Entity Updates
All enum fields in JPA entities have been updated to include explicit `@Column(length = 20)` annotation:

#### **Venue.java**
```java
@Enumerated(EnumType.STRING)
@Column(length = 20)
private VenueType venueType = VenueType.AUDITORIUM;
```

#### **Event.java**
```java
@Enumerated(EnumType.STRING)
@Column(length = 20)
private EventStatus status = EventStatus.DRAFT;
```

#### **User.java**
```java
@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 20)
private Role role;

@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 20)
private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;
```

#### **Club.java**
```java
@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 20)
private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;
```

#### **Registration.java**
```java
@Enumerated(EnumType.STRING)
@Column(length = 20)
private RegistrationStatus status = RegistrationStatus.PENDING;
```

#### **Booking.java**
```java
@Enumerated(EnumType.STRING)
@Column(length = 20)
private BookingStatus status;
```

### 2. Column Sizes Verified
All enum values have been analyzed for length (20 characters is sufficient):
- `VenueType`: AUDITORIUM, HALL, LAB, SPORTS_AREA (12 chars), MEETING_ROOM (12 chars), OTHER, OUTDOOR, SPORTS
- `EventStatus`: DRAFT, PENDING, APPROVED, COMPLETED (9 chars), CANCELLED (9 chars)
- `Role`: Multiple values including ADMIN_CLUB (10 chars)
- `ApprovalStatus`: PENDING (7 chars), APPROVED (8 chars), REJECTED (8 chars)
- `RegistrationStatus`: WAITING_LIST (12 chars)
- `BookingStatus`: PENDING (7 chars), APPROVED (8 chars), REJECTED (8 chars), CANCELLED (9 chars)

## How to Apply This Fix

### Option 1: Automatic (Recommended for Development)
The application uses `spring.jpa.hibernate.ddl-auto=update`, which means:
1. Rebuild and restart the application
2. Hibernate will automatically create the new columns with proper sizes
3. No manual SQL needed

**Steps:**
```bash
# 1. Build the project
mvn clean compile

# 2. Stop the running application (if any)
# Kill process on port 8084 or use Ctrl+C

# 3. Start the application
mvn spring-boot:run

# 4. The database schema will auto-update
# 5. Try publishing a new event again
```

### Option 2: Manual SQL Execution
If you prefer to manually apply the changes or in a production environment:

```sql
-- Execute this SQL script against your database
-- File: fix_venue_type_column.sql

-- Fix venues table venue_type column
ALTER TABLE venues MODIFY COLUMN venue_type VARCHAR(20) DEFAULT 'AUDITORIUM';

-- Fix events table status column
ALTER TABLE events MODIFY COLUMN status VARCHAR(20) DEFAULT 'DRAFT';

-- Fix users table role and approval_status columns
ALTER TABLE users MODIFY COLUMN role VARCHAR(20) NOT NULL;
ALTER TABLE users MODIFY COLUMN approval_status VARCHAR(20) DEFAULT 'PENDING' NOT NULL;

-- Fix clubs table approval_status column
ALTER TABLE clubs MODIFY COLUMN approval_status VARCHAR(20) DEFAULT 'PENDING' NOT NULL;

-- Fix registrations table status column
ALTER TABLE registrations MODIFY COLUMN status VARCHAR(20) DEFAULT 'PENDING';

-- Fix bookings table status column
ALTER TABLE bookings MODIFY COLUMN status VARCHAR(20);
```

## File Changes Made

### Java Entity Files Modified:
1. `src/main/java/com/unievent/entity/Venue.java`
2. `src/main/java/com/unievent/entity/Event.java`
3. `src/main/java/com/unievent/entity/User.java`
4. `src/main/java/com/unievent/entity/Club.java`
5. `src/main/java/com/unievent/entity/Registration.java`
6. `src/main/java/com/unievent/entity/Booking.java`

### New SQL Script:
- `fix_venue_type_column.sql` - Contains all necessary ALTER TABLE statements

## Testing

After applying the fix:

1. **Rebuild the project**
   ```bash
   mvn clean compile -DskipTests
   ```

2. **Restart the application**
   ```bash
   mvn spring-boot:run
   ```

3. **Test the event creation**
   - Log in as Club Admin
   - Navigate to "New Event" page
   - Create a new event with a venue that has type "SPORTS_AREA" or "MEETING_ROOM"
   - Publish the event
   - The error should no longer occur

## Prevention

For future enum fields in JPA entities, always include:
```java
@Enumerated(EnumType.STRING)
@Column(length = 20)  // Always specify this!
private YourEnumType enumField;
```

This ensures consistency and prevents similar truncation errors.

## Build Status
✅ **BUILD SUCCESS** - All Java code compiles without errors after applying these fixes.

---
**Last Updated:** March 23, 2026
**Spring Boot & Hibernate Configuration:** `spring.jpa.hibernate.ddl-auto=update`
