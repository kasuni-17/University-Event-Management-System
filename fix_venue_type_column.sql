-- =============================================================================
-- Database Migration: Fix Enum Column Truncation Issues
-- =============================================================================
-- This script addresses the issue where enum values were being truncated
-- in the database due to insufficient column size definition.
-- The columns are being modified to VARCHAR(20) to safely accommodate all enum values.
--
-- Affected enums and their longest values:
-- - VenueType: "SPORTS_AREA", "MEETING_ROOM" (max 12 chars)
-- - EventStatus: "COMPLETED", "CANCELLED" (max 9 chars)
-- - Role: "ORGANIZER", "ADMIN_CLUB" (max 11 chars)
-- - ApprovalStatus: "PENDING", "APPROVED", "REJECTED" (max 9 chars)
-- - RegistrationStatus: "WAITING_LIST" (max 12 chars)
-- - BookingStatus: "APPROVED", "REJECTED", "CANCELLED" (max 9 chars)
-- =============================================================================

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

-- =============================================================================
-- Migration Complete
-- =============================================================================
